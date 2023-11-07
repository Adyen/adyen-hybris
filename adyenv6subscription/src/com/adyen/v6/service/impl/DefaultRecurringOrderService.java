package com.adyen.v6.service.impl;

import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;

public class DefaultRecurringOrderService {

    private ImpersonationService impersonationService;


    public void createRecurringOrderForSubscription(final AbstractOrderModel originalOrder) throws Exception
    {
        final ImpersonationService.Executor<OrderModel, Exception> executor = (ImpersonationService.Executor<OrderModel, Exception>) Registry
                .getApplicationContext()
                .getBean(SubscriptionOrderExecutor.BEAN_NAME, originalOrder);

        final ImpersonationContext context = new ImpersonationContext();
        context.setSite(originalOrder.getSite());
        context.setCurrency(originalOrder.getCurrency());
        context.setUser(originalOrder.getUser());
        context.setLanguage(((OrderModel) originalOrder).getLanguage());
        impersonationService.executeInContext(context, executor);
    }

    public void setImpersonationService(ImpersonationService impersonationService) {
        this.impersonationService = impersonationService;
    }
}
