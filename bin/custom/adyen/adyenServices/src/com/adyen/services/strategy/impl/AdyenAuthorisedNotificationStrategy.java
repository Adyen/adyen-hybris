/**
 *
 */
package com.adyen.services.strategy.impl;

import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.externaltax.ExternalTaxesService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.AdyenPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.AdyenPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Date;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.adyen.services.integration.data.request.AdyenNotificationRequest;


/**
 * @author delli
 *
 */
public class AdyenAuthorisedNotificationStrategy extends AbstractAdyenNotificationStrategy
{
	private static final Logger LOG = Logger.getLogger(AdyenAuthorisedNotificationStrategy.class);
	private OrderService orderService;
	private ModelService modelService;
	private CommerceCheckoutService commerceCheckoutService;
	private CartService cartService;
	private CalculationService calculationService;
	private PromotionsService promotionsService;
	private ExternalTaxesService externalTaxesService;

	@Override
	public AdyenPaymentTransactionEntryModel handleNotification(final AdyenNotificationRequest request)
	{
		final AdyenPaymentTransactionEntryModel entry = super.handleNotification(request);
		if (entry != null && TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus())
				&& PaymentTransactionType.AUTHORIZATION.equals(entry.getType()))
		{
			AbstractOrderModel abstractOrderModel = entry.getPaymentTransaction().getOrder();
			final AdyenPaymentInfoModel adyenPaymentInfoModel = (AdyenPaymentInfoModel) abstractOrderModel.getPaymentInfo();
			if (null != request.getNotificationItems() && request.getNotificationItems().size() > 0
					&& null != request.getNotificationItems().get(0).getNotificationRequestItem()
					&& null != request.getNotificationItems().get(0).getNotificationRequestItem().getAdditionalData()
					&& null != request.getNotificationItems().get(0).getNotificationRequestItem().getAdditionalData().get("authCode"))
			{
				entry.setAdyenAuthCode(request.getNotificationItems().get(0).getNotificationRequestItem().getAdditionalData()
						.get("authCode"));
				modelService.save(entry);
			}
			if (abstractOrderModel instanceof CartModel && adyenPaymentInfoModel.isUseHPP()
					&& TransactionStatusDetails.SUCCESFULL.name().equals(entry.getTransactionStatusDetails()))
			{
				final CartModel cartModel = (CartModel) abstractOrderModel;
				if (cartModel.getTotalPrice().doubleValue() == getAmount(request).doubleValue()
						&& cartModel.getCode().equals(getMerchantReference(request)))
				{
					if (StringUtils.isBlank(entry.getPaymentTransaction().getRequestId()))
					{
						LOG.info("Save original psp reference : " + getPspReference(request));
						final PaymentTransactionModel pt = entry.getPaymentTransaction();
						pt.setRequestId(getPspReference(request));
						getModelService().save(pt);
						getModelService().refresh(pt);
					}
					try
					{
						LOG.info("HPP payment success, but Hybris haven't received Adyen HPP response, place order here.");
						abstractOrderModel = placeOrder(cartModel, null);
					}
					catch (final InvalidCartException e)
					{
						LOG.error("Error placing HPP order when auth notification arrived", e);
					}
					afterPlaceOrder(cartModel, (OrderModel) abstractOrderModel);
				}
			}

			//Maybe at this moment order has not been submitted yet.
			if (abstractOrderModel instanceof OrderModel)
			{
				this.getOrderService().submitOrder((OrderModel) abstractOrderModel);
			}
			try
			{
				adyenPaymentInfoModel.setAdyenReason(request.getNotificationItems().get(0).getNotificationRequestItem().getReason());
				adyenPaymentInfoModel.setAdyenPaymentBrand(request.getNotificationItems().get(0).getNotificationRequestItem()
						.getPaymentMethod());
				modelService.save(adyenPaymentInfoModel);
			}
			catch (final Exception e)
			{
				LOG.error("Error when saving adyenPaymentInfoModel after Authorization.", e);
			}
		}
		return entry;

	}

	public OrderModel placeOrder(final CartModel cartModel, final SalesApplication salesApplication) throws InvalidCartException
	{
		ServicesUtil.validateParameterNotNull(cartModel, "Cart model cannot be null");
		if (calculationService.requiresCalculation(cartModel))
		{
			LOG.error(String.format("CartModel's [%s] calculated flag was false", new Object[]
			{ cartModel.getCode() }));
		}

		final CustomerModel customer = (CustomerModel) cartModel.getUser();
		ServicesUtil.validateParameterNotNull(customer, "Customer model cannot be null");

		final OrderModel orderModel = getOrderService().createOrderFromCart(cartModel);
		if (orderModel != null)
		{
			orderModel.setDate(new Date());
			orderModel.setSite(cartModel.getSite());
			orderModel.setStore(cartModel.getStore());
			orderModel.setLanguage(cartModel.getSite().getDefaultLanguage());

			if (salesApplication != null)
			{
				orderModel.setSalesApplication(salesApplication);
			}
			else
			{
				orderModel.setSalesApplication(SalesApplication.WEB);
			}
			getModelService().saveAll(new Object[]
			{ customer, orderModel });

			orderModel.setAllPromotionResults(new HashSet<PromotionResultModel>());

			if ((cartModel.getPaymentInfo() != null) && (cartModel.getPaymentInfo().getBillingAddress() != null))
			{
				final AddressModel billingAddress = cartModel.getPaymentInfo().getBillingAddress();
				orderModel.setPaymentAddress(billingAddress);
				orderModel.getPaymentInfo().setBillingAddress(getModelService().clone(billingAddress));
				getModelService().save(orderModel.getPaymentInfo());
			}
			getModelService().save(orderModel);

			getPromotionsService().transferPromotionsToOrder(cartModel, orderModel, false);
			try
			{
				getCalculationService().calculateTotals(orderModel, false);
				getExternalTaxesService().calculateExternalTaxes(orderModel);
			}
			catch (final CalculationException ex)
			{
				LOG.error("Failed to calculate order [" + orderModel + "]", ex);
			}

			getModelService().refresh(orderModel);
			getModelService().refresh(customer);
			getExternalTaxesService().clearSessionTaxDocument();
		}
		else
		{
			throw new IllegalArgumentException(String.format("Order was not properly created from cart %s", new Object[]
			{ cartModel.getCode() }));
		}

		return orderModel;
	}

	protected void afterPlaceOrder(final CartModel cartModel, final OrderModel orderModel)
	{
		if (orderModel != null)
		{
			getModelService().remove(cartModel);
			getModelService().refresh(orderModel);
		}
	}


	/**
	 * @return the commerceCheckoutService
	 */
	public CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}



	/**
	 * @param commerceCheckoutService
	 *           the commerceCheckoutService to set
	 */
	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
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

	/**
	 * @return the orderService
	 */
	public OrderService getOrderService()
	{
		return orderService;
	}

	/**
	 * @param orderService
	 *           the orderService to set
	 */
	public void setOrderService(final OrderService orderService)
	{
		this.orderService = orderService;
	}

	/**
	 * @return the modelService
	 */
	@Override
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
	 * @return the calculationService
	 */
	public CalculationService getCalculationService()
	{
		return calculationService;
	}

	/**
	 * @param calculationService
	 *           the calculationService to set
	 */
	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}

	/**
	 * @return the promotionsService
	 */
	public PromotionsService getPromotionsService()
	{
		return promotionsService;
	}

	/**
	 * @param promotionsService
	 *           the promotionsService to set
	 */
	public void setPromotionsService(final PromotionsService promotionsService)
	{
		this.promotionsService = promotionsService;
	}

	/**
	 * @return the externalTaxesService
	 */
	public ExternalTaxesService getExternalTaxesService()
	{
		return externalTaxesService;
	}

	/**
	 * @param externalTaxesService
	 *           the externalTaxesService to set
	 */
	public void setExternalTaxesService(final ExternalTaxesService externalTaxesService)
	{
		this.externalTaxesService = externalTaxesService;
	}


}
