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
package com.adyen.storefront.interceptors.i18n;

import de.hybris.platform.commerceservices.i18n.LanguageResolver;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import com.adyen.storefront.interceptors.beforecontroller.SetLanguageBeforeControllerHandler;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
public class SetLanguageBeforeControllerHandlerTest
{
	@InjectMocks
	private final SetLanguageBeforeControllerHandler beforeControllerHandler = new SetLanguageBeforeControllerHandler();

	@Mock
	private LanguageResolver languageResolver;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private HttpServletRequest request;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCallForNonGetRequest() throws Exception
	{
		testCallForNonGetRequest("PUT");
		testCallForNonGetRequest("Put");
		testCallForNonGetRequest("put");

		testCallForNonGetRequest("POST");
		testCallForNonGetRequest("Post");
		testCallForNonGetRequest("post");

		testCallForNonGetRequest("DELETE");
		testCallForNonGetRequest("Delete");
		testCallForNonGetRequest("delete");
	}

	@Test
	public void testCallForAnyGetRequest() throws Exception
	{
		testCallForGetRequest("GET");
		testCallForGetRequest("Get");
		testCallForGetRequest("get");
	}

	private void testCallForNonGetRequest(final String nonGet) throws Exception
	{
		BDDMockito.given(request.getMethod()).willReturn(nonGet);
		BDDMockito.given(request.getParameter(SetLanguageBeforeControllerHandler.DEFAULT_LANG_PARAM)).willReturn("dummy");

		beforeControllerHandler.beforeController(request, null, null);

		Mockito.verifyZeroInteractions(commonI18NService);
		Mockito.verifyZeroInteractions(languageResolver);

		Mockito.reset(languageResolver, request, commonI18NService);
	}

	private void testCallForGetRequest(final String getMethod) throws Exception
	{
		final LanguageModel lang = Mockito.mock(LanguageModel.class);

		BDDMockito.given(languageResolver.getLanguage(Mockito.anyString())).willReturn(lang);
		BDDMockito.given(request.getMethod()).willReturn(getMethod);
		BDDMockito.given(request.getParameter(SetLanguageBeforeControllerHandler.DEFAULT_LANG_PARAM)).willReturn("dummy");

		beforeControllerHandler.beforeController(request, null, null);

		Mockito.verify(languageResolver).getLanguage("dummy");
		Mockito.verify(commonI18NService).setCurrentLanguage(lang);

		Mockito.reset(languageResolver, request, commonI18NService);
	}
}
