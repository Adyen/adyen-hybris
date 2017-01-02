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

import com.adyen.services.integration.data.AdditionalData;
import com.adyen.services.integration.data.AddressData;
import com.adyen.services.integration.data.AmountData;
import com.adyen.services.integration.data.BrowserInfo;
import com.adyen.services.integration.data.CardData;
import com.adyen.services.integration.data.Installments;
import com.adyen.services.integration.data.RecurringData;
import com.adyen.services.integration.data.ShopperName;


public class AdyenPaymentRequest implements java.io.Serializable
{
	public static final String SHOPPER_INTERACTION_DEFAULT_VALUE = "Ecommerce";

	private String reference;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentRequest.amount</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private AmountData amount;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentRequest.shopperIP</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private String shopperIP;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentRequest.shopperReference</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private String shopperReference;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentRequest.card</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private CardData card;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentRequest.fraudOffset</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private Integer fraudOffset;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentRequest.additionalData</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private AdditionalData additionalData;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentRequest.shopperEmail</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private String shopperEmail;
	/**
	 * <i>Generated property</i> for <code>AdyenPaymentRequest.merchantAccount</code> property defined at extension
	 * <code>adyenServices</code>.
	 */
	private String merchantAccount;

	private AddressData billingAddress;

	private RecurringData recurring;

	private String selectedRecurringDetailReference;
	private String shopperInteraction;
	private Installments installments;

	/* 3d secure */
	private BrowserInfo browserInfo;
	private String md;
	private String paResponse;

	/* boleto */
	private String selectedBrand;
	private String deliveryDate;
	private String shopperStatement;
	private String socialSecurityNumber;
	private ShopperName shopperName;

	public AdyenPaymentRequest()
	{
		// default constructor
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


	public void setAmount(final AmountData amount)
	{
		this.amount = amount;
	}


	public AmountData getAmount()
	{
		return amount;
	}


	public void setShopperIP(final String shopperIP)
	{
		this.shopperIP = shopperIP;
	}


	public String getShopperIP()
	{
		return shopperIP;
	}


	public void setShopperReference(final String shopperReference)
	{
		this.shopperReference = shopperReference;
	}


	public String getShopperReference()
	{
		return shopperReference;
	}


	public void setCard(final CardData card)
	{
		this.card = card;
	}


	public CardData getCard()
	{
		return card;
	}


	public void setFraudOffset(final Integer fraudOffset)
	{
		this.fraudOffset = fraudOffset;
	}


	public Integer getFraudOffset()
	{
		return fraudOffset;
	}


	public void setAdditionalData(final AdditionalData additionalData)
	{
		this.additionalData = additionalData;
	}


	public AdditionalData getAdditionalData()
	{
		return additionalData;
	}


	public void setShopperEmail(final String shopperEmail)
	{
		this.shopperEmail = shopperEmail;
	}


	public String getShopperEmail()
	{
		return shopperEmail;
	}


	public void setMerchantAccount(final String merchantAccount)
	{
		this.merchantAccount = merchantAccount;
	}


	public String getMerchantAccount()
	{
		return merchantAccount;
	}

	/**
	 * @return the billingAddress
	 */
	public AddressData getBillingAddress()
	{
		return billingAddress;
	}

	/**
	 * @param billingAddress
	 *           the billingAddress to set
	 */
	public void setBillingAddress(final AddressData billingAddress)
	{
		this.billingAddress = billingAddress;
	}

	/**
	 * @return the recurring
	 */
	public RecurringData getRecurring()
	{
		return recurring;
	}

	/**
	 * @param recurring
	 *           the recurring to set
	 */
	public void setRecurring(final RecurringData recurring)
	{
		this.recurring = recurring;
	}

	/**
	 * @return the selectedRecurringDetailReference
	 */
	public String getSelectedRecurringDetailReference()
	{
		return selectedRecurringDetailReference;
	}

	/**
	 * @param selectedRecurringDetailReference
	 *           the selectedRecurringDetailReference to set
	 */
	public void setSelectedRecurringDetailReference(final String selectedRecurringDetailReference)
	{
		this.selectedRecurringDetailReference = selectedRecurringDetailReference;
	}

	/**
	 * @return the shopperInteraction
	 */
	public String getShopperInteraction()
	{
		return shopperInteraction;
	}

	/**
	 * @param shopperInteraction
	 *           the shopperInteraction to set
	 */
	public void setShopperInteraction(final String shopperInteraction)
	{
		this.shopperInteraction = shopperInteraction;
	}

	/**
	 * @return the installments
	 */
	public Installments getInstallments()
	{
		return installments;
	}

	/**
	 * @param installments
	 *           the installments to set
	 */
	public void setInstallments(final Installments installments)
	{
		this.installments = installments;
	}

	/**
	 * @return the browserInfo
	 */
	public BrowserInfo getBrowserInfo()
	{
		return browserInfo;
	}

	/**
	 * @param browserInfo
	 *           the browserInfo to set
	 */
	public void setBrowserInfo(final BrowserInfo browserInfo)
	{
		this.browserInfo = browserInfo;
	}

	/**
	 * @return the md
	 */
	public String getMd()
	{
		return md;
	}

	/**
	 * @param md
	 *           the md to set
	 */
	public void setMd(final String md)
	{
		this.md = md;
	}

	/**
	 * @return the paResponse
	 */
	public String getPaResponse()
	{
		return paResponse;
	}

	/**
	 * @param paResponse
	 *           the paResponse to set
	 */
	public void setPaResponse(final String paResponse)
	{
		this.paResponse = paResponse;
	}

	/**
	 * @return the selectedBrand
	 */
	public String getSelectedBrand()
	{
		return selectedBrand;
	}

	/**
	 * @param selectedBrand
	 *           the selectedBrand to set
	 */
	public void setSelectedBrand(final String selectedBrand)
	{
		this.selectedBrand = selectedBrand;
	}

	/**
	 * @return the deliveryDate
	 */
	public String getDeliveryDate()
	{
		return deliveryDate;
	}

	/**
	 * @param deliveryDate
	 *           the deliveryDate to set
	 */
	public void setDeliveryDate(final String deliveryDate)
	{
		this.deliveryDate = deliveryDate;
	}

	/**
	 * @return the shopperStatement
	 */
	public String getShopperStatement()
	{
		return shopperStatement;
	}

	/**
	 * @param shopperStatement
	 *           the shopperStatement to set
	 */
	public void setShopperStatement(final String shopperStatement)
	{
		this.shopperStatement = shopperStatement;
	}

	/**
	 * @return the socialSecurityNumber
	 */
	public String getSocialSecurityNumber()
	{
		return socialSecurityNumber;
	}

	/**
	 * @param socialSecurityNumber
	 *           the socialSecurityNumber to set
	 */
	public void setSocialSecurityNumber(final String socialSecurityNumber)
	{
		this.socialSecurityNumber = socialSecurityNumber;
	}

	/**
	 * @return the shopperName
	 */
	public ShopperName getShopperName()
	{
		return shopperName;
	}

	/**
	 * @param shopperName
	 *           the shopperName to set
	 */
	public void setShopperName(final ShopperName shopperName)
	{
		this.shopperName = shopperName;
	}
}