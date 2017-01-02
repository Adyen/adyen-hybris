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

import java.util.List;
import java.util.Map;

import com.adyen.services.integration.data.AmountData;
import com.adyen.services.integration.data.ErrorType;
import com.adyen.services.integration.data.ResultCode;
import com.adyen.services.integration.data.request.AdyenFraudResultEntry;


public class AdyenPaymentResponse implements java.io.Serializable
{

	/**
	 * <i>Generated property</i> for <code>AdyenPaymentResponse.pspReference</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private String pspReference;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentResponse.authCode</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private String authCode;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentResponse.refusalReason</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private String refusalReason;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentResponse.md</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private String md;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentResponse.paRequest</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private String paRequest;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentResponse.resultCode</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private ResultCode resultCode;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentResponse.fraudResult</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private List<AdyenFraudResultEntry> fraudResult;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentResponse.issuerUrl</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private String issuerUrl;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentResponse.additionalData</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private Map<String, String> additionalData;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentResponse.dccAmount</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private AmountData dccAmount;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentResponse.dccSignature</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private String dccSignature;

	private String status;
	private String errorCode;
	private String message;
	private ErrorType errorType;

	public AdyenPaymentResponse()
	{
		// default constructor
	}


	public void setPspReference(final String pspReference)
	{
		this.pspReference = pspReference;
	}


	public String getPspReference()
	{
		return pspReference;
	}


	public void setAuthCode(final String authCode)
	{
		this.authCode = authCode;
	}


	public String getAuthCode()
	{
		return authCode;
	}


	public void setRefusalReason(final String refusalReason)
	{
		this.refusalReason = refusalReason;
	}


	public String getRefusalReason()
	{
		return refusalReason;
	}


	public void setMd(final String md)
	{
		this.md = md;
	}


	public String getMd()
	{
		return md;
	}


	public void setPaRequest(final String paRequest)
	{
		this.paRequest = paRequest;
	}


	public String getPaRequest()
	{
		return paRequest;
	}

	/**
	 * @return the resultCode
	 */
	public ResultCode getResultCode()
	{
		return resultCode;
	}

	/**
	 * @param resultCode
	 *           the resultCode to set
	 */
	public void setResultCode(final ResultCode resultCode)
	{
		this.resultCode = resultCode;
	}


	public void setFraudResult(final List<AdyenFraudResultEntry> fraudResult)
	{
		this.fraudResult = fraudResult;
	}


	public List<AdyenFraudResultEntry> getFraudResult()
	{
		return fraudResult;
	}


	public void setIssuerUrl(final String issuerUrl)
	{
		this.issuerUrl = issuerUrl;
	}


	public String getIssuerUrl()
	{
		return issuerUrl;
	}


	public void setAdditionalData(final Map<String, String> additionalData)
	{
		this.additionalData = additionalData;
	}


	public Map<String, String> getAdditionalData()
	{
		return additionalData;
	}


	public void setDccAmount(final AmountData dccAmount)
	{
		this.dccAmount = dccAmount;
	}


	public AmountData getDccAmount()
	{
		return dccAmount;
	}


	public void setDccSignature(final String dccSignature)
	{
		this.dccSignature = dccSignature;
	}


	public String getDccSignature()
	{
		return dccSignature;
	}


	/**
	 * @return the status
	 */
	public String getStatus()
	{
		return status;
	}


	/**
	 * @param status
	 *           the status to set
	 */
	public void setStatus(final String status)
	{
		this.status = status;
	}


	/**
	 * @return the errorCode
	 */
	public String getErrorCode()
	{
		return errorCode;
	}


	/**
	 * @param errorCode
	 *           the errorCode to set
	 */
	public void setErrorCode(final String errorCode)
	{
		this.errorCode = errorCode;
	}


	/**
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}


	/**
	 * @param message
	 *           the message to set
	 */
	public void setMessage(final String message)
	{
		this.message = message;
	}


	/**
	 * @return the errorType
	 */
	public ErrorType getErrorType()
	{
		return errorType;
	}


	/**
	 * @param errorType
	 *           the errorType to set
	 */
	public void setErrorType(final ErrorType errorType)
	{
		this.errorType = errorType;
	}
}