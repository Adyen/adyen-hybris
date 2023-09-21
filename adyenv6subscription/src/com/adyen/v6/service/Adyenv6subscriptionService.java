/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adyen.v6.service;

public interface Adyenv6subscriptionService
{
	String getHybrisLogoUrl(String logoCode);

	void createLogo(String logoCode);
}
