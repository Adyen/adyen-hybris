package com.adyen.v6.repository;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.log4j.Logger;

/**
 * Repository class for BaseStore
 */
public class BaseStoreRepository extends AbstractRepository {
    private OrderRepository orderRepository;
    private static final Logger LOG = Logger.getLogger(BaseStoreRepository.class);

    public BaseStoreModel findByOrder(String code) {
        OrderModel order = orderRepository.getOrderModel(code);
        if (order != null) {
            return order.getStore();
        }

        LOG.error("Order with code: " + code + " was not found");
        return null;
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
}
