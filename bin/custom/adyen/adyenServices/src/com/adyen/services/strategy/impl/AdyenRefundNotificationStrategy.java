/**
 *
 */
package com.adyen.services.strategy.impl;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.payment.model.AdyenPaymentTransactionEntryModel;
import de.hybris.platform.refund.RefundService;
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.adyen.services.integration.data.request.AdyenNotificationRequest;


/**
 * @author delli
 *
 */
public class AdyenRefundNotificationStrategy extends AbstractAdyenNotificationStrategy
{
	private ReturnService returnService;
	private RefundService refundService;
	private CatalogVersionService catalogVersionService;

	@Override
	public AdyenPaymentTransactionEntryModel handleNotification(final AdyenNotificationRequest request)
	{

		final AdyenPaymentTransactionEntryModel entry = super.handleNotification(request);

		if (entry != null)
		{
			//update refund entries
			final List<RefundEntryModel> refunds = new ArrayList<RefundEntryModel>(0);
			final ReturnRequestModel entryExample = new ReturnRequestModel();
			entryExample.setCode(getMerchantReference(request));
			final List<ReturnRequestModel> returnRequests = getFlexibleSearchService().getModelsByExample(entryExample);
			if (!CollectionUtils.isEmpty(returnRequests) && isSeccessful(request))
			{
				for (final ReturnRequestModel returnRequestModel : returnRequests)
				{
					for (final ReturnEntryModel refundEntryModel : returnRequestModel.getReturnEntries())
					{
						if (refundEntryModel.getStatus().getCode().equals(ReturnStatus.WAIT.getCode()))
						{
							refunds.add((RefundEntryModel) refundEntryModel);
						}
					}
				}

				if (!CollectionUtils.isEmpty(refunds))
				{
					getCatalogVersionService().setSessionCatalogVersion(
							returnRequests.get(0).getOrder().getEntries().get(0).getProduct().getCatalogVersion().getCatalog().getId(),
							"Online");
					for (final RefundEntryModel refundEntryModel : refunds)
					{
						refundEntryModel.setStatus(ReturnStatus.RECEIVED);
						refundEntryModel.setReceivedQuantity(refundEntryModel.getExpectedQuantity());
						getModelService().save(refundEntryModel);
					}
				}
			}
		}
		return entry;
	}

	/**
	 * @return the returnService
	 */
	public ReturnService getReturnService()
	{
		return returnService;
	}


	/**
	 * @param returnService
	 *           the returnService to set
	 */
	public void setReturnService(final ReturnService returnService)
	{
		this.returnService = returnService;
	}


	/**
	 * @return the refundService
	 */
	public RefundService getRefundService()
	{
		return refundService;
	}


	/**
	 * @param refundService
	 *           the refundService to set
	 */
	public void setRefundService(final RefundService refundService)
	{
		this.refundService = refundService;
	}

	/**
	 * @return the catalogVersionService
	 */
	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	/**
	 * @param catalogVersionService
	 *           the catalogVersionService to set
	 */
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}
}
