package com.adyen.v6.controllers.api;

import com.adyen.model.nexo.CardData;
import com.adyen.v6.facades.AdyenCheckoutFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/api/checkout")
@Controller
public class AdyenPlaceOrderController {

    @Autowired
    private AdyenCheckoutFacade adyenCheckoutFacade;
    @PostMapping("/place-order")
    public ResponseEntity<HttpStatus> placeOrder(CardData cardData) {

        return ResponseEntity.ok().build();
    }


}
