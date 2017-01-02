/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 2015-6-29 10:14:52
 * ----------------------------------------------------------------
 *
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.adyen.services.integration.data;

import java.util.Date;
import java.util.Map;


public class RecurringDetailData implements java.io.Serializable
{

	private Date creationDate;

	private String recurringDetailReference;

	private CardData card;

	private String variant;

	private Map<String, String> additionalData;

	private String alias;

	private String aliasType;

	private String firstPspReference;

	private String paymentMethodVariant;


	public RecurringDetailData()
	{
		// default constructor
	}


	public void setCreationDate(final Date creationDate)
	{
		this.creationDate = creationDate;
	}


	public Date getCreationDate()
	{
		return creationDate;
	}


	public void setRecurringDetailReference(final String recurringDetailReference)
	{
		this.recurringDetailReference = recurringDetailReference;
	}


	public String getRecurringDetailReference()
	{
		return recurringDetailReference;
	}


	public void setCard(final CardData card)
	{
		this.card = card;
	}


	public CardData getCard()
	{
		return card;
	}


	public void setVariant(final String variant)
	{
		this.variant = variant;
	}


	public String getVariant()
	{
		return variant;
	}


	/**
	 * @return the additionalData
	 */
	public Map<String, String> getAdditionalData()
	{
		return additionalData;
	}


	/**
	 * @param additionalData
	 *           the additionalData to set
	 */
	public void setAdditionalData(final Map<String, String> additionalData)
	{
		this.additionalData = additionalData;
	}


	/**
	 * @return the alias
	 */
	public String getAlias()
	{
		return alias;
	}


	/**
	 * @param alias
	 *           the alias to set
	 */
	public void setAlias(final String alias)
	{
		this.alias = alias;
	}


	/**
	 * @return the aliasType
	 */
	public String getAliasType()
	{
		return aliasType;
	}


	/**
	 * @param aliasType
	 *           the aliasType to set
	 */
	public void setAliasType(final String aliasType)
	{
		this.aliasType = aliasType;
	}


	/**
	 * @return the firstPspReference
	 */
	public String getFirstPspReference()
	{
		return firstPspReference;
	}


	/**
	 * @param firstPspReference
	 *           the firstPspReference to set
	 */
	public void setFirstPspReference(final String firstPspReference)
	{
		this.firstPspReference = firstPspReference;
	}


	/**
	 * @return the paymentMethodVariant
	 */
	public String getPaymentMethodVariant()
	{
		return paymentMethodVariant;
	}


	/**
	 * @param paymentMethodVariant
	 *           the paymentMethodVariant to set
	 */
	public void setPaymentMethodVariant(final String paymentMethodVariant)
	{
		this.paymentMethodVariant = paymentMethodVariant;
	}




}