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
package com.adyen.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.StoreBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.forms.StoreFinderForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.StorePositionForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.storefinder.StoreFinderFacade;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.exception.MapServiceException;
import com.adyen.storefront.controllers.ControllerConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import com.adyen.storefront.util.MetaSanitizerUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;


/**
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/**/store")
public class StorePageController extends AbstractPageController
{
	protected static final Logger LOG = Logger.getLogger(StorePageController.class);
	/**
	 * We use this suffix pattern because of an issue with Spring 3.1 where a Uri value is incorrectly extracted if it
	 * contains on or more '.' characters. Please see https://jira.springsource.org/browse/SPR-6164 for a discussion on
	 * the issue and future resolution.
	 */
	private static final String STORE_CODE_PATH_VARIABLE_PATTERN = "/{storeCode:.*}";
	private static final String REDIRECT_STORE_FINDER = REDIRECT_PREFIX + "/store-finder";

	private static final String STORE_FINDER_CMS_PAGE_LABEL = "storefinder";
	private static final String GOOGLE_API_KEY_ID = "googleApiKey";
	private static final String GOOGLE_API_VERSION = "googleApiVersion";

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "storeBreadcrumbBuilder")
	private StoreBreadcrumbBuilder storeBreadcrumbBuilder;

	@Resource(name = "storeFinderFacade")
	private StoreFinderFacade storeFinderFacade;

	@ModelAttribute("googleApiVersion")
	public String getGoogleApiVersion()
	{
		return configurationService.getConfiguration().getString(GOOGLE_API_VERSION);
	}

	@ModelAttribute("googleApiKey")
	public String getGoogleApiKey(final HttpServletRequest request)
	{
		final String googleApiKey = getHostConfigService().getProperty(GOOGLE_API_KEY_ID, request.getServerName());
		if (StringUtils.isEmpty(googleApiKey))
		{
			LOG.warn("No Google API key found for server: " + request.getServerName());
		}
		return googleApiKey;
	}

	@RequestMapping(value = STORE_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	public String storeDetail(@PathVariable("storeCode") final String storeCode,
			@RequestParam(value = "lat", required = false) final Double sourceLatitude,
			@RequestParam(value = "long", required = false) final Double sourceLongitude,
			@RequestParam(value = "q", required = false) final String locationQuery, final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		final StoreFinderForm storeFinderForm = new StoreFinderForm();
		model.addAttribute("storeFinderForm", storeFinderForm);
		final StorePositionForm storePositionForm = new StorePositionForm();
		model.addAttribute("storePositionForm", storePositionForm);

		if (sourceLatitude != null && sourceLongitude != null)
		{
			final GeoPoint geoPoint = new GeoPoint();
			geoPoint.setLatitude(sourceLatitude.doubleValue());
			geoPoint.setLongitude(sourceLongitude.doubleValue());

			// Get the point of service data with the formatted distance
			try
			{
				final PointOfServiceData pointOfServiceData = storeFinderFacade.getPointOfServiceForNameAndPosition(storeCode,
						geoPoint);
				if (pointOfServiceData == null)
				{
					return handleStoreNotFoundCase(redirectModel);
				}
				pointOfServiceData.setUrl("/store/" + pointOfServiceData.getName());
				model.addAttribute("store", pointOfServiceData);

				if (locationQuery != null && !locationQuery.isEmpty())
				{
					model.addAttribute("locationQuery", locationQuery);

					// Build URL to location query
					final String storeFinderSearchUrl = UriComponentsBuilder.fromPath("/store-finder").queryParam("q", locationQuery)
							.build().toString();
					model.addAttribute(WebConstants.BREADCRUMBS_KEY,
							storeBreadcrumbBuilder.getBreadcrumbs(pointOfServiceData, storeFinderSearchUrl));
				}
				else
				{
					// Build URL to position query
					final String storeFinderSearchUrl = UriComponentsBuilder.fromPath("/store-finder/position")
							.queryParam("lat", sourceLatitude).queryParam("long", sourceLongitude).build().toString();
					model.addAttribute(WebConstants.BREADCRUMBS_KEY,
							storeBreadcrumbBuilder.getBreadcrumbs(pointOfServiceData, storeFinderSearchUrl));
				}
				setUpMetaData(model, pointOfServiceData);
			}
			catch (final ModelNotFoundException e)
			{
				return handleStoreNotFoundCase(redirectModel);
			}
		}
		else
		{
			// No source point specified - just lookup the POS by name
			try
			{
				final PointOfServiceData pointOfServiceData = storeFinderFacade.getPointOfServiceForName(storeCode);
				pointOfServiceData.setUrl("/store/" + pointOfServiceData.getName());
				model.addAttribute("store", pointOfServiceData);
				model.addAttribute(WebConstants.BREADCRUMBS_KEY, storeBreadcrumbBuilder.getBreadcrumbs(pointOfServiceData));
				setUpMetaData(model, pointOfServiceData);
			}
			catch (final ModelNotFoundException e)
			{
				return handleStoreNotFoundCase(redirectModel);
			}
		}

		storeCmsPageInModel(model, getStoreFinderPage());
		return ControllerConstants.Views.Pages.StoreFinder.StoreFinderDetailsPage;
	}

	protected void setUpMetaData(final Model model, final PointOfServiceData pointOfServiceData)
	{
		final String metaKeywords = createMetaKeywords(pointOfServiceData);
		final String metaDescription = MetaSanitizerUtil.sanitizeDescription(pointOfServiceData.getDescription());
		setUpMetaData(model, metaKeywords, metaDescription);
	}

	@RequestMapping(value = STORE_CODE_PATH_VARIABLE_PATTERN + "/map", method = RequestMethod.GET)
	public String viewMap(@PathVariable("storeCode") final String storeCode, final Model model,
			final RedirectAttributes redirectModel) throws GeoLocatorException, MapServiceException, CMSItemNotFoundException
	{
		final StoreFinderForm storeFinderForm = new StoreFinderForm();
		model.addAttribute("storeFinderForm", storeFinderForm);
		final StorePositionForm storePositionForm = new StorePositionForm();
		model.addAttribute("storePositionForm", storePositionForm);

		try
		{
			final PointOfServiceData pointOfServiceData = storeFinderFacade.getPointOfServiceForName(storeCode);
			pointOfServiceData.setUrl("/store/" + pointOfServiceData.getName());
			model.addAttribute("store", pointOfServiceData);

			storeCmsPageInModel(model, getStoreFinderPage());
			model.addAttribute(WebConstants.BREADCRUMBS_KEY,
					storeBreadcrumbBuilder.getBreadcrumbsForSubPage(pointOfServiceData, "storeDetails.map.link"));
		}
		catch (final ModelNotFoundException e)
		{
			return handleStoreNotFoundCase(redirectModel);
		}
		return ControllerConstants.Views.Pages.StoreFinder.StoreFinderViewMapPage;
	}

	protected String handleStoreNotFoundCase(final RedirectAttributes redirectModel)
	{
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "storelocator.error.no.results.title");
		return REDIRECT_STORE_FINDER;
	}

	protected AbstractPageModel getStoreFinderPage() throws CMSItemNotFoundException
	{
		return getContentPageForLabelOrId(STORE_FINDER_CMS_PAGE_LABEL);
	}

	protected String createMetaKeywords(final PointOfServiceData pointOfServiceData)
	{
		final AddressData address = pointOfServiceData.getAddress();

		final String[] keywords = { address.getTown(), address.getPostalCode(), address.getCountry().getName() };
		return StringUtils.join(keywords, ',');
	}
}
