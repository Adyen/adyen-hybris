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
package com.adyen.facades.flow.impl;

import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.enums.CheckoutFlowEnum;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Collections;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;


public class DefaultCheckoutFlowFacadeTest extends BaseCommerceBaseTest
{
	@Resource
	private CheckoutFlowFacade checkoutFlowFacade;

	@Resource
	private CMSSiteService cmsSiteService;

	@Mock
	private HttpServletRequest request;

	@Before
	public void prepareRequest()
	{
		BDDMockito.given(request.getHeader("User-Agent")).willReturn(
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.77 Safari/535.7");
	}

	/**
	 * fallback to default
	 */
	@Test
	public void testGetDefaultFlowForUndefinedSiteUid()
	{
		new CMSSiteAwareTestExecutor("dummy", cmsSiteService)
		{
			@Override
			protected void performTest()
			{
				Assert.assertEquals(CheckoutFlowEnum.MULTISTEP, checkoutFlowFacade.getCheckoutFlow());
			}
		}.run();
	}

	@Test
	public void testGetDefaultFlowForApparelUk()
	{
		new CMSSiteAwareTestExecutor("apparel-uk", cmsSiteService)
		{
			@Override
			protected void performTest()
			{
				Assert.assertEquals(CheckoutFlowEnum.MULTISTEP, checkoutFlowFacade.getCheckoutFlow());
			}
		}.run();
	}

	@Test
	public void testGetDefaultFlowForApparelDe()
	{
		new CMSSiteAwareTestExecutor("apparel-de", cmsSiteService)
		{
			@Override
			protected void performTest()
			{
				Assert.assertEquals(CheckoutFlowEnum.MULTISTEP, checkoutFlowFacade.getCheckoutFlow());
			}
		}.run();
	}

	/**
	 * decision is based on : multi step on new customer (i.e. customer does not have full data required for checkout),
	 * single step otherwise
	 * 
	 */
	@Test
	public void testGetDefaultFlowForElectronics()
	{
		new CMSSiteAwareTestExecutor("electronics", cmsSiteService)
		{
			@Override
			protected void performTest()
			{
				Assert.assertEquals(CheckoutFlowEnum.MULTISTEP, checkoutFlowFacade.getCheckoutFlow());
			}
		}.run();
	}



	protected abstract static class CMSSiteAwareTestExecutor implements Runnable
	{
		private CMSSiteModel siteBefore;
		private final CMSSiteModel dummySite;
		private final CMSSiteService cmsSiteService;
		private final BaseStoreModel dummyStore;

		CMSSiteAwareTestExecutor(final String siteUid, final CMSSiteService cmsSiteService)
		{
			this.cmsSiteService = cmsSiteService;
			this.dummySite = new CMSSiteModel();
			this.dummyStore = new BaseStoreModel();
			this.dummySite.setUid(siteUid);
			this.dummyStore.setPaymentProvider("Mockup");
			this.dummyStore.setUid(siteUid);
			this.dummySite.setStores(Collections.singletonList(dummyStore));
		}

		private void prepare()
		{
			siteBefore = cmsSiteService.getCurrentSite();
			cmsSiteService.setCurrentSite(dummySite);
		}

		public void unPrepare()
		{
			if (siteBefore != null)
			{
				cmsSiteService.setCurrentSite(siteBefore);
			}
		}

		@Override
		public void run()
		{
			try
			{
				prepare();
				performTest();
			}
			finally
			{
				unPrepare();
			}

		}

		protected abstract void performTest();
	}
}
