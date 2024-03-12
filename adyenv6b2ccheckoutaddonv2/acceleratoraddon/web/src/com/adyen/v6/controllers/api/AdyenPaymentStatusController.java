package com.adyen.v6.controllers.api;

import com.adyen.v6.facades.AdyenCheckoutFacade;
import com.adyen.v6.facades.AdyenOrderFacade;
import com.adyen.v6.repository.OrderRepository;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
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
    PaymentService paymentService;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    AdyenCheckoutFacade adyenCheckoutFacade;

    @Autowired
    UserService userService;

    @Autowired
    SessionService sessionService;

    @Autowired
    AdyenOrderFacade adyenOrderFacade;

    @RequireHardLogIn
    @PostMapping("/get-payment-status")
    public ResponseEntity<String> postIdForPaymentStatus(@RequestBody String orderCode) {
        Object attribute = sessionService.getAttribute(WebConstants.ANONYMOUS_CHECKOUT_GUID);

        try {
            String paymentStatus = adyenOrderFacade.getPaymentStatus(orderCode, attribute);
            return ResponseEntity.ok().body(paymentStatus);
        } catch (UnknownIdentifierException exception) {
            return ResponseEntity.badRequest().build();
        }

    }

}
