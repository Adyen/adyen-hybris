/*
 *                        ######
 *                        ######
 *  ############    ####( ######  #####. ######  ############   ############
 *  #############  #####( ######  #####. ######  #####  ######  #####  ######
 *  ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 *  ###### ######  #####( ######  #####. ######  #####          #####  ######
 *  #############  #############  #############  #############  #####  ######
 *   ############   ############  #############   ############  #####  ######
 *                                       ######
 *                                #############
 *                                ############
 *
 *  Adyen Hybris Extension
 *
 *  Copyright (c) 2026 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.commerce.connector.facades.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adyen.commerce.connector.activation.BillingActivationAttemptService;
import com.adyen.commerce.connector.context.SubscriptionBaseStoreSelectorStrategy;
import com.adyen.commerce.connector.dto.CancelReason;
import com.adyen.commerce.connector.dto.NormalizedSubscriptionStatus;
import com.adyen.commerce.connector.dto.SubscriptionCancellation;
import com.adyen.commerce.connector.facades.MySubscriptionsFacade;
import com.adyen.commerce.connector.facades.data.SubscriptionDisplayState;
import com.adyen.commerce.connector.facades.data.SubscriptionEntryData;
import com.adyen.commerce.connector.facades.data.SubscriptionOverviewData;
import com.adyen.commerce.connector.model.BillingActivationAttemptModel;
import com.adyen.commerce.connector.model.BillingSubscriptionRefModel;
import com.adyen.commerce.connector.dto.AdyenTokenHandle;
import com.adyen.commerce.connector.dto.BillingCustomerRef;
import com.adyen.commerce.connector.dto.CardMetadata;
import com.adyen.commerce.connector.dto.RecurringProcessingModel;
import com.adyen.commerce.connector.dto.TokenImportRequest;
import com.adyen.commerce.connector.enums.BillingPlatform;
import com.adyen.commerce.connector.registry.SubscriptionBillingConnectorRegistry;
import com.adyen.commerce.connector.token.AdyenTokenHandleFactory;
import com.adyen.commerce.facades.AdyenStoredCardsFacade;
import com.adyen.commerce.connector.service.SubscriptionBillingService;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Default implementation.
 *
 * <h3>How ownership is enforced</h3>
 * <p>By predicate, in the query, every time — never by loading a row and comparing its customer afterwards.
 * The two are not equivalent under a mistake: a comparison that is forgotten, or short-circuited by a null,
 * silently returns somebody else's subscription, whereas a query that names the customer returns nothing.
 * The customer always comes from the session and is never accepted as an argument.</p>
 *
 * <h3>Why every operation runs inside the order's own store</h3>
 * <p>The connectors read their API key, site and gateway account from the base store in the session.
 * {@code Customer} is global across stores in SAP, so a shopper signed in to one storefront can hold
 * subscriptions bought on another; without putting the order's store in context, a cancellation would be
 * sent to the wrong billing account — where it either fails or, worse, names a subscription that exists
 * there.</p>
 */
public class DefaultMySubscriptionsFacade implements MySubscriptionsFacade
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultMySubscriptionsFacade.class);

	private UserService userService;
	private FlexibleSearchService flexibleSearchService;
	private ProductService productService;
	private SubscriptionBillingService subscriptionBillingService;
	private SessionService sessionService;
	private BaseSiteService baseSiteService;
	private SubscriptionBillingConnectorRegistry connectorRegistry;
	private AdyenTokenHandleFactory tokenHandleFactory;
	private AdyenStoredCardsFacade storedCardsFacade;

	@Override
	public SubscriptionOverviewData getSubscriptionsForCurrentCustomer()
	{
		final SubscriptionOverviewData overview = new SubscriptionOverviewData();
		final CustomerModel customer = currentCustomer();
		if (customer == null)
		{
			return overview;
		}

		final List<SubscriptionEntryData> entries = new ArrayList<>();
		for (final BillingSubscriptionRefModel ref : findSubscriptions(customer))
		{
			entries.add(toEntry(ref));
		}
		overview.setSubscriptions(entries);
		overview.setOrdersAwaitingSetup(findOrdersAwaitingSetup(customer));
		return overview;
	}

	@Override
	public boolean cancelForCurrentCustomer(final String code)
	{
		final CustomerModel customer = currentCustomer();
		if (customer == null || StringUtils.isBlank(code))
		{
			return false;
		}

		final BillingSubscriptionRefModel ref = findOwnSubscription(customer, code);
		if (ref == null)
		{
			// One line, no distinction: whether the code does not exist or belongs to somebody else is
			// exactly what a caller probing for codes wants to learn.
			LOG.info("No subscription matching the requested code belongs to the current customer; refusing.");
			return false;
		}

		// Re-derived rather than trusted from the form. The page that offered the button may have been
		// rendered minutes ago, and the subscription may have ended or been cancelled since.
		if (!displayState(ref).isCancellable())
		{
			LOG.info("Subscription '{}' is not in a state that can be cancelled by the shopper; refusing.", code);
			return false;
		}

		try
		{
			cancelInStoreContext(ref);
			return true;
		}
		catch (final RuntimeException e)
		{
			// The shopper is told it did not work and can try again; the detail stays in the log, because a
			// platform's own error text is not something to put in front of a customer.
			LOG.error("Could not cancel subscription '{}' for the current customer.", code, e);
			return false;
		}
	}

	@Override
	public boolean changePaymentMethodForCurrentCustomer(final String subscriptionCode,
			final String storedPaymentMethodId)
	{
		final CustomerModel customer = currentCustomer();
		if (customer == null || StringUtils.isBlank(subscriptionCode) || StringUtils.isBlank(storedPaymentMethodId))
		{
			return false;
		}

		final BillingSubscriptionRefModel ref = findOwnSubscription(customer, subscriptionCode);
		if (ref == null)
		{
			LOG.info("No subscription matching the requested code belongs to the current customer; refusing.");
			return false;
		}
		// Proof of concept, and the restriction is real rather than a placeholder: the import needs no
		// network transaction id, which a token vaulted earlier cannot supply, and Recurly's adapter refuses
		// exactly that. Widening this means answering how Recurly gets one, not deleting this check.
		if (ref.getPlatform() != BillingPlatform.CHARGEBEE)
		{
			LOG.info("Changing the payment method is only supported on Chargebee; subscription '{}' is on {}.",
					subscriptionCode, ref.getPlatform());
			return false;
		}
		if (StringUtils.isBlank(ref.getExternalCustomerId()))
		{
			LOG.warn("Subscription '{}' has no external customer id; the payment source has nothing to attach to.",
					subscriptionCode);
			return false;
		}

		try
		{
			importInStoreContext(customer, ref, storedPaymentMethodId);
			return true;
		}
		catch (final RuntimeException e)
		{
			LOG.error("Could not change the payment method for subscription '{}'.", subscriptionCode, e);
			return false;
		}
	}

	/**
	 * Imports the chosen token as the customer's payment source, with the subscription's own store in
	 * context so the connector reads the right credentials.
	 *
	 * <p>The card metadata is whatever the vault listing gave us, and may be absent — the import sends
	 * {@code last4} and expiry only for display on the platform, so a null simply means Chargebee shows
	 * less. The reference id it derives is deterministic, so repeating this with the same card is a no-op
	 * there rather than a second payment source.</p>
	 */
	protected void importInStoreContext(final CustomerModel customer, final BillingSubscriptionRefModel ref,
			final String storedPaymentMethodId)
	{
		final BaseStoreModel store = storeOf(ref);
		if (store == null)
		{
			throw new IllegalStateException("Subscription has no originating store; its connector's "
					+ "credentials cannot be selected safely");
		}

		final Map<String, Object> sessionParameters = Collections.singletonMap(
				SubscriptionBaseStoreSelectorStrategy.CURRENT_SUBSCRIPTION_BASE_STORE, store);
		sessionService.executeInLocalViewWithParams(sessionParameters, new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				try
				{
					final AdyenTokenHandle token = tokenHandleFactory.createForStoredToken(customer, store,
							storedPaymentMethodId, cardMetadataOf(customer, storedPaymentMethodId));
					final BillingCustomerRef customerRef = new BillingCustomerRef(ref.getPlatform(),
							ref.getExternalCustomerId());
					connectorRegistry.getConnector(ref.getPlatform())
							.importAdyenToken(new TokenImportRequest(customerRef, token,
									RecurringProcessingModel.SUBSCRIPTION, null));
				}
				catch (final Exception e)
				{
					throw new IllegalStateException(e);
				}
			}
		});
	}

	/**
	 * Display metadata for the chosen card, read back from the Adyen vault.
	 *
	 * <p>Best effort on purpose: it is only shown on the billing platform, and failing the whole change
	 * because a cosmetic lookup failed would be the wrong trade.</p>
	 */
	protected CardMetadata cardMetadataOf(final CustomerModel customer, final String storedPaymentMethodId)
	{
		try
		{
			return storedCardsFacade.getStoredCardsPageDataForCurrentCustomer().getStoredCards().stream()
					.filter(card -> storedPaymentMethodId.equals(card.getId()))
					.findFirst()
					.map(card -> new CardMetadata(null, card.getLastFour(), card.getHolderName(),
							expiry(card.getExpiryMonth(), card.getExpiryYear()), null))
					.orElse(null);
		}
		catch (final RuntimeException e)
		{
			LOG.debug("Could not read card metadata for the chosen stored token; importing without it.", e);
			return null;
		}
	}

	/** Chargebee wants the month and year separately; the handle carries them joined as MM/YYYY. */
	protected String expiry(final String month, final String year)
	{
		return StringUtils.isAnyBlank(month, year) ? null
				: StringUtils.leftPad(month, 2, '0') + "/" + (year.length() == 2 ? "20" + year : year);
	}

	// --- reading ---

	protected List<BillingSubscriptionRefModel> findSubscriptions(final CustomerModel customer)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(
				"SELECT {pk} FROM {BillingSubscriptionRef} WHERE {customer} = ?customer "
						+ "ORDER BY {currentPeriodEnd} DESC, {pk} DESC");
		query.addQueryParameter("customer", customer);
		return flexibleSearchService.<BillingSubscriptionRefModel> search(query).getResult();
	}

	/**
	 * The one subscription with this code that belongs to this customer, or {@code null}.
	 *
	 * <p>Both conditions are in the query on purpose — see the class javadoc.</p>
	 */
	protected BillingSubscriptionRefModel findOwnSubscription(final CustomerModel customer, final String code)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(
				"SELECT {pk} FROM {BillingSubscriptionRef} WHERE {customer} = ?customer AND {code} = ?code");
		query.addQueryParameter("customer", customer);
		query.addQueryParameter("code", code);
		final List<BillingSubscriptionRefModel> result = flexibleSearchService
				.<BillingSubscriptionRefModel> search(query).getResult();
		return result.isEmpty() ? null : result.get(0);
	}

	/**
	 * Orders this shopper paid for whose subscription was given up on.
	 *
	 * <p>Only the dead letter, not every failed attempt: an attempt that is still being retried is expected
	 * to succeed shortly and telling the shopper about it would be alarming and premature.</p>
	 */
	protected List<String> findOrdersAwaitingSetup(final CustomerModel customer)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(
				"SELECT {a.pk} FROM {BillingActivationAttempt AS a JOIN Order AS o ON {a.order} = {o.pk}} "
						+ "WHERE {o.user} = ?customer AND {a.status} = ?status ORDER BY {a.lastAttemptAt} DESC");
		query.addQueryParameter("customer", customer);
		query.addQueryParameter("status", BillingActivationAttemptService.STATUS_DEAD_LETTER);

		final List<String> orderCodes = new ArrayList<>();
		for (final BillingActivationAttemptModel attempt : flexibleSearchService
				.<BillingActivationAttemptModel> search(query).getResult())
		{
			final AbstractOrderModel order = attempt.getOrder();
			if (order != null && StringUtils.isNotBlank(order.getCode()))
			{
				orderCodes.add(order.getCode());
			}
		}
		return orderCodes;
	}

	// --- mapping ---

	protected SubscriptionEntryData toEntry(final BillingSubscriptionRefModel ref)
	{
		final SubscriptionEntryData entry = new SubscriptionEntryData();
		entry.setCode(ref.getCode());
		entry.setProductName(displayName(ref));
		final SubscriptionDisplayState state = displayState(ref);
		entry.setState(state);
		entry.setEffectiveDate(effectiveDate(ref, state));
		entry.setQuantity(ref.getQuantity());

		final AbstractOrderModel order = ref.getOrder();
		if (order != null)
		{
			entry.setOrderCode(order.getCode());
			entry.setOrderDate(order.getDate());
			entry.setPaymentMethodSummary(cardSummary(order.getPaymentInfo()));
		}
		return entry;
	}

	/**
	 * What the shopper reads instead of a plan code.
	 *
	 * <p>{@code getProductForCode} throws rather than returning null when the product is gone, and throws a
	 * different exception again when two catalogue versions are visible at once, so the fallback belongs in
	 * a catch. A subscription outlives the catalogue entry it was sold from, and it must still be listed and
	 * still be cancellable when that happens.</p>
	 */
	protected String displayName(final BillingSubscriptionRefModel ref)
	{
		final String productCode = ref.getProductCode();
		if (StringUtils.isBlank(productCode))
		{
			return null;
		}
		try
		{
			final ProductModel product = productService.getProductForCode(productCode);
			return StringUtils.defaultIfBlank(product.getName(), productCode);
		}
		catch (final RuntimeException e)
		{
			LOG.debug("Product '{}' could not be resolved for a subscription listing; showing the code.",
					productCode, e);
			return productCode;
		}
	}

	/**
	 * The single place that decides what a shopper is told about one subscription.
	 *
	 * <p>The first question is not the status but whether any platform has ever confirmed this row.
	 * {@code platformUpdatedAt} answers it, because only a reconciliation writes it — {@code lastSyncedAt}
	 * would not, since it is deliberately cleared to hurry the sweep along after a cancellation whose
	 * follow-up read failed, and reading that as "never confirmed" is how a subscription the shopper had
	 * just stopped came to be labelled as still being set up.</p>
	 */
	protected SubscriptionDisplayState displayState(final BillingSubscriptionRefModel ref)
	{
		if (storeOf(ref) == null)
		{
			// Nothing can be said, and more importantly nothing can be done: without the store there are no
			// credentials with which to reach the platform this belongs to.
			return SubscriptionDisplayState.UNAVAILABLE;
		}
		if (ref.getPlatformUpdatedAt() == null)
		{
			return SubscriptionDisplayState.SETTING_UP;
		}

		final boolean ending = Boolean.TRUE.equals(ref.getCancelAtPeriodEnd());
		final NormalizedSubscriptionStatus status = normalizedStatus(ref.getStatus());
		if (status == null)
		{
			return SubscriptionDisplayState.UNAVAILABLE;
		}
		return switch (status)
		{
			case ACTIVE -> ending ? SubscriptionDisplayState.ENDING : SubscriptionDisplayState.ACTIVE;
			// Dunning outranks the pending end on both adapters, so this pair has to exist: without it a
			// shopper who had already stopped their subscription would be told their provider will retry.
			case PAST_DUE -> ending ? SubscriptionDisplayState.PAST_DUE_ENDING : SubscriptionDisplayState.PAST_DUE;
			case PENDING -> startsInTheFuture(ref)
					? SubscriptionDisplayState.STARTING_SOON
					: SubscriptionDisplayState.SETTING_UP;
			case PAUSED -> SubscriptionDisplayState.PAUSED;
			case CANCELLED, EXPIRED, FAILED -> SubscriptionDisplayState.ENDED;
			case UNKNOWN -> SubscriptionDisplayState.UNAVAILABLE;
		};
	}

	protected Date effectiveDate(final BillingSubscriptionRefModel ref, final SubscriptionDisplayState state)
	{
		return switch (state)
		{
			case ACTIVE, ENDING, PAST_DUE, PAST_DUE_ENDING, ENDED -> ref.getCurrentPeriodEnd();
			case STARTING_SOON -> ref.getCurrentPeriodStart();
			case SETTING_UP, PAUSED, UNAVAILABLE -> null;
		};
	}

	protected boolean startsInTheFuture(final BillingSubscriptionRefModel ref)
	{
		final Date start = ref.getCurrentPeriodStart();
		return start != null && start.after(new Date());
	}

	/**
	 * The stored status as a normalized value, or {@code null} when the literal is not one.
	 *
	 * <p>The column is a string, so it can hold a value this vocabulary no longer has — from an older
	 * release, or from a platform that grew a state we have not mapped. Guessing at it would put a wrong
	 * word in front of a shopper; {@code null} sends the row to {@code UNAVAILABLE}, which says nothing and
	 * offers nothing.</p>
	 */
	protected NormalizedSubscriptionStatus normalizedStatus(final String stored)
	{
		if (StringUtils.isBlank(stored))
		{
			return null;
		}
		try
		{
			return NormalizedSubscriptionStatus.valueOf(stored);
		}
		catch (final IllegalArgumentException e)
		{
			LOG.warn("Subscription carries the unrecognised status literal '{}'; showing it as unavailable.", stored);
			return null;
		}
	}

	protected String cardSummary(final PaymentInfoModel paymentInfo)
	{
		return paymentInfo == null ? null : paymentInfo.getAdyenCardSummary();
	}

	// --- writing ---

	protected void cancelInStoreContext(final BillingSubscriptionRefModel ref)
	{
		final BaseStoreModel store = storeOf(ref);
		if (store == null)
		{
			throw new IllegalStateException("Subscription has no originating store; its connector's "
					+ "credentials cannot be selected safely");
		}

		final Map<String, Object> sessionParameters = Collections.singletonMap(
				SubscriptionBaseStoreSelectorStrategy.CURRENT_SUBSCRIPTION_BASE_STORE, store);
		sessionService.executeInLocalViewWithParams(sessionParameters, new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				final AbstractOrderModel order = ref.getOrder();
				final BaseSiteModel site = order == null ? null : order.getSite();
				if (site != null)
				{
					// false: catalogue versions are not needed to read a store's connector credentials, and
					// activating them is the expensive half of this call.
					baseSiteService.setCurrentBaseSite(site, false);
				}
				try
				{
					subscriptionBillingService.cancel(ref,
							SubscriptionCancellation.endOfPeriod(CancelReason.REQUESTED_BY_CUSTOMER));
				}
				catch (final Exception e)
				{
					// Rewrapped because SessionExecutionBody cannot throw a checked exception; the caller's
					// catch is what turns this into "no" on screen rather than an error page.
					throw new IllegalStateException(e);
				}
			}
		});
	}

	protected BaseStoreModel storeOf(final BillingSubscriptionRefModel ref)
	{
		final AbstractOrderModel order = ref.getOrder();
		return order == null ? null : order.getStore();
	}

	/**
	 * The signed-in customer, or {@code null} for anyone else.
	 *
	 * <p>A guest is deliberately included in "anyone else" by virtue of never reaching this page: the
	 * accelerator confines {@code /my-account/**} to {@code ROLE_CUSTOMERGROUP}. Activation now refuses to
	 * start a subscription for a guest at all, precisely because this page is the only way to stop one.</p>
	 */
	protected CustomerModel currentCustomer()
	{
		if (userService.isAnonymousUser(userService.getCurrentUser()))
		{
			return null;
		}
		return userService.getCurrentUser() instanceof CustomerModel customer ? customer : null;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	public void setSubscriptionBillingService(final SubscriptionBillingService subscriptionBillingService)
	{
		this.subscriptionBillingService = subscriptionBillingService;
	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	public void setConnectorRegistry(final SubscriptionBillingConnectorRegistry connectorRegistry)
	{
		this.connectorRegistry = connectorRegistry;
	}

	public void setTokenHandleFactory(final AdyenTokenHandleFactory tokenHandleFactory)
	{
		this.tokenHandleFactory = tokenHandleFactory;
	}

	public void setStoredCardsFacade(final AdyenStoredCardsFacade storedCardsFacade)
	{
		this.storedCardsFacade = storedCardsFacade;
	}
}
