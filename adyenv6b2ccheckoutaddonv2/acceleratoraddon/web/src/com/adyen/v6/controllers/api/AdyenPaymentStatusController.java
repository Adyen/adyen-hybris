package com.adyen.v6.controllers.api;

import com.adyen.v6.facades.AdyenOrderFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.servicelayer.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/api/checkout")
public class AdyenPaymentStatusController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private AdyenOrderFacade adyenOrderFacade;

    @RequireHardLogIn
    @PostMapping("/get-payment-status")
    public ResponseEntity<String> postIdForPaymentStatus(@RequestBody String orderCode) {
        Object attribute = sessionService.getAttribute(WebConstants.ANONYMOUS_CHECKOUT_GUID);

        try {
            String paymentStatus = adyenOrderFacade.getPaymentStatus(orderCode, attribute);
            return ResponseEntity.ok().body(paymentStatus);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().build();
        }

    }

}
