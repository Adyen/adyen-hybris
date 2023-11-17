package com.adyen.v6.controllers.checkout;

import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.v6.facades.AdyenExpressCheckoutFacade;
import com.adyen.v6.request.ApplePayExpressCartRequest;
import com.adyen.v6.request.ApplePayExpressPDPRequest;
import de.hybris.platform.acceleratorstorefrontcommons.security.AutoLoginStrategy;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AdyenApplePayExpressCheckoutController {
    private static final Logger LOG = Logger.getLogger(AdyenApplePayExpressCheckoutController.class);

    @Autowired
    private AdyenExpressCheckoutFacade adyenExpressCheckoutFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private AutoLoginStrategy autoLoginStrategy;

    @PostMapping("/expressCheckout/applePayPDP")
    public ResponseEntity applePayExpressPDP(final HttpServletRequest request, final HttpServletResponse response, @RequestBody ApplePayExpressPDPRequest applePayExpressPDPRequest) throws Exception {

        PaymentsResponse paymentsResponse = adyenExpressCheckoutFacade.expressPDPCheckout(applePayExpressPDPRequest.getAddressData(), applePayExpressPDPRequest.getProductCode(),
                applePayExpressPDPRequest.getAdyenApplePayMerchantIdentifier(), applePayExpressPDPRequest.getAdyenApplePayMerchantName(),
                applePayExpressPDPRequest.getApplePayToken(), request);

        autoLoginStrategy.login(userService.getCurrentUser().getUid(), null, request, response);

        return new ResponseEntity<>(paymentsResponse, HttpStatus.OK);
    }

    @PostMapping("/expressCheckout/cart")
    public ResponseEntity cartExpressCheckout(final HttpServletRequest request, final HttpServletResponse response, @RequestBody ApplePayExpressCartRequest applePayExpressCartRequest) throws Exception {

        PaymentsResponse paymentsResponse = adyenExpressCheckoutFacade.expressCartCheckout(applePayExpressCartRequest.getAddressData(),
                applePayExpressCartRequest.getAdyenApplePayMerchantIdentifier(), applePayExpressCartRequest.getAdyenApplePayMerchantName(),
                applePayExpressCartRequest.getApplePayToken(), request);

        autoLoginStrategy.login(userService.getCurrentUser().getUid(), null, request, response);

        return new ResponseEntity<>(paymentsResponse, HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = Exception.class)
    public String adyenComponentExceptionHandler(Exception e) {
        LOG.error("Exception during ApplePayExpress processing", e);
        return "Exception during ApplePayExpress processing";
    }
}
