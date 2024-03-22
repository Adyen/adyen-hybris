package com.adyen.v6.controllers.api;

import com.adyen.v6.response.ConfigurationResponse;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.enums.CountryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/configuration")
public class AdyenConfigurationController {

    @Autowired
    private CheckoutFacade checkoutFacade;

    @Autowired
    private UserFacade userFacade;

    @GetMapping("/shipping-address")
    public ResponseEntity<ConfigurationResponse> getConfiguration() {
        ConfigurationResponse configurationResponse = new ConfigurationResponse();

        configurationResponse.setCountries(checkoutFacade.getCountries(CountryType.SHIPPING));
        configurationResponse.setTitles(userFacade.getTitles());
        configurationResponse.setAnonymous(userFacade.isAnonymousUser());

        return ResponseEntity.ok(configurationResponse);
    }

}
