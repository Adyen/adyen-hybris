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
import org.apache.commons.collections4.CollectionUtils;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SameSiteCookieAttributeAppenderUtils {

    private SameSiteCookieAttributeAppenderUtils() {
        // ! Util class must not be initialized
    }

    private static final List<String> COOKIES_WITH_FORCE_SAME_SITE_NONE = Arrays.asList("JSESSIONID", "acceleratorSecureGUID", "blogSsoLoginRememberMe");

    public static void addSameSiteAttribute(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
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

    private static void addSameSiteNone(String sameSiteCookie, HttpServletResponse servletResponse, String userAgent) {
        Collection<String> headers = servletResponse.getHeaders(HttpHeaders.SET_COOKIE);

        // Check if exists session set cookie header
        Optional<String> sessionCookieWithoutSameSite = headers.stream()
                .filter(cookie -> cookie.startsWith(sameSiteCookie) && !cookie.contains("SameSite"))
                .findAny();

        if (sessionCookieWithoutSameSite.isPresent()
                //&& SameSiteCookieUtils.shouldSendSameSiteNone(userAgent)
        ) {
            // Replace all set cookie headers with 1 new session + sameSite header
            servletResponse.setHeader(HttpHeaders.SET_COOKIE, sessionCookieWithoutSameSite.get() + ";Secure ;SameSite=None");

            // Re-add all other set cookie headers
            headers.stream()
                    .filter(cookie -> !cookie.startsWith(sameSiteCookie))
                    .forEach(cookie -> servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie));
        }
    }

    private static boolean isNotCommittedResponse(ServletResponse servletResponse) {
        return !servletResponse.isCommitted();
    }

}