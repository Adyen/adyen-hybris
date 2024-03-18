package com.adyen.v6.facades;

import com.adyen.model.checkout.PaymentResponse;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.order.exceptions.CalculationException;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface AdyenExpressCheckoutFacade {

    PaymentResponse expressPDPCheckout(AddressData addressData, String productCode, String merchantId, String merchantName,
                                        String applePayToken, HttpServletRequest request) throws Exception;

    PaymentResponse expressCartCheckout(AddressData addressData, String merchantId, String merchantName,
                                         String applePayToken, HttpServletRequest request) throws Exception;

    Optional<ZoneDeliveryModeValueModel> getExpressDeliveryModePrice();

    void removeDeliveryModeFromSessionCart() throws CalculationException;
}
