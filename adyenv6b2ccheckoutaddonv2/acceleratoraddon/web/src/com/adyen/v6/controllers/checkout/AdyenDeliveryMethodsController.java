package com.adyen.v6.controllers.checkout;

import com.adyen.v6.dto.AdyenDeliveryMethodDTO;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping(value = "/checkoutApi/deliveryMethods")
public class AdyenDeliveryMethodsController {

    @Autowired
    private CheckoutFacade checkoutFacade;

    @RequireHardLogIn
    @GetMapping(value = "/getSupportedDeliveryMethods", produces = "application/json")
    public ResponseEntity<List<? extends DeliveryModeData>> getAllDeliveryMethods() {
        return new ResponseEntity<>(checkoutFacade.getSupportedDeliveryModes(), HttpStatus.OK);
    }

    @RequireHardLogIn
    @PostMapping(value = "/selectDeliveryMethod", produces = "application/json")
    public ResponseEntity<HttpStatus> selectDeliveryMethod(@RequestBody AdyenDeliveryMethodDTO deliveryMethod) {
        if (StringUtils.isNotEmpty(deliveryMethod.getDeliveryMethodCode())) {
            checkoutFacade.setDeliveryMode(deliveryMethod.getDeliveryMethodCode());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public CheckoutFacade getCheckoutFacade() {
        return checkoutFacade;
    }
}
