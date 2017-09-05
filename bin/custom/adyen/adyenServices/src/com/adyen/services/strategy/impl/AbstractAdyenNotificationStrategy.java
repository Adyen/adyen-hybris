/**
 *
 */
package com.adyen.services.strategy.impl;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.AdyenPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.adyen.services.integration.data.request.AdyenNotificationRequest;
import com.adyen.services.strategy.AdyenNotificationStrategy;


/**
 * @author delli
 *
 */
public class AbstractAdyenNotificationStrategy implements AdyenNotificationStrategy
{
	private ModelService modelService;
	private FlexibleSearchService flexibleSearchService;
	private Map<String, String> eventCode2Type;

	private static final Logger LOG = Logger.getLogger(AbstractAdyenNotificationStrategy.class);

	@Override
	public AdyenPaymentTransactionEntryModel handleNotification(final AdyenNotificationRequest request)
	{
		AdyenPaymentTransactionEntryModel entry = null;
		if (!getEventCode2Type().containsKey(getEventCode(request)))
		{
			LOG.info("UNKOWN NOTIFICATION :" + getEventCode(request));
		}
		try
		{
			final AdyenPaymentTransactionEntryModel entryExample = new AdyenPaymentTransactionEntryModel();
			entryExample.setRequestId(getPspReference(request));
			entryExample.setType(getType(request));
			if (!isExist(entryExample))
			{
				entry = createTransactionEntry(request);
			}
		}
		catch (final Exception e)
		{
			LOG.error("create transaction entry...", e);
		}
		return entry;
	}

	@Override
	public PaymentTransactionModel fetchTransaction(final AdyenNotificationRequest request)
	{
		final PaymentTransactionEntryModel entryExample = new PaymentTransactionEntryModel();
		entryExample.setRequestId(getPspReference(request));
		PaymentTransactionEntryModel entry = null;
		final List<PaymentTransactionEntryModel> entries = getFlexibleSearchService().getModelsByExample(entryExample);
		if (CollectionUtils.isNotEmpty(entries))
		{
			for (final PaymentTransactionEntryModel paymentTransactionEntry : entries)
			{
				if (paymentTransactionEntry != null && StringUtils.isEmpty(paymentTransactionEntry.getVersionID()))
				{
					//Get the order's transaction entry only
					if (entries.size() > 1 && paymentTransactionEntry.getPaymentTransaction() != null
							&& paymentTransactionEntry.getPaymentTransaction().getOrder() != null
							&& paymentTransactionEntry.getPaymentTransaction().getOrder() instanceof CartModel)
					{
						continue;
					}
					LOG.debug("Transaction entry exists for the PSP - " + getPspReference(request));
					entry = paymentTransactionEntry;

					if (entry.getPaymentTransaction() != null && entry.getPaymentTransaction().getOrder() != null)
					{
						LOG.debug("Payment transaction is associated with - "
										 + entry.getPaymentTransaction().getOrder().getClass().getName());
					}
					else
					{
						LOG.debug("Payment transaction is not associated with any order-");
					}

					break;
				}
			}
		}
		else
		{
			//Only when authorization notification come before we get authorization response.
			LOG.info("Only when authorization notification come before we get authorization response.");
			final String orderNumber = getMerchantReference(request);
			if (StringUtils.isNotBlank(orderNumber))
			{
				final CartModel cartExample = new CartModel();
				cartExample.setCode(orderNumber);
				final List<CartModel> carts = getFlexibleSearchService().getModelsByExample(cartExample);
				if (CollectionUtils.isNotEmpty(carts))
				{
					return carts.get(0).getPaymentTransactions().get(0);
				}
			}
		}
		return (entry == null) ? null : entry.getPaymentTransaction();
	}

	protected boolean isExist(final AdyenPaymentTransactionEntryModel example)
	{
		try
		{
			getFlexibleSearchService().getModelByExample(example);
		}
		catch (final ModelNotFoundException e)
		{
			return false;
		}
		return true;
	}

	@Override
	public AdyenPaymentTransactionEntryModel createTransactionEntry(final AdyenNotificationRequest request)
	{
		final AdyenPaymentTransactionEntryModel entry = getModelService().create(AdyenPaymentTransactionEntryModel.class);
		final PaymentTransactionModel transaction = fetchTransaction(request);
		if (null == transaction)
		{
			return null;
		}
		entry.setPaymentTransaction(transaction);
		if (isSeccessful(request))
		{
			entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
			entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
		}
		else
		{
			entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
			entry.setTransactionStatusDetails("UNSUCCESSFULL");
		}
		entry.setRequestId(getPspReference(request));
		entry.setTime(new Date());
		entry.setCurrency(transaction.getOrder().getCurrency());
		entry.setAmount(getAmount(request));
		entry.setType(getType(request));
		entry.setCode(transaction.getCode() + "-"
				+ ((transaction.getEntries() == null) ? "-1" : (transaction.getEntries().size() + 1) + ""));

		getModelService().saveAll(transaction, entry);
		getModelService().refresh(transaction);
		getModelService().refresh(entry);
		return entry;
	}

	protected boolean isSeccessful(final AdyenNotificationRequest request)
	{
		try
		{
			if (request.getNotificationItems().get(0).getNotificationRequestItem().isSuccess())
			{
				return true;
			}
		}
		catch (final Exception e)
		{
			LOG.error("get success status", e);
		}
		return false;
	}

	protected String getPspReference(final AdyenNotificationRequest request)
	{
		try
		{
			return request.getNotificationItems().get(0).getNotificationRequestItem().getPspReference();
		}
		catch (final Exception e)
		{
			LOG.error("get psp reference", e);
		}
		return null;
	}

	protected String getMerchantReference(final AdyenNotificationRequest request)
	{
		try
		{
			return request.getNotificationItems().get(0).getNotificationRequestItem().getMerchantReference();
		}
		catch (final Exception e)
		{
			LOG.error("get merchant reference", e);
		}
		return null;
	}

	protected BigDecimal getAmount(final AdyenNotificationRequest request)
	{
		try
		{
			return new BigDecimal(((double) request.getNotificationItems().get(0).getNotificationRequestItem().getAmount()
					.getValue().intValue()) / 100);
		}
		catch (final Exception e)
		{
			LOG.error("get amount", e);
		}
		return null;
	}

	protected PaymentTransactionType getType(final AdyenNotificationRequest request)
	{
		try
		{
			final String typeStr = getEventCode2Type().get(getEventCode(request));
			for (final PaymentTransactionType type : PaymentTransactionType.values())
			{
				if (typeStr.equalsIgnoreCase(type.getCode()))
				{
					return type;
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("get notification type", e);
		}
		return null;
	}

	/**
	 * @param request
	 * @return
	 */
	@Override
	public String getEventCode(final AdyenNotificationRequest request)
	{
		try
		{
			final String eventCode = request.getNotificationItems().get(0).getNotificationRequestItem().getEventCode();
			return eventCode;
		}
		catch (final Exception e)
		{
			LOG.error("get event code", e);
		}
		return null;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/**
	 * @return the eventCode2Type
	 */
	public Map<String, String> getEventCode2Type()
	{
		return eventCode2Type;
	}

	/**
	 * @param eventCode2Type
	 *           the eventCode2Type to set
	 */
	public void setEventCode2Type(final Map<String, String> eventCode2Type)
	{
		this.eventCode2Type = eventCode2Type;
	}

}
