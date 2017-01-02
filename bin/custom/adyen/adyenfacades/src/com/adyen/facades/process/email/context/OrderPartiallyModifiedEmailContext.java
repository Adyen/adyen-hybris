/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package com.adyen.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.orderprocessing.model.OrderModificationProcessModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Velocity context for email about partially order modification.
 */
public class OrderPartiallyModifiedEmailContext extends AbstractEmailContext<OrderModificationProcessModel>
{
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
	private Converter<OrderModel, OrderData> orderConverter;
	private PriceDataFactory priceDataFactory;
	private OrderData orderData;
	private String orderCode;
	private String orderGuid;
	private boolean guest;
	private String storeName;
	private List<OrderEntryData> modifiedEntries;


	@Override
	public void init(final OrderModificationProcessModel orderProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(orderProcessModel, emailPageModel);
		orderData = getOrderConverter().convert(orderProcessModel.getOrder());
		orderCode = orderProcessModel.getOrder().getCode();
		orderGuid = orderProcessModel.getOrder().getGuid();
		guest = CustomerType.GUEST.equals(getCustomer(orderProcessModel).getType());
		storeName = orderProcessModel.getOrder().getStore().getName();
		fillModifiedEntries(orderProcessModel);
	}

	protected void fillModifiedEntries(final OrderModificationProcessModel orderProcessModel)
	{
		modifiedEntries = new ArrayList<OrderEntryData>();

		for (final OrderEntryModificationRecordEntryModel modificationEntry : orderProcessModel.getOrderModificationRecordEntry()
				.getOrderEntriesModificationEntries())
		{
			final OrderEntryModel orderEntryModel = modificationEntry.getOriginalOrderEntry();
			final OrderEntryData orderEntryData = getOrderEntryConverter().convert(orderEntryModel);
			modifiedEntries.add(orderEntryData);
		}
	}


	protected Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter()
	{
		return this.orderEntryConverter;
	}

	@Required
	public void setOrderEntryConverter(final Converter<AbstractOrderEntryModel, OrderEntryData> converter)
	{
		this.orderEntryConverter = converter;
	}

	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	@Override
	protected BaseSiteModel getSite(final OrderModificationProcessModel orderProcessModel)
	{
		return orderProcessModel.getOrder().getSite();
	}

	@Override
	protected CustomerModel getCustomer(final OrderModificationProcessModel orderProcessModel)
	{
		return (CustomerModel) orderProcessModel.getOrder().getUser();
	}

	protected Converter<OrderModel, OrderData> getOrderConverter()
	{
		return orderConverter;
	}

	@Required
	public void setOrderConverter(final Converter<OrderModel, OrderData> orderConverter)
	{
		this.orderConverter = orderConverter;
	}

	public OrderData getOrder()
	{
		return orderData;
	}

	public boolean isGuest()
	{
		return guest;
	}

	public String getOrderCode()
	{
		return orderCode;
	}

	public String getOrderGuid()
	{
		return orderGuid;
	}

	public String getStoreName()
	{
		return storeName;
	}

	public List<OrderEntryData> getModifiedEntries()
	{
		return modifiedEntries;
	}

	@Override
	protected LanguageModel getEmailLanguage(final OrderModificationProcessModel orderProcessModel)
	{
		return orderProcessModel.getOrder().getLanguage();
	}


}
