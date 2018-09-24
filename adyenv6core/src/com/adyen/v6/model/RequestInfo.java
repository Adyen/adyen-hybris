/*
 *                        ######
 *                        ######
 *  ############    ####( ######  #####. ######  ############   ############
 *  #############  #####( ######  #####. ######  #############  #############
 *         ######  #####( ######  #####. ######  #####  ######  #####  ######
 *  ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 *  ###### ######  #####( ######  #####. ######  #####          #####  ######
 *  #############  #############  #############  #############  #####  ######
 *   ############   ############  #############   ############  #####  ######
 *                                       ######
 *                                #############
 *                                ############
 *
 *  Adyen Hybris Extension
 *
 *  Copyright (c) 2017 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.v6.model;

import javax.servlet.http.HttpServletRequest;

public class RequestInfo {

    public static final String USER_AGENT_HEADER = "User-Agent";
    public static final String ACCEPT_HEADER = "Accept";

    private String userAgent;
    private String acceptHeader;
    private String shopperIp;

    public RequestInfo(HttpServletRequest request) {
        this.userAgent = request.getHeader(USER_AGENT_HEADER);
        this.acceptHeader = request.getHeader(ACCEPT_HEADER);
        this.shopperIp = request.getRemoteAddr();
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getAcceptHeader() {
        return acceptHeader;
    }

    public String getShopperIp() {
        return shopperIp;
    }
}
