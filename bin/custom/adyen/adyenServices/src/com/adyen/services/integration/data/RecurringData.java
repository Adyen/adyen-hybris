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

public class RecurringData implements java.io.Serializable
{

	/**
	 * "recurring": { "contract" : "RECURRING, ONECLICK" }
	 */
	private String contract;

	public RecurringData()
	{
		// default constructor
	}

	/**
	 * @return the contract
	 */
	public String getContract()
	{
		return contract;
	}

	/**
	 * @param contract
	 *           the contract to set
	 */
	public void setContract(final String contract)
	{
		this.contract = contract;
	}




}