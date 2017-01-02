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


public class AmountData implements java.io.Serializable
{

	/**
	 * <i>Generated property</i> for <code>AmountData.value</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private Integer value;
	/**
	 * <i>Generated property</i> for <code>AmountData.currency</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private String currency;

	public AmountData()
	{
		// default constructor
	}

	/**
	 * @return the value
	 */
	public Integer getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *           the value to set
	 */
	public void setValue(final Integer value)
	{
		this.value = value;
	}




	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}


	public String getCurrency()
	{
		return currency;
	}


}