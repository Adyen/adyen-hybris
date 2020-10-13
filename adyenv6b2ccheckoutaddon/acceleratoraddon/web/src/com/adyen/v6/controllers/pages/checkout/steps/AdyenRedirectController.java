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
 *  Copyright (c) 2020 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.v6.controllers.pages.checkout.steps;

import com.adyen.v6.constants.AdyenControllerConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.adyen.v6.constants.AdyenControllerConstants.TRANSPARENT_REDIRECT_PREFIX;

@Controller
@RequestMapping(TRANSPARENT_REDIRECT_PREFIX)
public class AdyenRedirectController extends AbstractCheckoutController {
    private static final Logger LOGGER = Logger.getLogger(AdyenRedirectController.class);

    public static final String THREE_D_MD = "MD";
    public static final String THREE_D_PARES = "PaRes";

    @RequestMapping(method = RequestMethod.POST)
    public String transparentRedirect(final Model model, final HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        LOGGER.debug("Session=" + session);

        model.addAttribute(THREE_D_PARES, request.getParameter(THREE_D_PARES));
        model.addAttribute(THREE_D_MD, request.getParameter(THREE_D_MD));

        LOGGER.debug("Redirecting 3DS response...");

        return AdyenControllerConstants.Views.Pages.MultiStepCheckout.Redirect3DSecureResponse;
    }
}
