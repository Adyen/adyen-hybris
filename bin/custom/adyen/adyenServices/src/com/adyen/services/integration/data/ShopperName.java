package com.adyen.services.integration.data;

import java.io.Serializable;


@SuppressWarnings("serial")
public class ShopperName implements Serializable
{
	private String firstName;
	private String lastName;

	public ShopperName()
	{
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

}
