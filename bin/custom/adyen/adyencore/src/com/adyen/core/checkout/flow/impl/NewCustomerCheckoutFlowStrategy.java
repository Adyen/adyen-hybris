/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.adyen.core.checkout.flow.impl;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.acceleratorservices.checkout.flow.CheckoutFlowStrategy;
import de.hybris.platform.acceleratorservices.enums.CheckoutFlowEnum;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Prepared for customization flow strategy. Uses a {@link #isNewCustomer()} result to decide if use between
 * {@link #getDefaultStrategy()} and {@link #newCustomerStrategy} configured flow strategy.
 */
public class NewCustomerCheckoutFlowStrategy extends AbstractCheckoutFlowStrategy
{
	private static final Logger LOG = Logger.getLogger(NewCustomerCheckoutFlowStrategy.class);

	private UserService userService;
	private CustomerAccountService customerAccountService;
	private CheckoutFlowStrategy newCustomerStrategy;

	@Override
	public CheckoutFlowEnum getCheckoutFlow()
	{
		if (isNewCustomer())
		{
			return getNewCustomerStrategy().getCheckoutFlow();
		}
		return getDefaultStrategy().getCheckoutFlow();
	}

	/**
	 * Method which checks whether customer is new. In this case it just checks if the customer has a default shipping
	 * address.
	 * 
	 * @return Returns true if this is a new customer
	 */
	protected boolean isNewCustomer()
	{
		final UserModel user = getUserService().getCurrentUser();
		if (user instanceof CustomerModel)
		{
			return hasNoDefaultAddress((CustomerModel) user) || hasNoPaymentInfo((CustomerModel) user);
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Current user for session is empty or not a customer " + user);
		}
		return false;
	}

	protected boolean hasNoDefaultAddress(final CustomerModel user)
	{
		return getCustomerAccountService().getDefaultAddress(user) == null;
	}

	protected boolean hasNoPaymentInfo(final CustomerModel user)
	{
		return CollectionUtils.isEmpty(getCustomerAccountService().getCreditCardPaymentInfos(user, true));
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	protected CheckoutFlowStrategy getNewCustomerStrategy()
	{
		return newCustomerStrategy;
	}

	@Required
	public void setNewCustomerStrategy(final CheckoutFlowStrategy newCustomerStrategy)
	{
		this.newCustomerStrategy = newCustomerStrategy;
	}
}
