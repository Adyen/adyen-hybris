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
package com.adyen.storefront.interceptors.beforeview;

import de.hybris.platform.acceleratorcms.data.CmsPageRequestContextData;
import de.hybris.platform.acceleratorcms.model.actions.AbstractCMSActionModel;
import de.hybris.platform.acceleratorcms.services.CMSPageContextService;
import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.acceleratorservices.util.SpringHelper;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.jalo.preview.PreviewData;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.preview.CMSPreviewTicketModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.jalo.c2l.LocalizableItem;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.session.SessionService;
import com.adyen.storefront.filters.cms.CMSSiteFilter;
import com.adyen.storefront.interceptors.BeforeViewHandler;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.servlet.ModelAndView;


/**
 * Filter to load the appropriate Cms page slots into the model
 */
public class CmsPageBeforeViewHandler implements BeforeViewHandler
{
	private static final Logger LOG = Logger.getLogger(CmsPageBeforeViewHandler.class);

	private static final String CSS_CODE_PREFIX = "page-";
	private static final String CSS_LABEL_PREFIX = "pageLabel-";
	private static final String CSS_TYPE_PREFIX = "pageType-";
	private static final String CSS_TEMPLATE_PREFIX = "template-";

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Resource(name = "cmsPageService")
	private CMSPageService cmsPageService;

	@Resource(name = "cmsPreviewService")
	private CMSPreviewService cmsPreviewService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "cmsPageContextService")
	private CMSPageContextService cmsPageContextService;

	@Resource(name = "requestContextRestrictionConverter")
	private Converter<RequestContextData, RestrictionData> requestContextRestrictionConverter;


	@Override
	public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView)
	{
		modelAndView.addObject("cmsSite", cmsSiteService.getCurrentSite());

		// Look for the page in the model
		final AbstractPageModel page = updateCmsPageInModelAndView(request, modelAndView);

		modelAndView.addObject("pageBodyCssClasses", buildCssClasses(page));
		if (page != null)
		{
			final Set<String> actonJsFiles = new HashSet();

			final Collection<ContentSlotData> contentSlotsForPage = cmsPageService.getContentSlotsForPage(page);
			for (final ContentSlotData contentSlotData : contentSlotsForPage)
			{
				final ContentSlotModel contentSlot = contentSlotData.getContentSlot();
				final List<AbstractCMSComponentModel> cmsComponents = contentSlot.getCmsComponents();
				for (final AbstractCMSComponentModel cmsComponent : cmsComponents)
				{
					final List<AbstractCMSActionModel> actions = cmsComponent.getActions();
					for (final AbstractCMSActionModel action : actions)
					{
						actonJsFiles.add(StringUtils.lowerCase(action.getItemtype()) + ".js");
					}
				}
			}
			modelAndView.addObject("cmsActionsJsFiles", actonJsFiles);
		}

		// Create the restriction data
		final RequestContextData requestContextData = SpringHelper.getSpringBean(request, "requestContextData",
				RequestContextData.class, true);
		final RestrictionData restrictionData = requestContextRestrictionConverter.convert(requestContextData);

		// Initialise CMS support
		final CmsPageRequestContextData cmsPageRequestContextData = cmsPageContextService.updateCmsPageContextForPage(request,
				page, restrictionData);
		modelAndView.addObject("cmsPageRequestContextData", cmsPageRequestContextData);

		sessionService.setAttribute(LocalizableItem.LANGUAGE_FALLBACK_ENABLED, Boolean.TRUE);
		sessionService.setAttribute(AbstractItemModel.LANGUAGE_FALLBACK_ENABLED_SERVICE_LAYER, Boolean.TRUE);
	}

	protected AbstractPageModel updateCmsPageInModelAndView(final HttpServletRequest request, final ModelAndView modelAndView)
	{
		// Look for the page in the model
		final AbstractPageModel requestedPage = (AbstractPageModel) modelAndView.getModel().get(
				AbstractPageController.CMS_PAGE_MODEL);
		if (requestedPage != null)
		{
			final AbstractPageModel previewPage = lookupPreviewPage(request);
			if (previewPage != null && !previewPage.equals(requestedPage))
			{
				// Have a preview page that overrides the current page

				// Check that the preview page is the same type as the expected page
				if (!requestedPage.getClass().isInstance(previewPage))
				{
					LOG.error("Preview page is of type [" + previewPage.getClass().getName() + "] expected page of type ["
							+ requestedPage.getClass().getName() + "]");
				}
				else
				{
					// Push the preview page into the model
					LOG.info("Replaced page [" + requestedPage + "] with preview page [" + previewPage + "]");
					modelAndView.addObject(AbstractPageController.CMS_PAGE_MODEL, previewPage);

					// Check to see if we are using the default view for the page
					if (modelAndView.getViewName() != null && modelAndView.getViewName().equals(getViewForPage(requestedPage)))
					{
						final String viewForPreviewPage = getViewForPage(previewPage);
						if (viewForPreviewPage != null && !viewForPreviewPage.equals(modelAndView.getViewName()))
						{
							// Change the view name
							LOG.info("Changing view from [" + modelAndView.getViewName() + "] to preview view [" + viewForPreviewPage
									+ "]");
							modelAndView.setViewName(viewForPreviewPage);
						}
					}

					return previewPage;
				}
			}
		}
		return requestedPage;
	}

	protected String buildCssClasses(final AbstractPageModel page)
	{
		if (page == null)
		{
			return "";
		}

		final String regEx = "[^a-zA-Z0-9-]";
		final StringBuilder cssClasses = new StringBuilder();
		cssClasses.append(CSS_CODE_PREFIX).append(page.getUid().replaceAll(regEx, "-"));
		cssClasses.append(' ');
		cssClasses.append(CSS_TYPE_PREFIX).append(page.getTypeCode().replaceAll(regEx, "-"));
		cssClasses.append(' ');
		cssClasses.append(CSS_TEMPLATE_PREFIX).append(getViewForPage(page).replaceAll(regEx, "-"));
		cssClasses.append(' ');

		if (ContentPageModel.class.equals(page.getClass()))
		{
			final ContentPageModel contentPage = (ContentPageModel) page;
			if (contentPage.getLabel() != null)
			{
				cssClasses.append(CSS_LABEL_PREFIX).append(contentPage.getLabel().replaceAll(regEx, "-"));
			}
		}

		return cssClasses.toString();
	}

	/**
	 * Retrieves a preview ticket, if available and retrieves the preview page from the {@link PreviewData}
	 * 
	 * @param request
	 * @return preview page
	 */
	protected AbstractPageModel lookupPreviewPage(final HttpServletRequest request)
	{
		final String previewTicketId = request.getParameter(CMSSiteFilter.PREVIEW_TICKET_ID_PARAM);
		if (previewTicketId != null && !previewTicketId.isEmpty())
		{
			final CMSPreviewTicketModel previewTicket = cmsPreviewService.getPreviewTicket(previewTicketId);
			if (previewTicket != null)
			{
				final PreviewDataModel previewData = previewTicket.getPreviewData();
				if (previewData != null)
				{
					return previewData.getPage();
				}
			}
		}
		return null;
	}

	/**
	 * Returns ths view name for the page by retrieving the frontendTemplateName from the masterTemplate of the page
	 * 
	 * @param page
	 * @return view name or null, if the view name cannot retrieved from the masterTemplate
	 */
	protected String getViewForPage(final AbstractPageModel page)
	{
		if (page != null)
		{
			final PageTemplateModel masterTemplate = page.getMasterTemplate();
			if (masterTemplate != null)
			{
				final String targetPage = cmsPageService.getFrontendTemplateName(masterTemplate);
				if (targetPage != null && !targetPage.isEmpty())
				{
					return AbstractPageController.PAGE_ROOT + targetPage;
				}
			}
		}
		return null;
	}
}
