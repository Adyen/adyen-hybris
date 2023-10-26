package com.adyen.v6.facades;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;

import java.util.Optional;

public interface AdyenExpressCheckoutFacade {

    void expressPDPCheckout(AddressData addressData, String productCode, String merchantId, String merchantName) throws DuplicateUidException;

    void expressCartCheckout(AddressData addressData, String merchantId, String merchantName) throws DuplicateUidException;

    Optional<ZoneDeliveryModeValueModel> getExpressDeliveryModePrice();
}
