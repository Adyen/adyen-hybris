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

package com.adyen.v6.filters;

import com.adyen.v6.utils.SameSiteCookieAttributeAppenderUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * This class uses code written by Igor Zarvanskyi and published on https://clutcher.github.io/post/hybris/same_site_login_issue/
 */
public class SameSiteCookiePostProcessFilter extends GenericFilterBean {

    private SameSiteCookieAttributeAppenderUtils sameSiteCookieAttributeAppenderUtils;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        getSameSiteCookieAttributeAppenderUtils().addSameSiteAttribute((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    protected SameSiteCookieAttributeAppenderUtils getSameSiteCookieAttributeAppenderUtils() {
        return sameSiteCookieAttributeAppenderUtils;
    }

    public void setSameSiteCookieAttributeAppenderUtils(SameSiteCookieAttributeAppenderUtils sameSiteCookieAttributeAppenderUtils) {
        this.sameSiteCookieAttributeAppenderUtils = sameSiteCookieAttributeAppenderUtils;
    }
}