package com.adyen.v6.controllers.checkout;

import com.adyen.v6.forms.AdyenPaymentForm;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller(value = "/checkout/multi/adyen")
public class AdyenPaymentMethodsController {

  @Autowired
  private CheckoutFacade checkoutFacade;
  @RequireHardLogIn
  @PostMapping(value = "/select-payment-method")
  public ResponseEntity<HttpStatus> selectPaymentMethod(AdyenPaymentForm adyenPaymentForm){
    adyenPaymentForm.getPaymentMethod();
    checkoutFacade.payme
    return
  }
}
