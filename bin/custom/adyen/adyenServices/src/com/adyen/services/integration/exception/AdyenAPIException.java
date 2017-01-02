/**
 *
 */
package com.adyen.services.integration.exception;

/**
 * @author delli
 *
 */
public class AdyenAPIException extends RuntimeException
{
	public AdyenAPIException(final String message)
	{
		super(message);
	}
}
