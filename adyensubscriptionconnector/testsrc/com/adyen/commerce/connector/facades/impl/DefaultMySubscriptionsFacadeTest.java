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

import com.adyen.commerce.connector.dto.NormalizedSubscriptionStatus;
import com.adyen.commerce.connector.dto.SubscriptionCancellation;
import com.adyen.commerce.connector.facades.data.SubscriptionDisplayState;
import com.adyen.commerce.connector.facades.data.SubscriptionEntryData;
import com.adyen.commerce.connector.model.BillingSubscriptionRefModel;
import com.adyen.commerce.connector.service.SubscriptionBillingService;

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

		when(userService.getCurrentUser()).thenReturn(customer);
		when(userService.isAnonymousUser(customer)).thenReturn(false);

		when(sessionService.executeInLocalViewWithParams(any(), any(SessionExecutionBody.class)))
				.thenAnswer(invocation -> {
					invocation.<SessionExecutionBody> getArgument(1).execute();
					return null;
				});
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
