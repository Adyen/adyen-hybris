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
package com.adyen.storefront.controllers.cms;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorcms.enums.NavigationBarMenuLayout;
import de.hybris.platform.acceleratorcms.model.components.NavigationBarComponentModel;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService;
import com.adyen.storefront.controllers.ControllerConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import junit.framework.Assert;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


/**
 * Unit test for {@link NavigationBarComponentController}
 */
@UnitTest
public class NavigationBarComponentControllerTest
{
	private static final String COMPONENT_UID = "componentUid";
	private static final String TEST_COMPONENT_UID = "componentUID";
	private static final String TEST_TYPE_CODE = "myTypeCode";
	private static final String TEST_TYPE_VIEW = ControllerConstants.Views.Cms.ComponentPrefix
			+ StringUtils.lowerCase(TEST_TYPE_CODE);
	private static final String DROP_DOWN_LAYOUT = "dropDownLayout";
	private static final String COMPONENT = "component";

	private NavigationBarComponentController navigationBarComponentController;

	@Mock
	private NavigationBarComponentModel navigationBarComponentModel;

	@Mock
	private Model model;
	@Mock
	private DefaultCMSComponentService cmsComponentService;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;

	private final NavigationBarMenuLayout navigationBarMenuLayout = NavigationBarMenuLayout.LEFT_EDGE;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		navigationBarComponentController = new NavigationBarComponentController();
		navigationBarComponentController.setCmsComponentService(cmsComponentService);
	}

	@Test
	public void testRenderComponent() throws Exception
	{
		given(navigationBarComponentModel.getDropDownLayout()).willReturn(navigationBarMenuLayout);
		given(navigationBarComponentModel.getItemtype()).willReturn(TEST_TYPE_CODE);

		final String viewName = navigationBarComponentController.handleComponent(request, response, model,
				navigationBarComponentModel);
		verify(model, Mockito.times(1)).addAttribute(DROP_DOWN_LAYOUT, navigationBarMenuLayout.getCode().toLowerCase());
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test
	public void testRenderComponentNoLayout() throws Exception
	{
		given(navigationBarComponentModel.getDropDownLayout()).willReturn(null);
		given(navigationBarComponentModel.getItemtype()).willReturn(TEST_TYPE_CODE);

		final String viewName = navigationBarComponentController.handleComponent(request, response, model,
				navigationBarComponentModel);
		verify(model, Mockito.times(0)).addAttribute(DROP_DOWN_LAYOUT, navigationBarMenuLayout.getCode().toLowerCase());
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test
	public void testRenderComponentUid() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getAbstractCMSComponent(TEST_COMPONENT_UID)).willReturn(navigationBarComponentModel);
		given(navigationBarComponentModel.getDropDownLayout()).willReturn(navigationBarMenuLayout);
		given(navigationBarComponentModel.getItemtype()).willReturn(TEST_TYPE_CODE);

		final String viewName = navigationBarComponentController.handleGet(request, response, model);
		verify(model, Mockito.times(1)).addAttribute(COMPONENT, navigationBarComponentModel);
		verify(model, Mockito.times(1)).addAttribute(DROP_DOWN_LAYOUT, navigationBarMenuLayout.getCode().toLowerCase());
		Assert.assertEquals(TEST_TYPE_VIEW, viewName);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(null);
		given(request.getParameter(COMPONENT_UID)).willReturn(null);
		navigationBarComponentController.handleGet(request, response, model);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound2() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(null);
		given(request.getParameter(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(null);
		navigationBarComponentController.handleGet(request, response, model);
	}

	@Test(expected = AbstractPageController.HttpNotFoundException.class)
	public void testRenderComponentNotFound3() throws Exception
	{
		given(request.getAttribute(COMPONENT_UID)).willReturn(TEST_COMPONENT_UID);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willReturn(null);
		given(cmsComponentService.getSimpleCMSComponent(TEST_COMPONENT_UID)).willThrow(new CMSItemNotFoundException(""));
		navigationBarComponentController.handleGet(request, response, model);
	}

}
