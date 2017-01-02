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
package com.adyen.services.integration.data.response;

import java.util.Date;
import java.util.List;

import com.adyen.services.integration.data.RecurringDetailDto;


public class AdyenListRecurringDetailsResponse implements java.io.Serializable
{

	private Date creationDate;

	private List<RecurringDetailDto> details;

	private String shopperReference;

	private String lastKnownShopperEmail;

	public AdyenListRecurringDetailsResponse()
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


	public void setDetails(final List<RecurringDetailDto> details)
	{
		this.details = details;
	}


	public List<RecurringDetailDto> getDetails()
	{
		return details;
	}


	public void setShopperReference(final String shopperReference)
	{
		this.shopperReference = shopperReference;
	}


	public String getShopperReference()
	{
		return shopperReference;
	}


	public void setLastKnownShopperEmail(final String lastKnownShopperEmail)
	{
		this.lastKnownShopperEmail = lastKnownShopperEmail;
	}


	public String getLastKnownShopperEmail()
	{
		return lastKnownShopperEmail;
	}


}