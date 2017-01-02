/*
 * Copyright 2015 Willian Oki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.adyen.services.integration.data.request;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import com.adyen.services.integration.data.AdditionalData;
import com.adyen.services.integration.data.AmountData;


/**
 */
@SuppressWarnings("serial")
public class AdyenModificationRequest implements Serializable
{
	private AdditionalData additionalData;
	private String authorisationCode;
	private String merchantAccount;
	private AmountData modificationAmount;
	private String originalReference;
	private String reference;

	public AdyenModificationRequest()
	{
	}

	/**
	 * @return the merchantAccount
	 */
	public String getMerchantAccount()
	{
		return merchantAccount;
	}

	/**
	 * @param merchantAccount
	 *           the merchantAccount to set
	 */
	public void setMerchantAccount(final String merchantAccount)
	{
		this.merchantAccount = merchantAccount;
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

	/**
	 * @return the reference
	 */
	public String getReference()
	{
		return reference;
	}

	/**
	 * @param reference
	 *           the reference to set
	 */
	public void setReference(final String reference)
	{
		this.reference = reference;
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
	 * @return the authorisationCode
	 */
	public String getAuthorisationCode()
	{
		return authorisationCode;
	}

	/**
	 * @param authorisationCode
	 *           the authorisationCode to set
	 */
	public void setAuthorisationCode(final String authorisationCode)
	{
		this.authorisationCode = authorisationCode;
	}

	/**
	 * @return the modificationAmount
	 */
	public AmountData getModificationAmount()
	{
		return modificationAmount;
	}

	/**
	 * @param modificationAmount
	 *           the modificationAmount to set
	 */
	public void setModificationAmount(final AmountData modificationAmount)
	{
		this.modificationAmount = modificationAmount;
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
