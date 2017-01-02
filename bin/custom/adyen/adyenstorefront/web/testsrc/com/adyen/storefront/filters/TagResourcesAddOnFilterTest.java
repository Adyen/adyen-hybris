/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package com.adyen.storefront.filters;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.mockito.Mockito;


/**
 * @author mariusz.donigiewicz
 * 
 */
@UnitTest
public class TagResourcesAddOnFilterTest extends AbstractAddOnFilterTest
{

	@Test
	public void testResourceForNotExistingTargetInSubFolder() throws ServletException, IOException
	{
		//create specific resource
		createResource(addOnSourceResource, "/a/b/c", "c.txt");

		prepareRequest("/a/addons/" + ADDONTWO_NAME + "/b/c/c.txt");
		prepareLocalContextPathRequest(STOREFRONT_NAME + "/a/addons/" + ADDONTWO_NAME + "/b/c/c.txt");

		filter.doFilter(request, response, filterChain);

		verifyFileCreated(webTargetResource, "/", "c.txt");
	}




	@Test
	public void testResourceForNotExistingTarget() throws ServletException, IOException
	{

		//create specific resource
		createResource(addOnSourceResource, "/", "c.txt");

		prepareRequest("/addons/" + ADDONTWO_NAME + "/c.txt");
		prepareLocalContextPathRequest(STOREFRONT_NAME + "/addons/" + ADDONTWO_NAME + "/c.txt");

		filter.doFilter(request, response, filterChain);


		verifyFileNotCreated(webExtensionPhysicalPath, "/web/webroot/WEB-INF/c.txt");
	}


	@Test
	public void testResourceForUpdateExistingTargetInSubFolder() throws ServletException, IOException, InterruptedException
	{

		//assume resource exists 
		createResource(webTargetResource, "/", "c.txt");
		waitASecond();
		//updating locally  
		createResourceWithContent(addOnSourceResource, "/a/b/c", "c.txt", "changed here");

		prepareRequest("/a/addons/" + ADDONTWO_NAME + "/b/c/c.txt");
		prepareLocalContextPathRequest(STOREFRONT_NAME + "/a/addons/" + ADDONTWO_NAME + "/b/c/c.txt");

		filter.doFilter(request, response, filterChain);


		verifyFileCreatedWithContent(webTargetResource, "/", "c.txt", "changed here");
	}

	@Override
	protected File createWebTargetDir()
	{
		return new File(rootSandboxDir, STOREFRONT_NAME + "/web" + getFolder() + "/a/addons/addontwo/b/c");
	}


	@Override
	protected String getFolder()
	{
		return WEB_INF_FOLDER;
	}


	@Override
	protected void prepareRequest(final String remotePath)
	{
		final File rootUrl = new File(webExtensionPhysicalPath, "/web/webroot/");
		Mockito.doReturn(new File(rootUrl, "/").getAbsolutePath()).when(filter).getAppContextFullPathNameFromRequest(request);
	}

}