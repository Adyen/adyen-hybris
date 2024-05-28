package com.adyen.commerce.controllerbase;

import com.adyen.commerce.exception.AdyenControllerException;
import com.adyen.commerce.facades.AdyenCheckoutApiFacade;
import com.adyen.commerce.response.PlaceOrderResponse;
import com.adyen.model.checkout.PaymentDetailsRequest;
import com.adyen.service.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.platform.commercefacades.order.data.OrderData;
import org.apache.log4j.Logger;

import static com.adyen.commerce.constants.AdyenwebcommonsConstants.CHECKOUT_ERROR_AUTHORIZATION_FAILED;

public abstract class PlaceOrderControllerBase {
    private static final Logger LOGGER = Logger.getLogger(PlaceOrderControllerBase.class);
    protected static final ObjectMapper objectMapper = new ObjectMapper();


    public PlaceOrderResponse handleAdditionalDetails(final PaymentDetailsRequest paymentDetailsRequest) {
        try {
            OrderData orderData = getAdyenCheckoutApiFacade().placeOrderWithAdditionalDetails(paymentDetailsRequest);
            PlaceOrderResponse placeOrderResponse = new PlaceOrderResponse();
            placeOrderResponse.setOrderNumber(orderData.getCode());
            return placeOrderResponse;
        } catch (ApiException e) {
            LOGGER.error("ApiException: " + e);
            throw new AdyenControllerException(CHECKOUT_ERROR_AUTHORIZATION_FAILED);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
            throw new AdyenControllerException(CHECKOUT_ERROR_AUTHORIZATION_FAILED);
        }
    }

    public abstract AdyenCheckoutApiFacade getAdyenCheckoutApiFacade();
}
