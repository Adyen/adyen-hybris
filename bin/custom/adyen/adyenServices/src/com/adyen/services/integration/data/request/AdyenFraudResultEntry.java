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

public class AdyenFraudResultEntry  implements java.io.Serializable 
{

	/** <i>Generated property</i> for <code>AdyenFraudResultEntry.checkId</code> property defined at extension <code>adyenServices</code>. */
	private Integer checkId;
	/** <i>Generated property</i> for <code>AdyenFraudResultEntry.name</code> property defined at extension <code>adyenServices</code>. */
	private String name;
	/** <i>Generated property</i> for <code>AdyenFraudResultEntry.accountScore</code> property defined at extension <code>adyenServices</code>. */
	private Integer accountScore;
		
	public AdyenFraudResultEntry()
	{
		// default constructor
	}
	
		
	public void setCheckId(final Integer checkId)
	{
		this.checkId = checkId;
	}
	
		
	public Integer getCheckId() 
	{
		return checkId;
	}
		
		
	public void setName(final String name)
	{
		this.name = name;
	}
	
		
	public String getName() 
	{
		return name;
	}
		
		
	public void setAccountScore(final Integer accountScore)
	{
		this.accountScore = accountScore;
	}
	
		
	public Integer getAccountScore() 
	{
		return accountScore;
	}
		
	
}