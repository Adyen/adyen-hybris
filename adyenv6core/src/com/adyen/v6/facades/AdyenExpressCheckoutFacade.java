package com.adyen.v6.facades;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;

public interface AdyenExpressCheckoutFacade {

    void expressPDPCheckout(AddressData addressData, String productCode) throws DuplicateUidException;

    void expressCartCheckout(AddressData addressData) throws DuplicateUidException;
}
