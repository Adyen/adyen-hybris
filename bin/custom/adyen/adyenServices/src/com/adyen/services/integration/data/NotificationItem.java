/**
 *
 */
package com.adyen.services.integration.data;

import java.util.Map;


/**
 * @author Kenneth Zhou
 *
 */
public class NotificationItem implements java.io.Serializable
{
	private AmountData amount;
	private String pspReference;
	private String eventDate;
	private String merchantAccountCode;
	private String reason;
	private String eventCode;
	private String merchantReference;
	private String[] operations;
	private boolean success;
	private Map<String, String> additionalData;
	private String paymentMethod;
	private String originalReference;

	public AmountData getAmount()
	{
		return amount;
	}

	public void setAmount(final AmountData amount)
	{
		this.amount = amount;
	}

	public String getPspReference()
	{
		return pspReference;
	}

	public void setPspReference(final String pspReference)
	{
		this.pspReference = pspReference;
	}

	public String getEventDate()
	{
		return eventDate;
	}

	public void setEventDate(final String eventDate)
	{
		this.eventDate = eventDate;
	}

	public String getMerchantAccountCode()
	{
		return merchantAccountCode;
	}

	public void setMerchantAccountCode(final String merchantAccountCode)
	{
		this.merchantAccountCode = merchantAccountCode;
	}

	public String getReason()
	{
		return reason;
	}

	public void setReason(final String reason)
	{
		this.reason = reason;
	}

	public String getEventCode()
	{
		return eventCode;
	}

	public void setEventCode(final String eventCode)
	{
		this.eventCode = eventCode;
	}

	public String getMerchantReference()
	{
		return merchantReference;
	}

	public void setMerchantReference(final String merchantReference)
	{
		this.merchantReference = merchantReference;
	}

	public String[] getOperations()
	{
		return operations;
	}

	public void setOperations(final String[] operations)
	{
		this.operations = operations;
	}

	public Map<String, String> getAdditionalData()
	{
		return additionalData;
	}

	public void setAdditionalData(final Map<String, String> additionalData)
	{
		this.additionalData = additionalData;
	}

	public String getPaymentMethod()
	{
		return paymentMethod;
	}

	public void setPaymentMethod(final String paymentMethod)
	{
		this.paymentMethod = paymentMethod;
	}



	/**
	 * @return the success
	 */
	public boolean isSuccess()
	{
		return success;
	}

	/**
	 * @param success
	 *           the success to set
	 */
	public void setSuccess(final boolean success)
	{
		this.success = success;
	}

	/**
	 * @return the originalReference
	 */
	public String getOriginalReference()
	{
		return originalReference;
	}

	/**
	 * @param originalReference
	 *           the originalReference to set
	 */
	public void setOriginalReference(final String originalReference)
	{
		this.originalReference = originalReference;
	}

	@Override
	public String toString()
	{
		return "[amount = " + amount + ", pspReference = " + pspReference + ", eventDate = " + eventDate
				+ ", merchantAccountCode = " + merchantAccountCode + ", reason = " + reason + ", eventCode = " + eventCode
				+ ", merchantReference = " + merchantReference + ", operations = " + operations + ", success = " + success
				+ ", additionalData = " + additionalData + ", paymentMethod = " + paymentMethod + "]";
	}

}
