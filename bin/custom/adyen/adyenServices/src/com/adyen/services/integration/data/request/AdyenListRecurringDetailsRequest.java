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
package com.adyen.services.integration.data.request;

import com.adyen.services.integration.data.RecurringData;

public class AdyenListRecurringDetailsRequest  implements java.io.Serializable 
{

	/** <i>Generated property</i> for <code>AdyenListRecurringDetailsRequest.shopperReference</code> property defined at extension <code>adyenServices</code>. */
	private String shopperReference;
	/** <i>Generated property</i> for <code>AdyenListRecurringDetailsRequest.recurring</code> property defined at extension <code>adyenServices</code>. */
	private RecurringData recurring;
	/** <i>Generated property</i> for <code>AdyenListRecurringDetailsRequest.merchantAccount</code> property defined at extension <code>adyenServices</code>. */
	private String merchantAccount;
		
	public AdyenListRecurringDetailsRequest()
	{
		// default constructor
	}
	
		
	public void setShopperReference(final String shopperReference)
	{
		this.shopperReference = shopperReference;
	}
	
		
	public String getShopperReference() 
	{
		return shopperReference;
	}
		
		
	public void setRecurring(final RecurringData recurring)
	{
		this.recurring = recurring;
	}
	
		
	public RecurringData getRecurring() 
	{
		return recurring;
	}
		
		
	public void setMerchantAccount(final String merchantAccount)
	{
		this.merchantAccount = merchantAccount;
	}
	
		
	public String getMerchantAccount() 
	{
		return merchantAccount;
	}
		
	
}