package com.adyen.v6.facades.impl;

import com.adyen.v6.facades.AdyenOrderFacade;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.lang.StringUtils;

import java.util.Comparator;
import java.util.List;

public class DefaultAdyenOrderFacade implements AdyenOrderFacade {
    private static final String ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE = "Order with guid %s not found for current user in current BaseStore";

    private BaseStoreService baseStoreService;
    private CheckoutCustomerStrategy checkoutCustomerStrategy;
    private CustomerAccountService customerAccountService;
    private UserService userService;

    @Override
    public String getPaymentStatus(final String orderCode, final Object sessionGuid) {
        OrderModel orderModel = getOrderDetailsForCodeInternal(orderCode, sessionGuid);
        List<PaymentTransactionModel> paymentTransactions = orderModel.getPaymentTransactions();
        if (paymentTransactions.isEmpty()) {
            return getMessageFromStatus("REVIEW");
        }
        return getStatus(paymentTransactions);
    }


    private OrderModel getOrderDetailsForCodeInternal(final String code, final Object sessionGuid) {
        final BaseStoreModel baseStoreModel = baseStoreService.getCurrentBaseStore();

        OrderModel orderModel = null;
        if (checkoutCustomerStrategy.isAnonymousCheckout()) {
            orderModel = customerAccountService.getOrderForCode(code, baseStoreModel);
            if (!StringUtils.substringBefore(orderModel.getUser().getUid(), "|")
                    .equals(sessionGuid)) {
                orderModel = null;
            }
        } else {
            try {
                orderModel = customerAccountService.getOrderForCode((CustomerModel) userService.getCurrentUser(), code,
                        baseStoreModel);
            } catch (final ModelNotFoundException e) {
                throw new UnknownIdentifierException(String.format(ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE, code));
            }
        }

        if (orderModel == null) {
            throw new UnknownIdentifierException(String.format(ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE, code));
        }
        return orderModel;
    }


    private String getStatus(List<PaymentTransactionModel> paymentTransactions) {
        List<PaymentTransactionModel> paymentTransactionModelList = paymentTransactions.stream()
                .sorted(Comparator.comparing(ItemModel::getCreationtime))
                .toList();

        List<PaymentTransactionEntryModel> paymentTransactionEntryModelList = paymentTransactionModelList.get(paymentTransactionModelList.size() - 1).getEntries().stream()
                .sorted(Comparator.comparing(ItemModel::getCreationtime))
                .toList();

        return getMessageFromStatus(paymentTransactionEntryModelList.get(paymentTransactionEntryModelList.size() - 1).getTransactionStatus());
    }

    private String getMessageFromStatus(String transactionStatus) {
        return switch (transactionStatus) {
            case "ACCEPTED" -> "completed";
            case "REJECTED" -> "rejected";
            case "REVIEW" -> "waiting";
            case "ERROR" -> "error";
            default -> "unknown";
        };
    }

    public void setBaseStoreService(BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public void setCheckoutCustomerStrategy(CheckoutCustomerStrategy checkoutCustomerStrategy) {
        this.checkoutCustomerStrategy = checkoutCustomerStrategy;
    }

    public void setCustomerAccountService(CustomerAccountService customerAccountService) {
        this.customerAccountService = customerAccountService;
    }


    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
