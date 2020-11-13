/*
 *                       ######
 *                       ######
 * ############    ####( ######  #####. ######  ############   ############
 * #############  #####( ######  #####. ######  #############  #############
 *        ######  #####( ######  #####. ######  #####  ######  #####  ######
 * ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 * ###### ######  #####( ######  #####. ######  #####          #####  ######
 * #############  #############  #############  #############  #####  ######
 *  ############   ############  #############   ############  #####  ######
 *                                      ######
 *                               #############
 *                               ############
 *
 * Adyen Hybris Extension
 *
 * Copyright (c) 2020 Adyen B.V.
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more info.
 */

package com.adyen.v6.utils;

import com.google.common.net.HttpHeaders;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SameSiteCookieAttributeAppenderUtils {

    private static final Logger LOG = Logger.getLogger(SameSiteCookieAttributeAppenderUtils.class);

    private ConfigurationService configurationService;

    private static final String PLATFORM_VERSION_PROPERTY = "build.version.api";
    private static final String SAMESITE_COOKIE_HANDLER_ENABLED_PROPERTY = "adyen.samesitecookie.handler.enabled";
    private static final List<String> SAP_VERSIONS_WITH_SAMESITE_FIX = Collections.singletonList("2005");
    private static final List<String> COOKIES_WITH_FORCE_SAME_SITE_NONE = Arrays.asList("JSESSIONID", "acceleratorSecureGUID");
    private static final Pattern CHROME_VERSION = Pattern.compile("Chrom[^ \\/]+\\/(\\d+)[\\.\\d]*");

    public void addSameSiteAttribute(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        // Do not modify cookies for SAP versions which already have SameSite cookies handler available
        String platformVersion = getConfigurationService().getConfiguration().getString(PLATFORM_VERSION_PROPERTY);
        if(!isSameSiteCookieHandlingEnabled() && SAP_VERSIONS_WITH_SAMESITE_FIX.contains(platformVersion)) {
            return;
        }

        if (isNotCommittedResponse(servletResponse)) {
            Collection<String> headers = servletResponse.getHeaders(HttpHeaders.SET_COOKIE);
            if (CollectionUtils.isNotEmpty(headers)) {
                String userAgent = servletRequest.getHeader(HttpHeaders.USER_AGENT);
                for (String sameSiteCookie : COOKIES_WITH_FORCE_SAME_SITE_NONE) {
                    addSameSiteNone(sameSiteCookie, servletResponse, userAgent);
                }
            }
        }
    }

    private void addSameSiteNone(String sameSiteCookie, HttpServletResponse servletResponse, String userAgent) {
        Collection<String> headers = servletResponse.getHeaders(HttpHeaders.SET_COOKIE);

        // Check if exists session set cookie header
        Optional<String> sessionCookieWithoutSameSite = headers.stream()
                .filter(cookie -> cookie.startsWith(sameSiteCookie) && !cookie.contains("SameSite"))
                .findAny();

        if (sessionCookieWithoutSameSite.isPresent() && shouldSendSameSiteNone(userAgent)) {
            // Replace all set cookie headers with 1 new session + sameSite header
            servletResponse.setHeader(HttpHeaders.SET_COOKIE, sessionCookieWithoutSameSite.get() + ";Secure ;SameSite=None");

            // Re-add all other set cookie headers
            headers.stream()
                    .filter(cookie -> !cookie.startsWith(sameSiteCookie))
                    .forEach(cookie -> servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie));
        }
    }

    private boolean isNotCommittedResponse(ServletResponse servletResponse) {
        return !servletResponse.isCommitted();
    }

    private Boolean isSameSiteCookieHandlingEnabled() {
        Configuration configuration = getConfigurationService().getConfiguration();
        boolean isSameSiteCookieHandlingEnabled = false;
        if (configuration.containsKey(SAMESITE_COOKIE_HANDLER_ENABLED_PROPERTY)) {
            isSameSiteCookieHandlingEnabled = configuration.getBoolean(SAMESITE_COOKIE_HANDLER_ENABLED_PROPERTY);
        }
        return isSameSiteCookieHandlingEnabled;
    }

    public static boolean shouldSendSameSiteNone(String useragent) {
        return isChromiumBased(useragent) && isChromiumVersionAtLeast(80, useragent);
    }

    private static boolean isChromiumBased(String useragent) {
        return useragent.contains("Chrome") || useragent.contains("Chromium");
    }

    private static boolean isChromiumVersionAtLeast(int major, String useragent) {
        Matcher matcher = CHROME_VERSION.matcher(useragent);
        if (matcher.find()) {
            try {
                String chromeVersion = matcher.group(1);
                return Integer.parseInt(chromeVersion) >= major;
            } catch (NumberFormatException ignored) {
                LOG.debug("Could not parse Chrome browser version, SameSite cookie handling will be skipped");
            }
        }
        return false;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}