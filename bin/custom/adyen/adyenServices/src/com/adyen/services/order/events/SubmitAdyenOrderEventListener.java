/**
 *
 */
package com.adyen.services.order.events;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Kenneth Zhou
 *
 */
public class SubmitAdyenOrderEventListener extends AbstractSiteEventListener<SubmitAdyenOrderEvent>
{
	private static final Logger LOG = Logger.getLogger(SubmitAdyenOrderEventListener.class);

	private BusinessProcessService businessProcessService;
	private BaseStoreService baseStoreService;
	private ModelService modelService;

	/**
	 * @return the businessProcessService
	 */
	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * @param businessProcessService
	 *           the businessProcessService to set
	 */
	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	/**
	 * @return the baseStoreService
	 */
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	protected void onSiteEvent(final SubmitAdyenOrderEvent event)
	{
		final OrderModel order = event.getOrder();
		LOG.info("Order " + "["+order.getCode()+"]" + "Submitted.");
		ServicesUtil.validateParameterNotNullStandardMessage("event.order", order);

		// Try the store set on the Order first, then fallback to the session
		BaseStoreModel store = order.getStore();
		if (store == null)
		{
			store = getBaseStoreService().getCurrentBaseStore();
		}

		if (store == null)
		{
			LOG.warn("Unable to start fulfilment process for order [" + order.getCode()
					+ "]. Store not set on Order and no current base store defined in session.");
		}
		else
		{
			boolean isOrderAuthorised = false;
			boolean isPaymentPending = false;
			for (final PaymentTransactionModel transaction : order.getPaymentTransactions())
			{
				for (final PaymentTransactionEntryModel entry : transaction.getEntries())
				{
					if (entry.getType().equals(PaymentTransactionType.AUTHORIZATION))
					{
						if (TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus())
								&& TransactionStatusDetails.SUCCESFULL.name().equals(entry.getTransactionStatusDetails()))
						{
							isOrderAuthorised = true;
							break;
						}
						if (TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus())
								&& "UNSUCCESSFULL".equals(entry.getTransactionStatusDetails()))
						{
							isOrderAuthorised = true;
							break;
						}
					}
					if (entry.getType().equals(PaymentTransactionType.HPP_RESULT))
					{
						if ("PENDING".equals(entry.getTransactionStatus()))
						{
							isPaymentPending = true;
						}
					}
				}
			}
			if (isOrderAuthorised)
			{
				final String fulfilmentProcessDefinitionName = store.getSubmitOrderProcessCode();
				if (fulfilmentProcessDefinitionName == null || fulfilmentProcessDefinitionName.isEmpty())
				{
					LOG.warn("Unable to start fulfilment process for order [" + order.getCode() + "]. Store [" + store.getUid()
							+ "] has missing SubmitOrderProcessCode");
				}
				else
				{
					final String processCode = fulfilmentProcessDefinitionName + "-" + order.getCode() + "-"
							+ System.currentTimeMillis();
					final OrderProcessModel businessProcessModel = getBusinessProcessService().createProcess(processCode,
							fulfilmentProcessDefinitionName);
					businessProcessModel.setOrder(order);
					getModelService().save(businessProcessModel);
					getBusinessProcessService().startProcess(businessProcessModel);
					if (LOG.isInfoEnabled())
					{
						LOG.info(String.format("Started the process %s", processCode));
					}
				}
			}
			else
			{
				if (isPaymentPending)
				{
					LOG.info(String
							.format("Order HPP Payment is PENDING, order process won't be initialized, change order status to PAYMENT_PENDING."));
					order.setStatus(OrderStatus.PAYMENT_PENDING);
					getModelService().save(order);
					getModelService().refresh(order);
				}
			}
		}
	}

	@Override
	protected boolean shouldHandleEvent(final SubmitAdyenOrderEvent event)
	{
		final OrderModel order = event.getOrder();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order", order);
		final BaseSiteModel site = order.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
		return SiteChannel.B2C.equals(site.getChannel());
	}
}
