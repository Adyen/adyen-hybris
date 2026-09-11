/*
 *                        ######
 *                        ######
 *  ############    ####( ######  #####. ######  ############   ############
 *  #############  #####( ######  #####. ######  #############  #############
 *         ######  #####( ######  #####. ######  #####  ######  #####  ######
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.adyen.commerce.connector.dto.AdyenTokenHandle;
import com.adyen.commerce.connector.dto.BillingPaymentMethodRef;
import com.adyen.commerce.connector.dto.ConnectorCapabilities;
import com.adyen.commerce.connector.dto.NormalizedSubscriptionStatus;
import com.adyen.commerce.connector.dto.PaymentMethodChangeOutcome;
import com.adyen.commerce.connector.dto.PaymentMethodChangeScope;
import com.adyen.commerce.connector.dto.TokenImportStyle;
import com.adyen.commerce.connector.dto.SubscriptionCancellation;
import com.adyen.commerce.connector.facades.data.PaymentMethodChangeResult;
import com.adyen.commerce.connector.facades.data.SubscriptionDisplayState;
import com.adyen.commerce.connector.facades.data.SubscriptionOverviewData;
import com.adyen.commerce.connector.facades.data.SubscriptionEntryData;
import com.adyen.commerce.connector.model.BillingSubscriptionRefModel;
import com.adyen.commerce.connector.enums.BillingPlatform;
import com.adyen.commerce.connector.registry.SubscriptionBillingConnectorRegistry;
import com.adyen.commerce.connector.service.SubscriptionBillingService;
import com.adyen.commerce.connector.spi.SubscriptionBillingConnector;
import com.adyen.commerce.connector.token.AdyenTokenHandleFactory;
import com.adyen.commerce.facades.AdyenStoredCardsFacade;
import com.adyen.model.checkout.StoredPaymentMethodResource;
import com.adyen.v6.dto.StoredCardsPageData;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * What the shopper is told, and what they are allowed to do about it.
 */
@UnitTest
public class DefaultMySubscriptionsFacadeTest
{
	@Mock
	private UserService userService;
	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private ProductService productService;
	@Mock
	private SubscriptionBillingService subscriptionBillingService;
	@Mock
	private SessionService sessionService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private CustomerModel customer;
	@Mock
	private AdyenStoredCardsFacade storedCardsFacade;
	@Mock
	private SubscriptionBillingConnectorRegistry connectorRegistry;
	@Mock
	private AdyenTokenHandleFactory tokenHandleFactory;
	@Mock
	private SubscriptionBillingConnector connector;

	private DefaultMySubscriptionsFacade facade;

	@Before
	public void setUp()
	{
		MockitoAnnotations.openMocks(this);

		facade = new DefaultMySubscriptionsFacade();
		facade.setUserService(userService);
		facade.setFlexibleSearchService(flexibleSearchService);
		facade.setProductService(productService);
		facade.setSubscriptionBillingService(subscriptionBillingService);
		facade.setSessionService(sessionService);
		facade.setBaseSiteService(baseSiteService);
		facade.setStoredCardsFacade(storedCardsFacade);
		facade.setConnectorRegistry(connectorRegistry);
		facade.setTokenHandleFactory(tokenHandleFactory);

		when(userService.getCurrentUser()).thenReturn(customer);
		when(userService.isAnonymousUser(customer)).thenReturn(false);

		// Returns what the body returned. Swallowing it made the facade see no applied scope, which is
		// exactly the case it now refuses rather than guesses at - so the stub has to model the real
		// contract or the test proves the opposite of what it claims.
		when(sessionService.executeInLocalViewWithParams(any(), any(SessionExecutionBody.class)))
				.thenAnswer(invocation -> invocation.<SessionExecutionBody> getArgument(1).execute());
	}

	// --- the state table ---

	/**
	 * The first question is not the status but whether any platform has ever confirmed the row. Only a
	 * reconciliation writes platformUpdatedAt; lastSyncedAt is deliberately cleared to hurry the sweep along
	 * after a cancellation whose follow-up read failed, and reading that as "never confirmed" is how a
	 * subscription the shopper had just stopped came to be labelled as still being set up.
	 */
	@Test
	public void saysNothingAboutASubscriptionNoPlatformHasConfirmedYet()
	{
		final BillingSubscriptionRefModel ref = ref(NormalizedSubscriptionStatus.ACTIVE);
		when(ref.getPlatformUpdatedAt()).thenReturn(null);

		assertEquals(SubscriptionDisplayState.SETTING_UP, facade.displayState(ref));
		assertNull(facade.effectiveDate(ref, SubscriptionDisplayState.SETTING_UP));
		assertFalse(SubscriptionDisplayState.SETTING_UP.isCancellable());
	}

	@Test
	public void anActiveSubscriptionRenewsAndCanBeStopped()
	{
		assertEquals(SubscriptionDisplayState.ACTIVE, facade.displayState(ref(NormalizedSubscriptionStatus.ACTIVE)));
		assertTrue(SubscriptionDisplayState.ACTIVE.isCancellable());
	}

	/**
	 * Both adapters keep a stopped subscription ACTIVE until its term runs out, and say so through
	 * cancelAtPeriodEnd. Without this branch the page would go on promising a renewal that will not happen.
	 */
	@Test
	public void anActiveSubscriptionAlreadyStoppedIsShownAsEndingAndOffersNoButton()
	{
		final BillingSubscriptionRefModel ref = ref(NormalizedSubscriptionStatus.ACTIVE);
		when(ref.getCancelAtPeriodEnd()).thenReturn(Boolean.TRUE);

		assertEquals(SubscriptionDisplayState.ENDING, facade.displayState(ref));
		assertFalse(SubscriptionDisplayState.ENDING.isCancellable());
	}

	/**
	 * Dunning outranks the pending end on both adapters, so a shopper who has already stopped their
	 * subscription must not be told their provider will retry.
	 */
	@Test
	public void anUnpaidInvoiceOnAnAlreadyStoppedSubscriptionIsItsOwnState()
	{
		final BillingSubscriptionRefModel ref = ref(NormalizedSubscriptionStatus.PAST_DUE);
		when(ref.getCancelAtPeriodEnd()).thenReturn(Boolean.TRUE);

		assertEquals(SubscriptionDisplayState.PAST_DUE_ENDING, facade.displayState(ref));
	}

	/** The state shoppers most often want to leave from, so it keeps its button. */
	@Test
	public void aPaymentProblemStillLetsTheShopperStopTheSubscription()
	{
		assertEquals(SubscriptionDisplayState.PAST_DUE, facade.displayState(ref(NormalizedSubscriptionStatus.PAST_DUE)));
		assertTrue(SubscriptionDisplayState.PAST_DUE.isCancellable());
	}

	@Test
	public void aFutureDatedSubscriptionSaysWhenItStarts()
	{
		final BillingSubscriptionRefModel ref = ref(NormalizedSubscriptionStatus.PENDING);
		final Date tomorrow = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));
		when(ref.getCurrentPeriodStart()).thenReturn(tomorrow);

		assertEquals(SubscriptionDisplayState.STARTING_SOON, facade.displayState(ref));
		assertEquals(tomorrow, facade.effectiveDate(ref, SubscriptionDisplayState.STARTING_SOON));
	}

	@Test
	public void aPendingSubscriptionWithNoStartDateIsStillJustBeingSetUp()
	{
		assertEquals(SubscriptionDisplayState.SETTING_UP,
				facade.displayState(ref(NormalizedSubscriptionStatus.PENDING)));
	}

	@Test
	public void anEndedSubscriptionSaysSoWhicheverWordThePlatformUsed()
	{
		assertEquals(SubscriptionDisplayState.ENDED, facade.displayState(ref(NormalizedSubscriptionStatus.EXPIRED)));
		assertEquals(SubscriptionDisplayState.ENDED, facade.displayState(ref(NormalizedSubscriptionStatus.CANCELLED)));
		assertEquals(SubscriptionDisplayState.ENDED, facade.displayState(ref(NormalizedSubscriptionStatus.FAILED)));
	}

	/**
	 * The status column is a plain string, so it can hold a value this vocabulary no longer has. Guessing at
	 * it would put a wrong word in front of a shopper.
	 */
	@Test
	public void anUnrecognisedStatusLiteralSaysNothingAndOffersNothing()
	{
		final BillingSubscriptionRefModel ref = ref(NormalizedSubscriptionStatus.ACTIVE);
		when(ref.getStatus()).thenReturn("transferred");

		assertEquals(SubscriptionDisplayState.UNAVAILABLE, facade.displayState(ref));
		assertFalse(SubscriptionDisplayState.UNAVAILABLE.isCancellable());
	}

	/**
	 * Without the originating store there are no credentials with which to reach the platform, so nothing
	 * can be said and nothing can be done — but the row still appears, because it is billing somebody.
	 */
	@Test
	public void aSubscriptionWhoseStoreCannotBeDeterminedIsShownWithoutAButton()
	{
		final BillingSubscriptionRefModel ref = ref(NormalizedSubscriptionStatus.ACTIVE);
		when(ref.getOrder()).thenReturn(null);

		assertEquals(SubscriptionDisplayState.UNAVAILABLE, facade.displayState(ref));
	}

	/**
	 * A reference created before the public identifier existed carries none until essential data has been
	 * imported. Offering a button that posts an empty code reads to the shopper as a subscription they
	 * cannot cancel, rather than as a deployment step somebody skipped.
	 */
	@Test
	public void offersNoButtonForARowThatHasNoPublicIdentifierYet()
	{
		final BillingSubscriptionRefModel ref = ref(NormalizedSubscriptionStatus.ACTIVE);
		when(ref.getCode()).thenReturn(null);

		final SubscriptionEntryData entry = facade.toEntry(ref);

		assertEquals(SubscriptionDisplayState.ACTIVE, entry.getState());
		assertFalse("a row with no code cannot be acted on, so it must not look as though it can",
				entry.isCancellable());
	}

	// --- ownership and cancelling ---

	@Test
	public void cancellingAsksForTheEndOfThePaidPeriod() throws Exception
	{
		givenSingleResult(ref(NormalizedSubscriptionStatus.ACTIVE));

		assertTrue(facade.cancelForCurrentCustomer("code-1"));

		verify(subscriptionBillingService).cancel(any(), any(SubscriptionCancellation.class));
	}

	/**
	 * The lookup names the customer in the query rather than comparing after loading. A comparison that is
	 * forgotten returns somebody else's subscription; a query that names the customer returns nothing.
	 */
	@Test
	public void refusesACodeThatIsNotThisCustomersWithoutSayingWhy() throws Exception
	{
		givenNoResults();

		assertFalse(facade.cancelForCurrentCustomer("someone-elses-code"));

		verify(subscriptionBillingService, never()).cancel(any(), any());
	}

	/**
	 * The page that offered the button may have been rendered minutes ago. The state is re-derived rather
	 * than trusted from the form.
	 */
	@Test
	public void refusesToCancelSomethingThatIsNoLongerInACancellableState() throws Exception
	{
		final BillingSubscriptionRefModel ref = ref(NormalizedSubscriptionStatus.ACTIVE);
		when(ref.getCancelAtPeriodEnd()).thenReturn(Boolean.TRUE);
		givenSingleResult(ref);

		assertFalse(facade.cancelForCurrentCustomer("code-1"));

		verify(subscriptionBillingService, never()).cancel(any(), any());
	}

	@Test
	public void anAnonymousVisitorSeesAnEmptyPageRatherThanAnError()
	{
		final UserModel anonymous = mock(UserModel.class);
		when(userService.getCurrentUser()).thenReturn(anonymous);
		when(userService.isAnonymousUser(anonymous)).thenReturn(true);

		assertTrue(facade.getSubscriptionsForCurrentCustomer().isEmpty());
		assertFalse(facade.cancelForCurrentCustomer("code-1"));
	}

	// --- naming ---

	/**
	 * A subscription outlives the catalogue entry it was sold from, and getProductForCode throws rather than
	 * returning null when the product is gone — so the fallback has to be in a catch.
	 */
	@Test
	public void fallsBackToTheProductCodeWhenTheProductIsNoLongerInTheCatalogue()
	{
		final BillingSubscriptionRefModel ref = ref(NormalizedSubscriptionStatus.ACTIVE);
		when(ref.getProductCode()).thenReturn("SUB-1");
		when(productService.getProductForCode("SUB-1")).thenThrow(new IllegalArgumentException("gone"));

		assertEquals("SUB-1", facade.displayName(ref));
	}

	@Test
	public void neverShowsThePlatformsPlanCodeAsAName()
	{
		final BillingSubscriptionRefModel ref = ref(NormalizedSubscriptionStatus.ACTIVE);
		when(ref.getProductCode()).thenReturn(null);
		when(ref.getPlanCode()).thenReturn("test-subscription-plan-EUR-Monthly");

		assertNull(facade.displayName(ref));
	}

	// --- helpers ---

	// --- changing the payment method ---

	/**
	 * The access check that did not exist. The token id is a request parameter, and until this refused it
	 * nothing between the form and the platform compared it against the cards this shopper actually holds:
	 * the select on the page is a convenience, not a control, and a request need not come from that page.
	 * What stood in for a check was Adyen declining to retrieve somebody else's token — a property of one
	 * platform's import call, not a promise every connector makes.
	 */
	@Test
	public void refusesAPaymentMethodThatIsNotOnThisShoppersVaultListing() throws Exception
	{
		givenSubscriptionOnAPlatformThatSupportsTheChange(NormalizedSubscriptionStatus.ACTIVE);
		givenVaultHolding("card-mine");

		assertEquals(PaymentMethodChangeResult.FAILED,
				facade.changePaymentMethodForCurrentCustomer("code-1", "card-somebody-elses"));

		verify(subscriptionBillingService, never()).changePaymentMethod(any(), any());
	}

	/** The same listing, read successfully, containing the card: the one case that goes through. */
	@Test
	public void acceptsAPaymentMethodTheShopperActuallyHolds() throws Exception
	{
		givenSubscriptionOnAPlatformThatSupportsTheChange(NormalizedSubscriptionStatus.ACTIVE);
		givenVaultHolding("card-mine");

		// The wording the shopper reads follows what the platform reported, not what was advertised.
		assertEquals(PaymentMethodChangeResult.CHANGED_ALL_SUBSCRIPTIONS,
				facade.changePaymentMethodForCurrentCustomer("code-1", "card-mine"));

		verify(subscriptionBillingService).changePaymentMethod(any(), any());
	}

	/**
	 * A vault listing that could not be read says nothing about ownership, so it is a failure and not a
	 * refusal. The distinction is the point: both used to end as absent card metadata and a request that
	 * carried on to the platform regardless.
	 */
	@Test
	public void refusesWhenTheVaultListingCannotBeRead() throws Exception
	{
		givenSubscriptionOnAPlatformThatSupportsTheChange(NormalizedSubscriptionStatus.ACTIVE);
		when(storedCardsFacade.getStoredCardsPageDataForCurrentCustomer())
				.thenThrow(new IllegalStateException("Adyen is unreachable"));

		assertEquals(PaymentMethodChangeResult.FAILED,
				facade.changePaymentMethodForCurrentCustomer("code-1", "card-mine"));

		verify(subscriptionBillingService, never()).changePaymentMethod(any(), any());
	}

	/**
	 * Re-derived from the row rather than trusted from the form, exactly as the cancellation is. A page
	 * rendered minutes ago can offer a control for a subscription that has since ended.
	 */
	@Test
	public void refusesToChangeThePaymentMethodOfASubscriptionThatHasEnded() throws Exception
	{
		givenSubscriptionOnAPlatformThatSupportsTheChange(NormalizedSubscriptionStatus.EXPIRED);
		givenVaultHolding("card-mine");

		assertEquals(PaymentMethodChangeResult.FAILED,
				facade.changePaymentMethodForCurrentCustomer("code-1", "card-mine"));

		verify(subscriptionBillingService, never()).changePaymentMethod(any(), any());
	}

	/**
	 * Past due is the state shoppers most often want to fix, so it must be offered even though the same row
	 * is not something they can usefully cancel any more.
	 */
	@Test
	public void offersThePaymentMethodChangeOnASubscriptionThatIsPastDue() throws Exception
	{
		givenSubscriptionOnAPlatformThatSupportsTheChange(NormalizedSubscriptionStatus.PAST_DUE);
		givenVaultHolding("card-mine");

		assertEquals(PaymentMethodChangeResult.CHANGED_ALL_SUBSCRIPTIONS,
				facade.changePaymentMethodForCurrentCustomer("code-1", "card-mine"));

		verify(subscriptionBillingService).changePaymentMethod(any(), any());
	}

	/**
	 * The whole point of the capability: a platform that cannot do this is answered with its own result,
	 * which the page turns into a sentence rather than into "please try again". No platform name is
	 * involved anywhere on this path.
	 */
	@Test
	public void tellsTheShopperWhenThePlatformCannotChangeThePaymentMethodAtAll() throws Exception
	{
		givenSubscriptionOnAPlatformThatSupportsTheChange(NormalizedSubscriptionStatus.ACTIVE);
		when(connector.capabilities()).thenReturn(capabilities(PaymentMethodChangeScope.NOT_SUPPORTED));
		givenVaultHolding("card-mine");

		assertEquals(PaymentMethodChangeResult.NOT_SUPPORTED_HERE,
				facade.changePaymentMethodForCurrentCustomer("code-1", "card-mine"));

		verify(subscriptionBillingService, never()).changePaymentMethod(any(), any());
	}

	/** And the page does not offer a control it knows cannot work. */
	@Test
	public void doesNotOfferTheChangeWhenNoSubscriptionOnThePageSupportsIt() throws Exception
	{
		givenSubscriptionOnAPlatformThatSupportsTheChange(NormalizedSubscriptionStatus.ACTIVE);
		when(connector.capabilities()).thenReturn(capabilities(PaymentMethodChangeScope.NOT_SUPPORTED));

		// The offer alone. Going through getSubscriptionsForCurrentCustomer would also run the
		// orders-awaiting-setup query, which this fixture's single search stub cannot answer.
		final SubscriptionOverviewData overview = new SubscriptionOverviewData();
		facade.applyPaymentMethodChangeOffer(overview, customer);

		assertNull(overview.getPaymentMethodSubscriptionCode());
		assertEquals(PaymentMethodChangeScope.NOT_SUPPORTED, overview.getPaymentMethodChangeScope());
	}

	/**
	 * The case that took a deployed package and a user report to find. The row bills perfectly well and its
	 * platform supports the change; it simply predates the public identifier, so the form cannot be built.
	 * Withholding it is right — submitting it could only be refused — but it must not be silent, because
	 * from outside it is indistinguishable from the feature not being deployed.
	 */
	@Test
	public void doesNotOfferTheChangeForARowWithNoPublicCode() throws Exception
	{
		givenSubscriptionOnAPlatformThatSupportsTheChange(NormalizedSubscriptionStatus.ACTIVE);
		when(findTheOnlySubscription().getCode()).thenReturn(null);

		final SubscriptionOverviewData overview = new SubscriptionOverviewData();
		facade.applyPaymentMethodChangeOffer(overview, customer);

		assertNull(overview.getPaymentMethodSubscriptionCode());
		assertEquals(PaymentMethodChangeScope.NOT_SUPPORTED, overview.getPaymentMethodChangeScope());
	}

	/** The row the single-result search stub is returning, so a test can change its mind about it. */
	private BillingSubscriptionRefModel findTheOnlySubscription()
	{
		return flexibleSearchService.<BillingSubscriptionRefModel> search(mock(FlexibleSearchQuery.class))
				.getResult().get(0);
	}

	private void givenSubscriptionOnAPlatformThatSupportsTheChange(final NormalizedSubscriptionStatus status)
			throws Exception
	{
		final BillingSubscriptionRefModel ref = ref(status);
		when(ref.getPlatform()).thenReturn(BillingPlatform.CHARGEBEE);
		when(ref.getExternalCustomerId()).thenReturn("cb-customer-1");
		givenSingleResult(ref);
		when(connectorRegistry.getConnector(BillingPlatform.CHARGEBEE)).thenReturn(connector);
		when(connector.capabilities()).thenReturn(capabilities(PaymentMethodChangeScope.CUSTOMER));
		// A real handle rather than a mock: the request record validates its arguments, so a null here would
		// fail the happy path for a reason that has nothing to do with what these tests are about.
		when(tokenHandleFactory.createForStoredToken(any(), any(), any(), any()))
				.thenReturn(new AdyenTokenHandle("MERCHANT", "shopper-1", "card-mine", null, null));
		when(subscriptionBillingService.changePaymentMethod(any(), any()))
				.thenReturn(new PaymentMethodChangeOutcome(
						new BillingPaymentMethodRef(BillingPlatform.CHARGEBEE, "pm_1"),
						PaymentMethodChangeScope.CUSTOMER));
	}

	private static ConnectorCapabilities capabilities(final PaymentMethodChangeScope scope)
	{
		return new ConnectorCapabilities(false, true, false, true, true, TokenImportStyle.SLASH_JOINED, scope);
	}

	private void givenVaultHolding(final String... storedPaymentMethodIds)
	{
		final StoredCardsPageData page = mock(StoredCardsPageData.class);
		final List<StoredPaymentMethodResource> cards = new java.util.ArrayList<>();
		for (final String id : storedPaymentMethodIds)
		{
			final StoredPaymentMethodResource card = new StoredPaymentMethodResource();
			card.setId(id);
			cards.add(card);
		}
		when(page.getStoredCards()).thenReturn(cards);
		when(storedCardsFacade.getStoredCardsPageDataForCurrentCustomer()).thenReturn(page);
	}

	private static BillingSubscriptionRefModel ref(final NormalizedSubscriptionStatus status)
	{
		final BillingSubscriptionRefModel ref = mock(BillingSubscriptionRefModel.class);
		when(ref.getCode()).thenReturn("code-1");
		when(ref.getStatus()).thenReturn(status.name());
		// Present by default: "no platform has confirmed this yet" is its own state and every other test
		// would otherwise land in it.
		when(ref.getPlatformUpdatedAt()).thenReturn(new Date());
		final OrderModel order = mock(OrderModel.class);
		when(order.getStore()).thenReturn(mock(BaseStoreModel.class));
		when(ref.getOrder()).thenReturn(order);
		return ref;
	}

	@SuppressWarnings("unchecked")
	private void givenSingleResult(final BillingSubscriptionRefModel ref)
	{
		final SearchResult<BillingSubscriptionRefModel> result = mock(SearchResult.class);
		when(result.getResult()).thenReturn(List.of(ref));
		when(flexibleSearchService.<BillingSubscriptionRefModel> search(any(FlexibleSearchQuery.class)))
				.thenReturn(result);
	}

	@SuppressWarnings("unchecked")
	private void givenNoResults()
	{
		final SearchResult<BillingSubscriptionRefModel> result = mock(SearchResult.class);
		when(result.getResult()).thenReturn(List.of());
		when(flexibleSearchService.<BillingSubscriptionRefModel> search(any(FlexibleSearchQuery.class)))
				.thenReturn(result);
	}
}
