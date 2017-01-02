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
package com.adyen.storefront.url;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Responsible for generating correct URL for given page.
 */
public class DefaultPreviewDataModelUrlResolver implements UrlResolver<PreviewDataModel>
{
	private UrlResolver<ProductModel> productModelUrlResolver;
	private UrlResolver<CategoryModel> categoryModelUrlResolver;
	private Map<String, String> pageMapping;

	protected UrlResolver<ProductModel> getProductModelUrlResolver()
	{
		return productModelUrlResolver;
	}

	@Required
	public void setProductModelUrlResolver(final UrlResolver<ProductModel> productModelUrlResolver)
	{
		this.productModelUrlResolver = productModelUrlResolver;
	}

	protected UrlResolver<CategoryModel> getCategoryModelUrlResolver()
	{
		return categoryModelUrlResolver;
	}

	@Required
	public void setCategoryModelUrlResolver(final UrlResolver<CategoryModel> categoryModelUrlResolver)
	{
		this.categoryModelUrlResolver = categoryModelUrlResolver;
	}

	protected Map<String, String> getPageMapping()
	{
		return pageMapping;
	}

	@Required
	public void setPageMapping(final Map<String, String> pageMapping)
	{
		this.pageMapping = pageMapping;
	}


	/**
	 * Returns the relative URL for the specified page <code>page</code>.
	 * 
	 * @return relative URL for the specified page
	 */
	@Override
	public String resolve(final PreviewDataModel previewDataModel)
	{
		if (previewDataModel != null)
		{
			final AbstractPageModel page = previewDataModel.getPage();

			final Map<String, String> pageMapping = getPageMapping();
			if (pageMapping != null && page != null)
			{
				// Lookup the page mapping by page UID
				final String pageUid = page.getUid();
				if (pageUid != null)
				{
					final String url = pageMapping.get(pageUid);
					if (url != null && !url.isEmpty())
					{
						return url;
					}
				}

				// For ContentPages also lookup by label
				if (page instanceof ContentPageModel)
				{
					final String pageLabel = ((ContentPageModel) page).getLabel();
					if (pageLabel != null)
					{
						final String url = pageMapping.get(pageLabel);
						if (url != null && !url.isEmpty())
						{
							return url;
						}
					}
				}
			}

			if (page instanceof ContentPageModel)
			{
				// Construct URL to preview the Page by UID
				return "/preview-content?uid=" + page.getUid();
			}

			if (page instanceof CategoryPageModel)
			{
				return getCategoryModelUrlResolver().resolve(getPreviewValueForCategoryPage(previewDataModel));
			}

			if (page instanceof ProductPageModel)
			{
				return getProductModelUrlResolver().resolve(getPreviewValueForProductPage(previewDataModel));
			}
		}

		return "/";
	}

	protected CategoryModel getPreviewValueForCategoryPage(final PreviewDataModel previewCtx)
	{
		final CMSSiteModel currentSite = previewCtx.getActiveSite();
		CategoryModel ret = previewCtx.getPreviewCategory();

		if (ret == null && currentSite != null)
		{
			ret = currentSite.getDefaultPreviewCategory();
		}
		return ret;
	}

	protected ProductModel getPreviewValueForProductPage(final PreviewDataModel previewCtx)
	{
		final CMSSiteModel currentSite = previewCtx.getActiveSite();
		ProductModel ret = previewCtx.getPreviewProduct();

		if (ret == null && currentSite != null)
		{
			ret = currentSite.getDefaultPreviewProduct();
		}
		return ret;
	}
}
