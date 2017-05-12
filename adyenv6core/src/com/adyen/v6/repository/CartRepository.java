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
package com.adyen.v6.repository;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository class for Carts
 */
public class CartRepository extends AbstractRepository {
    private static final Logger LOG = Logger.getLogger(CartRepository.class);

    public CartModel getCart(String code) {
        final Map queryParams = new HashMap();
        queryParams.put("code", code);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(
                "SELECT {pk} FROM {" + CartModel._TYPECODE + "}"
                        + " WHERE {" + CartModel.CODE + "} = ?code",
                queryParams
        );

        LOG.debug("Finding cart with code: " + code);

        return (CartModel) getOneOrNull(query);
    }
}
