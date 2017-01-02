/**
 *
 */
package com.adyen.services.impl;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.AdyenPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.payment.model.AdyenPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.adyen.services.AdyenPaymentService;
import com.adyen.services.integration.AdyenService;
import com.adyen.services.integration.data.request.AdyenModificationRequestBuilder;
import com.adyen.services.integration.data.request.AdyenPaymentRequest;
import com.adyen.services.integration.data.request.AdyenPaymentRequestBuilder;
import com.adyen.services.integration.data.response.AdyenModificationResponse;


/**
 * @author Kenneth Zhou
 *
 */
public class DefaultAdyenPaymentService extends DefaultPaymentServiceImpl implements AdyenPaymentService
{
	private static final Logger LOG = Logger.getLogger(DefaultAdyenPaymentService.class);
	private ModelService modelService;
	private AdyenService adyenService;
	private UserService userService;
	private CMSSiteService cmsSiteService;
	private CommonI18NService commonI18NService;
	private FlexibleSearchService flexibleSearchService;
	private CartService cartService;

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
	@Override
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	@Override
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the cmsSiteService
	 */
	public CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	/**
	 * @param cmsSiteService
	 *           the cmsSiteService to set
	 */
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	/**
	 * @return the adyenService
	 */
	public AdyenService getAdyenService()
	{
		return adyenService;
	}

	/**
	 * @param adyenService
	 *           the adyenService to set
	 */
	public void setAdyenService(final AdyenService adyenService)
	{
		this.adyenService = adyenService;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
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
	@Override
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	@Override
	public PaymentTransactionEntryModel authorize(final BigDecimal amount,
			final Currency currency, final CartModel cartModel, final AdyenPaymentInfoModel adyenPaymentInfoModel,
			final String securityCode, final String userAgent, final String accept)
	{
		final PaymentTransactionModel transaction = (PaymentTransactionModel) this.modelService
				.create(PaymentTransactionModel.class);
		final String merchantTransactionCode = cartModel.getCode();
		transaction.setCode(merchantTransactionCode);
		transaction.setOrder(cartModel);
		modelService.save(transaction);
		getModelService().saveAll(new Object[]
		{ cartModel, transaction });
		final AdyenPaymentRequest paymentRequest = createAuthoriseRequest(merchantTransactionCode, amount, currency,
				cartModel.getDeliveryAddress(), adyenPaymentInfoModel, securityCode, userAgent, accept);

		return adyenService.authorise(transaction, paymentRequest, false);
	}

	@Override
	public PaymentTransactionEntryModel authorize3DSecure(final CartModel cartModel, final String paResponse,
			final String md, final String shopperIp, final String userAgent, final String accept)
	{
		final PaymentTransactionModel transaction = (PaymentTransactionModel) this.modelService
				.create(PaymentTransactionModel.class);
		transaction.setCode(cartModel.getCode());
		transaction.setOrder(cartModel);
		modelService.save(transaction);
		getModelService().saveAll(new Object[]
		{ cartModel, transaction });
		final AdyenPaymentRequestBuilder builder = new AdyenPaymentRequestBuilder();
		builder.create3DRequest(cartModel.getCode(), shopperIp, getCmsSiteService().getCurrentSite().getAdyenMerchantAccount(),
				paResponse, md);
		builder.browserInfo(userAgent, accept);
		final AdyenPaymentRequest paymentRequest = builder.getPaymentRequest();
		return adyenService.authorise(transaction, paymentRequest, true);
	}

	@Override
	public PaymentTransactionEntryModel cancelOrRefund(final PaymentTransactionModel transaction, final String reference,
			final CurrencyModel currency, final Double amount)
	{
		String merchantAccount = null;
		if (transaction.getOrder().getSite() instanceof CMSSiteModel)
		{
			merchantAccount = ((CMSSiteModel) transaction.getOrder().getSite()).getAdyenMerchantAccount();
		}
		final AdyenModificationRequestBuilder builder = new AdyenModificationRequestBuilder(merchantAccount,
				transaction.getRequestId(), reference);

		if (amount != null)
		{
			builder.modificationAmount(currency.getIsocode(), amount);
		}
		return adyenService.cancelOrRefund(transaction, builder.getRequest());
	}



	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.adyen.services.AdyenPaymentService#maybeClearAuthorizeHistory(de.hybris.platform.core.model.order.CartModel)
	 */
	@Override
	public void maybeClearAuthorizeHistory(final CartModel cart)
	{
		if (CollectionUtils.isNotEmpty(cart.getPaymentTransactions()))
		{
			for (final PaymentTransactionModel pt : cart.getPaymentTransactions())
			{
				clearPaymentTransaction(pt);
			}
		}
		else
		{
			try
			{
				final PaymentTransactionModel model = getFlexibleSearchService().getModelByExample(
						new PaymentTransactionModel(cart.getCode()));
				if (model != null)
				{
					clearPaymentTransaction(model);
				}
			}
			catch (final Exception e)
			{
				LOG.error(e.getMessage());
			}
		}
	}

	/**
	 * @param cartCode
	 * @param pspReference
	 * @return
	 */
	@Override
	public PaymentTransactionModel createTransaction(final String cartCode)
	{
		final PaymentTransactionModel transaction = (PaymentTransactionModel) this.modelService
				.create(PaymentTransactionModel.class);
		transaction.setCode(cartCode);
		transaction.setPaymentProvider("Adyen");
		this.modelService.save(transaction);

		final CartModel cartModel = getCartService().getSessionCart();
		transaction.setOrder(cartModel);
		this.modelService.saveAll(new Object[]
		{ transaction, cartModel });
		return transaction;
	}

	@Override
	public AdyenPaymentTransactionEntryModel createHPPAuthorisePTE(final String cartCode, final String pspReference,
			final String authResult)
	{
		final CartModel cartModel = getCartService().getSessionCart();
		createHPPAuthorisePTE(cartModel, pspReference, authResult);
		return null;
	}

	@Override
	public AdyenPaymentTransactionEntryModel createHPPAuthorisePTE(final AbstractOrderModel order, final String pspReference,
			final String authResult)
	{
		if (order != null)
		{
			final PaymentTransactionModel transaction = order.getPaymentTransactions().get(0);
			transaction.setRequestId(pspReference);

			final AdyenPaymentTransactionEntryModel entry = (AdyenPaymentTransactionEntryModel) this.modelService
					.create(AdyenPaymentTransactionEntryModel.class);
			entry.setCode(getAdyenEntryCode(transaction));
			entry.setTime(new Date());
			entry.setPaymentTransaction(transaction);
			entry.setRequestId(pspReference);
			entry.setTransactionStatus(authResult);
			entry.setType(PaymentTransactionType.HPP_RESULT);

			entry.setCurrency(order.getCurrency());
			entry.setAmount(new BigDecimal(order.getTotalPrice().doubleValue()));
			transaction.setOrder(order);
			this.modelService.saveAll(new Object[]
			{ transaction, order, entry });
			this.modelService.refresh(order);
			this.modelService.refresh(transaction);
			return entry;
		}
		return null;
	}

	private String getAdyenEntryCode(final PaymentTransactionModel transaction)
	{
		if (transaction.getEntries() == null)
		{
			return transaction.getCode() + "-1";
		}
		return transaction.getCode() + "-" + (transaction.getEntries().size() + 1);
	}

	@Override
	public void clearPaymentTransaction(final String code)
	{
		try
		{
			final PaymentTransactionModel transaction = getFlexibleSearchService()
					.getModelByExample(new PaymentTransactionModel(code));
			if (transaction != null)
			{
				for (final PaymentTransactionEntryModel pte : transaction.getEntries())
				{
					getModelService().remove(pte);
				}
				getModelService().remove(transaction);
			}
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage());
		}
	}
	/**
	 * @param transaction
	 */
	public void clearPaymentTransaction(final PaymentTransactionModel transaction)
	{
		for (final PaymentTransactionEntryModel pte : transaction.getEntries())
		{
			getModelService().remove(pte);
		}
		getModelService().remove(transaction);
	}

	/**
	 * @param amount
	 * @param currency
	 * @param deliveryAddress
	 * @param adyenPaymentInfo
	 * @return
	 */
	private AdyenPaymentRequest createAuthoriseRequest(final String cartGuid, final BigDecimal amount, final Currency currency,
			final AddressModel deliveryAddress, final AdyenPaymentInfoModel adyenPaymentInfo, final String securityCode,
			final String userAgent, final String accept)
	{
		final AdyenPaymentRequestBuilder builder = new AdyenPaymentRequestBuilder();
		builder.createBaseRequest(cartGuid, ((CustomerModel) getUserService().getCurrentUser()).getUid(),
				adyenPaymentInfo.getShopperIp(), getCmsSiteService().getCurrentSite().getAdyenMerchantAccount(),
				((CustomerModel) getUserService().getCurrentUser()).getCustomerID());
		builder.amount(amount, currency);
		if (StringUtils.isEmpty(adyenPaymentInfo.getSocialSecurityNumber()))
		{
			builder.cardEncryptedJson(adyenPaymentInfo.getCardEncryptedJson());

			final boolean recurringAndOneclickCondition = adyenPaymentInfo.isSavePayment()
					|| adyenPaymentInfo.getRecurringDetailReference() != null;
			builder.recurringAndOneclick(recurringAndOneclickCondition);

			final boolean updateRecurringCondtion = adyenPaymentInfo.getRecurringDetailReference() != null;
			builder.cardNeedUpdate(updateRecurringCondtion, securityCode, adyenPaymentInfo.getValidToMonth(),
					adyenPaymentInfo.getValidToYear(), adyenPaymentInfo.getRecurringDetailReference());

			builder.installments(adyenPaymentInfo.getInstallments());
			//3d-authorise need brownserInfo
			builder.browserInfo(userAgent, accept);
		}
		else
		{
			//boleto
			builder.boletoInfo(adyenPaymentInfo.getFirstName(), adyenPaymentInfo.getLastName(),
					adyenPaymentInfo.getSocialSecurityNumber(), adyenPaymentInfo.getSelectedBrand(),
					adyenPaymentInfo.getShopperStatement());
		}
		return builder.getPaymentRequest();
	}


	@Override
	public PaymentTransactionEntryModel capture(final PaymentTransactionModel transaction, final String reference)
	{
		PaymentTransactionEntryModel auth = null;
		for (final Iterator iterator = transaction.getEntries().iterator(); iterator.hasNext();)
		{
			final PaymentTransactionEntryModel pte = (PaymentTransactionEntryModel) iterator.next();
			if (pte.getType().equals(PaymentTransactionType.AUTHORIZATION))
			{
				auth = pte;
				break;
			}
		}
		if (auth == null)
		{
			throw new AdapterException("Could not capture without authorization");
		}
		final String newEntryCode = getNewTransactionEntryCode(transaction, PaymentTransactionType.CAPTURE_REQUESTED);
		String merchantAccount = null;
		if (transaction.getOrder().getSite() instanceof CMSSiteModel)
		{
			merchantAccount = ((CMSSiteModel) transaction.getOrder().getSite()).getAdyenMerchantAccount();
		}
		final AdyenModificationRequestBuilder builder = new AdyenModificationRequestBuilder(merchantAccount,
				transaction.getRequestId(), reference);
		builder.modificationAmount(transaction.getOrder().getCurrency().getIsocode(), transaction.getOrder().getTotalPrice());
		final AdyenModificationResponse result = adyenService.capturePayment(builder.getRequest(), (CMSSiteModel) transaction
				.getOrder().getSite());
		if (StringUtils.isNotBlank(result.getResponse())
				&& AdyenModificationResponse.ADYEN_MDIFICATION_CAPTURE_RESPONSE.equals(result.getResponse()))
		{
			final PaymentTransactionEntryModel entry = (PaymentTransactionEntryModel) modelService
					.create(PaymentTransactionEntryModel.class);
			entry.setCurrency(transaction.getOrder().getCurrency());
			entry.setAmount(new BigDecimal(transaction.getOrder().getTotalPrice().doubleValue()));
			entry.setType(PaymentTransactionType.CAPTURE_REQUESTED);
			entry.setRequestId(result.getPspReference());
			entry.setTime(new Date());
			entry.setPaymentTransaction(transaction);
			entry.setTransactionStatusDetails(result.getResponse());
			entry.setCode(newEntryCode);
			modelService.save(entry);
			return entry;
		}
		return null;
	}

	private String getNewTransactionEntryCode(final PaymentTransactionModel transaction,
			final PaymentTransactionType paymentTransactionType)
	{
		if (transaction.getEntries() == null)
		{
			return (new StringBuilder(String.valueOf(transaction.getCode()))).append("-").append(paymentTransactionType.getCode())
					.append("-1").toString();
		}
		else
		{
			return (new StringBuilder(String.valueOf(transaction.getCode()))).append("-").append(paymentTransactionType.getCode())
					.append("-").append(transaction.getEntries().size() + 1).toString();
		}
	}

	@Override
	public AbstractOrderModel getOrderByPSPReference(final String pspReference)
	{
		final PaymentTransactionEntryModel entryExample = new PaymentTransactionEntryModel();
		entryExample.setRequestId(pspReference);
		final List<PaymentTransactionEntryModel> entries = getFlexibleSearchService().getModelsByExample(entryExample);
		if (CollectionUtils.isNotEmpty(entries))
		{
			for (final PaymentTransactionEntryModel paymentTransactionEntry : entries)
			{
				if (StringUtils.isEmpty(paymentTransactionEntry.getVersionID()))
				{
					return paymentTransactionEntry.getPaymentTransaction().getOrder();
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.adyen.services.AdyenPaymentService#buildHPPOpenInvoiceData(java.lang.String)
	 */
	@Override
	public LinkedHashMap<String, String> buildHPPOpenInvoiceData()
	{
		final LinkedHashMap<String, String> openInvoiceData = new LinkedHashMap<String, String>();
		final CartModel cartModel = getCartService().getSessionCart();
		int i = 1;

		for (final AbstractOrderEntryModel entry : cartModel.getEntries())
		{

			openInvoiceData.put("openinvoicedata.line" + i + ".currencyCode", cartModel.getCurrency().getIsocode());
			openInvoiceData.put("openinvoicedata.line" + i + ".description", entry.getProduct().getName());


			openInvoiceData.put("openinvoicedata.line" + i + ".itemAmount",
					new BigDecimal(100).multiply(new BigDecimal(entry.getBasePrice().toString())).intValue() + "");



			final double taxValue = TaxValue.sumAbsoluteTaxValues(entry.getTaxValues());
			openInvoiceData.put("openinvoicedata.line" + i + ".itemVatAmount",
					new BigDecimal(100).multiply(new BigDecimal(Double.toString(taxValue))).intValue() + "");

			final double taxRate = TaxValue.sumRelativeTaxValues(entry.getTaxValues());
			openInvoiceData.put("openinvoicedata.line" + i + ".itemVatPercentage",
					new BigDecimal(100).multiply(new BigDecimal(Double.toString(taxRate))).intValue() + "");

			openInvoiceData.put("openinvoicedata.line" + i + ".numberOfItems", entry.getQuantity() + "");

			openInvoiceData.put("openinvoicedata.line" + i + ".vatCategory", "None");



			i++;
		}
		openInvoiceData.put("openinvoicedata.merchantData", "{}");
		openInvoiceData.put("openinvoicedata.numberOfLines", cartModel.getEntries().size() + "");
		openInvoiceData.put("openinvoicedata.refundDescription", cartModel.getCode());

		return openInvoiceData;
	}

	@Override
	public LinkedHashMap<String, String> buildHPPOpenInvoiceData(final String merchantData)
	{
		final LinkedHashMap<String, String> openInvoiceData = buildHPPOpenInvoiceData();
		openInvoiceData.put("openinvoicedata.merchantData", merchantData);
		return openInvoiceData;
	}


}
