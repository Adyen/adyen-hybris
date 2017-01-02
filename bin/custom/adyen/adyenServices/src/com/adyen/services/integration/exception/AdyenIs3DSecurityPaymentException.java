/**
 *
 */
package com.adyen.services.integration.exception;

import com.adyen.services.integration.data.response.AdyenPaymentResponse;


/**
 * @author delli
 *
 */
public class AdyenIs3DSecurityPaymentException extends RuntimeException
{
	AdyenPaymentResponse paymentResponse;

	/**
	 * @return the paymentResponse
	 */
	public AdyenPaymentResponse getPaymentResponse()
	{
		return paymentResponse;
	}

	/**
	 * @param paymentResponse
	 *           the paymentResponse to set
	 */
	public void setPaymentResponse(final AdyenPaymentResponse paymentResponse)
	{
		this.paymentResponse = paymentResponse;
	}

	/**
	 * @param paymentResponse
	 */
	public AdyenIs3DSecurityPaymentException(final AdyenPaymentResponse paymentResponse)
	{
		super();
		this.paymentResponse = paymentResponse;
	}

}
