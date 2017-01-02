/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.adyen.core.checkout.flow.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.acceleratorservices.checkout.flow.CheckoutFlowStrategy;
import de.hybris.platform.acceleratorservices.enums.CheckoutFlowEnum;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SiteCheckoutFlowStrategyTest
{
	private CheckoutFlowStrategy strategy;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private CMSSiteService cmsSiteService;


	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		strategy = initStrategy();
	}

	protected CheckoutFlowStrategy initStrategy()
	{
		final SiteCheckoutFlowStrategy siteStrategy = new SiteCheckoutFlowStrategy();

		final CheckoutFlowStrategy oneStub = new CheckoutFlowStrategy()
		{
			@Override
			public CheckoutFlowEnum getCheckoutFlow()
			{
				throw new OneStrategyCallException();
			}
		};

		final CheckoutFlowStrategy twoStub = new CheckoutFlowStrategy()
		{
			@Override
			public CheckoutFlowEnum getCheckoutFlow()
			{
				throw new TwoStrategyCallException();
			}
		};

		final CheckoutFlowStrategy defaultStub = new CheckoutFlowStrategy()
		{
			@Override
			public CheckoutFlowEnum getCheckoutFlow()
			{
				throw new DefaultStrategyCallException();
			}
		};

		final Map<String, CheckoutFlowStrategy> mapping = new HashMap<String, CheckoutFlowStrategy>();
		mapping.put("one", oneStub);
		mapping.put("two", twoStub);
		siteStrategy.setSiteMappings(mapping);
		siteStrategy.setDefaultStrategy(defaultStub);
		siteStrategy.setCmsSiteService(cmsSiteService);

		return siteStrategy;
	}


	@Test(expected = NullPointerException.class)
	public void testHasNoCurrentSite()
	{
		given(cmsSiteService.getCurrentSite()).willReturn(null);
		strategy.getCheckoutFlow();
	}

	@Test(expected = NullPointerException.class)
	public void testHasCurrentSiteWithNullUid()
	{
		BDDMockito.given(cmsSiteService.getCurrentSite().getUid()).willReturn(null);
		strategy.getCheckoutFlow();
	}

	@Test(expected = OneStrategyCallException.class)
	public void testGetExistingMapping()
	{
		BDDMockito.given(cmsSiteService.getCurrentSite().getUid()).willReturn("one");
		strategy.getCheckoutFlow();
	}


	@Test(expected = DefaultStrategyCallException.class)
	public void testGetDefaultMapping()
	{
		BDDMockito.given(cmsSiteService.getCurrentSite().getUid()).willReturn("not_existsing");
		strategy.getCheckoutFlow();
	}


	static class DefaultStrategyCallException extends RuntimeException
	{
		//
	}

	static class OneStrategyCallException extends RuntimeException
	{
		//
	}

	static class TwoStrategyCallException extends RuntimeException
	{
		//
	}
}
