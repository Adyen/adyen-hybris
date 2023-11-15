package com.adyen.v6.facades;

import com.adyen.model.checkout.PaymentsResponse;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface AdyenExpressCheckoutFacade {

    PaymentsResponse expressPDPCheckout(AddressData addressData, String productCode, String merchantId, String merchantName,
                                        String applePayToken, HttpServletRequest request) throws Exception;

    PaymentsResponse expressCartCheckout(AddressData addressData, String merchantId, String merchantName,
                                         String applePayToken, HttpServletRequest request) throws Exception;

    Optional<ZoneDeliveryModeValueModel> getExpressDeliveryModePrice();
}