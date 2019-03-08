/*
 *                        ######
 *                        ######
 *  ############    ####( ######  #####. ######  ############   ############
 *  #############  #####( ######  #####. ######  #############  #############
 *         ######  #####( ######  #####. ######  #####  ######  #####  ######
 *  ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 *  ###### ######  #####( ######  #####. ######  #####          #####  ######
 *  #############  #############  #############  #############  #####  ######
 *   ############   ############  #############   ############  #####  ######
 *                                       ######
 *                                #############
 *                                ############
 *
 *  Adyen Hybris Extension
 *
 *  Copyright (c) 2017 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.v6.impl.order.strategies;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.strategies.impl.DefaultCreateOrderFromCartStrategy;

public class AdyenCreateOrderFromCartStrategy extends DefaultCreateOrderFromCartStrategy {
    @Override
    protected String generateOrderCode(CartModel cart) {
        if (!cart.getCode().isEmpty()) {
            //Use the Cart code as order code
            return cart.getCode();
        }

        return super.generateOrderCode(cart);
    }
}
