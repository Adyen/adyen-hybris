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

import org.codehaus.jackson.annotate.JsonProperty;


public class AdditionalData implements java.io.Serializable
{
	@JsonProperty(value = "card.encrypted.json")
	private String cardEncryptedJson;


	/**
	 * "boletobancario.url" :
	 * "https://test.adyen.com/hpp/generationBoleto.shtml?data=AgABAQAk5QYbuNl9TiV63c5KeLTvXpB03Ml3krv%2FtwYj....2FFq3920vVWRd5HKHT96mCdVXyo4Gzq%2BTkzNbmT2XcgFf%2FwhYkU4%3D"
	 * ,<br>
	 * "boletobancario.data" :
	 * "AgABAQAk5QYbuNl9TiV63c5KeLTvXpB03Ml3krv/twYj....2FFq3920vVWRd5HKHT96mCdVXyo4Gzq+TkzNbmT2XcgFf/whYkU4=",<br>
	 * "boletobancario.expirationDate" : "2013-08-19",<br>
	 * "boletobancario.dueDate" : "2013-08-12"<br>
	 */
	@JsonProperty(value = "boletobancario.url")
	private String boletobancarioUrl;
	@JsonProperty(value = "boletobancario.data")
	private String boletobancarioData;
	@JsonProperty(value = "boletobancario.expirationDate")
	private String boletobancarioExpirationDate;
	@JsonProperty(value = "boletobancario.dueDate")
	private String boletobancarioDueDate;


	public AdditionalData()
	{
		// default constructor
	}


	public void setCardEncryptedJson(final String cardEncryptedJson)
	{
		this.cardEncryptedJson = cardEncryptedJson;
	}


	public String getCardEncryptedJson()
	{
		return cardEncryptedJson;
	}

	/**
	 * @return the boletobancarioUrl
	 */
	public String getBoletobancarioUrl()
	{
		return boletobancarioUrl;
	}


	/**
	 * @param boletobancarioUrl
	 *           the boletobancarioUrl to set
	 */
	public void setBoletobancarioUrl(final String boletobancarioUrl)
	{
		this.boletobancarioUrl = boletobancarioUrl;
	}


	/**
	 * @return the boletobancarioData
	 */
	public String getBoletobancarioData()
	{
		return boletobancarioData;
	}


	/**
	 * @param boletobancarioData
	 *           the boletobancarioData to set
	 */
	public void setBoletobancarioData(final String boletobancarioData)
	{
		this.boletobancarioData = boletobancarioData;
	}


	/**
	 * @return the boletobancarioExpirationDate
	 */
	public String getBoletobancarioExpirationDate()
	{
		return boletobancarioExpirationDate;
	}


	/**
	 * @param boletobancarioExpirationDate
	 *           the boletobancarioExpirationDate to set
	 */
	public void setBoletobancarioExpirationDate(final String boletobancarioExpirationDate)
	{
		this.boletobancarioExpirationDate = boletobancarioExpirationDate;
	}


	/**
	 * @return the boletobancarioDueDate
	 */
	public String getBoletobancarioDueDate()
	{
		return boletobancarioDueDate;
	}


	/**
	 * @param boletobancarioDueDate
	 *           the boletobancarioDueDate to set
	 */
	public void setBoletobancarioDueDate(final String boletobancarioDueDate)
	{
		this.boletobancarioDueDate = boletobancarioDueDate;
	}


}