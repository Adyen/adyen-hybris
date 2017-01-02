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

import com.adyen.services.integration.data.AmountData;


public class AdyenModificationRequestBuilder
{
	private final AdyenModificationRequest request;

	/**
	 */
	public AdyenModificationRequestBuilder(final String merchantAccount, final String originalReference, final String reference)
	{
		super();
		this.request = new AdyenModificationRequest();
		request.setMerchantAccount(merchantAccount);
		request.setOriginalReference(originalReference);
		request.setReference(reference);
	}

	public AdyenModificationRequest modificationAmount(final String currency, final Double amount)
	{
		final AmountData modificationAmount = new AmountData();
		modificationAmount.setCurrency(currency);
		modificationAmount.setValue(new Integer((int) Math.round((amount.doubleValue() * 100))));
		request.setModificationAmount(modificationAmount);
		return request;
	}

	/**
	 * @return the request
	 */
	public AdyenModificationRequest getRequest()
	{
		return request;
	}

}
