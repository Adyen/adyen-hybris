package com.adyen.services.integration.data.response;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import com.adyen.services.integration.data.AdditionalData;
import com.adyen.services.integration.data.ErrorType;


@SuppressWarnings("serial")
public class AdyenModificationResponse implements Serializable
{
	public final static String ADYEN_MODIFICATION_CANCEL_OR_REFUND_RESPONSE = "[cancelOrRefund-received]";
	public final static String ADYEN_MODIFICATION_CANCEL_RESPONSE = "[cancel-received]";
	public final static String ADYEN_MODIFICATION_REFUND_RESPONSE = "[refund-received]";
	public final static String ADYEN_MDIFICATION_CAPTURE_RESPONSE = "[capture-received]";

	private AdditionalData additionalData;
	private String pspReference;
	private String response;

	private String status;
	private String errorCode;
	private String message;
	private ErrorType errorType;

	public AdyenModificationResponse()
	{
	}

	/**
	 * @return the pspReference
	 */
	public String getPspReference()
	{
		return pspReference;
	}

	/**
	 * @param pspReference
	 *           the pspReference to set
	 */
	public void setPspReference(final String pspReference)
	{
		this.pspReference = pspReference;
	}

	/**
	 * @return the response
	 */
	public String getResponse()
	{
		return response;
	}

	/**
	 * @param response
	 *           the response to set
	 */
	public void setResponse(final String response)
	{
		this.response = response;
	}

	/**
	 * @return the additionalData
	 */
	public AdditionalData getAdditionalData()
	{
		return additionalData;
	}

	/**
	 * @param additionalData
	 *           the additionalData to set
	 */
	public void setAdditionalData(final AdditionalData additionalData)
	{
		this.additionalData = additionalData;
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

	private String toString(final Collection<?> collection, final int maxLen)
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (final Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++)
		{
			if (i > 0)
			{
				builder.append(", ");
			}
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
}
