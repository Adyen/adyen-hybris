package com.adyen.v6.service.cart;

import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.Optional;

public interface AdyenCartService {

    AbstractOrderModel getAbstractOrderByCode(String code);
}
