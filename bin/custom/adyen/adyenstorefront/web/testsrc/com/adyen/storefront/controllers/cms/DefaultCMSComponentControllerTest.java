/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package com.adyen.storefront.controllers.cms;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import com.adyen.storefront.controllers.ControllerConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;


/**
 * Unit test for {@link DefaultCMSComponentController}
 */
@UnitTest
public class DefaultCMSComponentControllerTest
{
	private static final String COMPONENT_UID = "componentUid";
	private static final String TEST_COMPONENT_UID = "componentUID";
	private static final String TEST_TYPE_CODE = "myTypeCode";
	private static final String TEST_TYPE_VIEW = ControllerConstants.Views.Cms.ComponentPrefix
			+ StringUtils.lowerCase(TEST_TYPE_CODE);
	private static final String TEST_VALUE = "myValue";
	private static final String TEST_PROPERTY = "myProperty";

	private DefaultCMSComponentController defaultCMSComponentController;
	private SimpleCMSComponentModel component;

	@Mock
	private Model model;
	@Mock
	private DefaultCMSComponentService cmsComponentService;
	@Mock
	private DefaultModelService modelService;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		component = new SimpleCMSComponentModel();

		defaultCMSComponentController = new DefaultCMSComponentController();
		defaultCMSComponentController.setCmsComponentService(cmsComponentService);
		defaultCMSComponentController.setModelService(modelService);

		given(cmsComponentService.getEditorProperties(component)).willReturn(Collections.singletonList(TEST_PROPERTY));
		given(modelService.getAttributeValue(component, TEST_PROPERTY)).willReturn(TEST_VALUE);
	}

	@Test
	public void testRenderComponent() throws Exception
	{
		final String viewName = defaultCMSComponentController.handleComponent(request, response, model, component);
		verify(model, Mockito.times(1)).addAttribute(TEST_PROPERTY, TEST_VALUE);
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(null);
		given(request.getParameter(COMPONENT_UID)).willReturn(null);
		defaultCMSComponentController.handleGet(request, response, model);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound2() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(null);
		given(request.getParameter(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(null);
		defaultCMSComponentController.handleGet(request, response, model);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound3() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(null);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willThrow(new CMSItemNotFoundException(""));
		defaultCMSComponentController.handleGet(request, response, model);
	}

	@Test
	public void testRenderComponentUid() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(component);
		final String viewName = defaultCMSComponentController.handleComponent(request, response, model, component);
		verify(model, Mockito.times(1)).addAttribute(TEST_PROPERTY, TEST_VALUE);
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}
}
