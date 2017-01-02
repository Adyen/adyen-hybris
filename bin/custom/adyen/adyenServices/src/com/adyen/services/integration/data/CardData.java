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

import com.adyen.services.integration.data.AddressData;

public class CardData  implements java.io.Serializable 
{

	/** <i>Generated property</i> for <code>CardData.expiryYear</code> property defined at extension <code>adyenServices</code>. */
	private String expiryYear;
	/** <i>Generated property</i> for <code>CardData.expiryMonth</code> property defined at extension <code>adyenServices</code>. */
	private String expiryMonth;
	/** <i>Generated property</i> for <code>CardData.cvc</code> property defined at extension <code>adyenServices</code>. */
	private String cvc;
	/** <i>Generated property</i> for <code>CardData.billingAddress</code> property defined at extension <code>adyenServices</code>. */
	private AddressData billingAddress;
	/** <i>Generated property</i> for <code>CardData.number</code> property defined at extension <code>adyenServices</code>. */
	private String number;
	/** <i>Generated property</i> for <code>CardData.holderName</code> property defined at extension <code>adyenServices</code>. */
	private String holderName;
		
	public CardData()
	{
		// default constructor
	}
	
		
	public void setExpiryYear(final String expiryYear)
	{
		this.expiryYear = expiryYear;
	}
	
		
	public String getExpiryYear() 
	{
		return expiryYear;
	}
		
		
	public void setExpiryMonth(final String expiryMonth)
	{
		this.expiryMonth = expiryMonth;
	}
	
		
	public String getExpiryMonth() 
	{
		return expiryMonth;
	}
		
		
	public void setCvc(final String cvc)
	{
		this.cvc = cvc;
	}
	
		
	public String getCvc() 
	{
		return cvc;
	}
		
		
	public void setBillingAddress(final AddressData billingAddress)
	{
		this.billingAddress = billingAddress;
	}
	
		
	public AddressData getBillingAddress() 
	{
		return billingAddress;
	}
		
		
	public void setNumber(final String number)
	{
		this.number = number;
	}
	
		
	public String getNumber() 
	{
		return number;
	}
		
		
	public void setHolderName(final String holderName)
	{
		this.holderName = holderName;
	}
	
		
	public String getHolderName() 
	{
		return holderName;
	}
		
	
}