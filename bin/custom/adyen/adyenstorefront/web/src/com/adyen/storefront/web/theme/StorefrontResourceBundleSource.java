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
package com.adyen.storefront.web.theme;

import de.hybris.platform.acceleratorservices.addonsupport.RequiredAddOnsNameProvider;
import de.hybris.platform.util.Utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;
import org.springframework.ui.context.support.SimpleTheme;
import org.springframework.web.context.ConfigurableWebApplicationContext;


/**
 * CustomResourceBundleThemeSource. Supports site and theme specific resource bundles. Uses a
 * ReloadableResourceBundleMessageSource to load the theme file. Delegates to a message source if the theme specific
 * file cannot be found.
 */
public class StorefrontResourceBundleSource implements ThemeSource, ResourceLoaderAware, MessageSource
{
	private static final Logger LOG = Logger.getLogger(StorefrontResourceBundleSource.class);

	private MessageSource parentMessageSource;
	private MessageSource lastMessageSource;
	private int cacheSeconds;
	private ResourceLoader resourceLoader;
	private boolean fallbackToSystemLocale;
	private String defaultEncoding;
	private String basenamePrefix;
	private String basePrefix;
	private String sitePrefix;
	private String themePrefix;
	private RequiredAddOnsNameProvider requiredAddOnsNameProvider;

	@Autowired
	private ApplicationContext appContext;

	/**
	 * Map from theme name to Theme instance
	 */
	private final Map<String, Theme> themeCache = new HashMap<String, Theme>();

	protected MessageSource getParentMessageSource()
	{
		return parentMessageSource;
	}

	@Required
	public void setParentMessageSource(final MessageSource parentMessageSource)
	{
		this.parentMessageSource = parentMessageSource;
	}

	protected String getDefaultEncoding()
	{
		return defaultEncoding;
	}

	@Required
	public void setDefaultEncoding(final String defaultEncoding)
	{
		this.defaultEncoding = defaultEncoding;
	}

	public int getCacheSeconds()
	{
		return cacheSeconds;
	}

	@Required
	public void setCacheSeconds(final int cacheSeconds)
	{
		this.cacheSeconds = cacheSeconds;
	}

	public ResourceLoader getResourceLoader()
	{
		return resourceLoader;
	}

	public String getSitePrefix()
	{
		return sitePrefix;
	}

	@Required
	public void setSitePrefix(final String sitePrefix)
	{
		this.sitePrefix = sitePrefix;
	}

	public String getThemePrefix()
	{
		return themePrefix;
	}

	@Required
	public void setThemePrefix(final String themePrefix)
	{
		this.themePrefix = themePrefix;
	}

	@Override
	public void setResourceLoader(final ResourceLoader resourceLoader)
	{
		this.resourceLoader = resourceLoader;
	}

	public RequiredAddOnsNameProvider getRequiredAddOnsNameProvider()
	{
		return requiredAddOnsNameProvider;
	}

	@Required
	public void setRequiredAddOnsNameProvider(final RequiredAddOnsNameProvider requiredAddOnsNameProvider)
	{
		this.requiredAddOnsNameProvider = requiredAddOnsNameProvider;
	}

	/**
	 * @param fallbackToSystemLocale
	 *           the fallbackToSystemLocale to set
	 */
	public void setFallbackToSystemLocale(final boolean fallbackToSystemLocale)
	{
		this.fallbackToSystemLocale = fallbackToSystemLocale;
	}

	public String getBasenamePrefix()
	{
		return basenamePrefix;
	}

	@Required
	public void setBasenamePrefix(final String basenamePrefix)
	{
		this.basenamePrefix = basenamePrefix;
	}

	public String getBasePrefix()
	{
		return basePrefix;
	}

	@Required
	public void setBasePrefix(final String basePrefix)
	{
		this.basePrefix = basePrefix;
	}

	/**
	 * This implementation returns a SimpleTheme instance, holding a ResourceBundle-based MessageSource whose basename
	 * corresponds to the given site name (prefixed by the configured "siteBasenamePrefix") which then delegates to a
	 * ResourceBundle-based MessageSource whose basename corresponds to the theme name (prefixed by the configured
	 * "themeBasenamePrefix") which in turn delegates to the {@link #getParentMessageSource()}.
	 * 
	 * <p>
	 * SimpleTheme instances are cached per theme name.
	 * 
	 * <p>
	 * Uses reloadable MessageSources to reflect changes to the underlying files. Set the {@link #setCacheSeconds(int)}
	 * to control how long the files should be cached for.
	 * 
	 * @param themeName
	 *           the theme name
	 * @see #setSiteBasenamePrefix
	 * @see #setThemeBasenamePrefix
	 */
	@Override
	public Theme getTheme(final String themeName)
	{
		if (themeName == null)
		{
			return null;
		}

		synchronized (this.themeCache)
		{
			// Look for the theme in the cache
			{
				final Theme theme = this.themeCache.get(themeName);
				if (theme != null)
				{
					return theme;
				}
			}

			// Split the theme name into site and theme parts
			final String[] strings = splitThemeName(themeName);
			final String uiExperiencePart = strings[0];
			final String sitePart = strings[1];
			final String themePart = strings[2];
			final String siteBasename = getBasenamePrefix() + getSitePrefix() + "-" + sitePart;
			final String themeBasename = getBasenamePrefix() + getThemePrefix() + "-" + themePart;
			final String uiExperienceCode = uiExperiencePart.toLowerCase();
			final List<String> addOnNames = requiredAddOnsNameProvider.getAddOns(((ConfigurableWebApplicationContext) appContext)
					.getServletContext().getServletContextName());
			// Build the messages sources from most general to most specific
			final MessageSource addOnBaseMessageSrouce = createAddOnMessageSource(addOnNames, getParentMessageSource(),
					getBasePrefix());
			final MessageSource themeMessageSource = createMessageSource(themeBasename, addOnBaseMessageSrouce);
			final MessageSource addOnThemeMessageSource = createAddOnMessageSource(addOnNames, themeMessageSource, getThemePrefix(),
					themePart);
			final MessageSource themeUiExperienceMessageSource = createMessageSource(themeBasename + "-" + uiExperienceCode,
					addOnThemeMessageSource);
			final MessageSource addOnThemeUiMessageSource = createAddOnMessageSource(addOnNames, themeUiExperienceMessageSource,
					getThemePrefix(), themePart, uiExperienceCode);
			final MessageSource siteMessageSource = createMessageSource(siteBasename, addOnThemeUiMessageSource);
			final MessageSource addOnSiteMessageSource = createAddOnMessageSource(addOnNames, siteMessageSource, getSitePrefix(),
					sitePart);
			final MessageSource siteUiExperienceMessageSource = createMessageSource(siteBasename + "-" + uiExperienceCode,
					addOnSiteMessageSource);
			final MessageSource addOnSiteUiMessageSource = createAddOnMessageSource(addOnNames, siteUiExperienceMessageSource,
					getSitePrefix(), sitePart, uiExperienceCode);

			lastMessageSource = addOnSiteUiMessageSource;

			// Create the new theme
			final Theme theme = new SimpleTheme(themeName, addOnSiteUiMessageSource);
			this.themeCache.put(themeName, theme);

			if (LOG.isDebugEnabled())
			{
				LOG.debug("Theme created: name '" + themeName + "', iteBasename [" + siteBasename + "], themeBasename ["
						+ themeBasename + "]");
			}
			return theme;
		}
	}

	protected MessageSource createAddOnMessageSource(final List<String> addOnNames, final MessageSource parentMessageSource,
			final String... nameParts)
	{
		if (addOnNames.isEmpty())
		{
			return parentMessageSource;
		}

		final StringBuffer nameBuffer = new StringBuffer();
		for (final String part : nameParts)
		{
			nameBuffer.append(part);
			nameBuffer.append('-');
		}
		String lastBasenamePart = nameBuffer.toString();
		lastBasenamePart = lastBasenamePart.substring(0, lastBasenamePart.length() - 1);

		MessageSource messageSource = null;
		MessageSource tmpParentMessageSource = parentMessageSource;
		for (final String addOnName : addOnNames)
		{
			final String basename = "file:///" + Utilities.getExtensionInfo(addOnName).getExtensionDirectory()
					+ "/acceleratoraddon/web/webroot/WEB-INF/messages/" + lastBasenamePart;
			if (LOG.isDebugEnabled())
			{
				LOG.debug("AddOn message reource basename: " + basename);
			}

			messageSource = createMessageSource(basename, tmpParentMessageSource);
			tmpParentMessageSource = messageSource;
		}

		return messageSource;

	}


	protected String[] splitThemeName(final String themeName)
	{
		return themeName.split(",", 3);
	}

	protected MessageSource createMessageSource(final String basename, final MessageSource parentMessageSource)
	{
		final AbstractMessageSource messageSource = createMessageSource(basename);
		messageSource.setParentMessageSource(parentMessageSource);
		messageSource.setUseCodeAsDefaultMessage(true);
		return messageSource;
	}

	protected AbstractMessageSource createMessageSource(final String basename)
	{
		final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename(basename);
		messageSource.setCacheSeconds(getCacheSeconds());
		messageSource.setResourceLoader(getResourceLoader());
		messageSource.setFallbackToSystemLocale(fallbackToSystemLocale);
		messageSource.setDefaultEncoding(getDefaultEncoding());
		return messageSource;
	}

	@Override
	public String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale)
	{
		if (lastMessageSource != null)
		{
			return lastMessageSource.getMessage(code, args, defaultMessage, locale);
		}
		return getParentMessageSource().getMessage(code, args, defaultMessage, locale);
	}

	@Override
	public String getMessage(final String code, final Object[] args, final Locale locale) throws NoSuchMessageException
	{
		return this.getMessage(code, args, null, locale);
	}

	@Override
	public String getMessage(final MessageSourceResolvable resolvable, final Locale locale) throws NoSuchMessageException
	{
		if (lastMessageSource != null)
		{
			return lastMessageSource.getMessage(resolvable, locale);
		}
		return getParentMessageSource().getMessage(resolvable, locale);
	}
}
