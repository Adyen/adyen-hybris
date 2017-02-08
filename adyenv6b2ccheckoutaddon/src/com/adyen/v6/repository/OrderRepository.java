package com.adyen.v6.repository;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository class for Orders
 */
public class OrderRepository extends AbstractRepository {
    private static final Logger LOG = Logger.getLogger(OrderRepository.class);

    public OrderModel getOrderModel(String code) {
        final Map queryParams = new HashMap();
        queryParams.put("code", code);
        final FlexibleSearchQuery selectOrderQuery = new FlexibleSearchQuery(
                "SELECT {pk} FROM {" + OrderModel._TYPECODE + "}"
                        + " WHERE {" + OrderModel.CODE + "} = ?code",
                queryParams
        );

        LOG.info("Finding order with code: " + code);

        return (OrderModel) getOneOrNull(selectOrderQuery);
    }
}
