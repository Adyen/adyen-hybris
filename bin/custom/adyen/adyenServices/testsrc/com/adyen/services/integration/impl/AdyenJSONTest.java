/**
 * 
 */
package com.adyen.services.integration.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import junit.framework.Assert;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.adyen.services.integration.data.request.AdyenNotificationRequest;


/**
 * @author Kenneth Zhou
 * 
 */
@UnitTest
public class AdyenJSONTest
{
	private ObjectMapper objectMapper;
	private String adyenNotificationString;

	@Before
	public void setUp() throws Exception
	{
		objectMapper = new ObjectMapper();
		adyenNotificationString = "{\"live\":\"false\",\"notificationItems\":[{\"NotificationRequestItem\":{\"amount\":{\"currency\":\"EUR\",\"value\":0},\"eventCode\":\"REPORT_AVAILABLE\",\"eventDate\":\"2015-07-15T04:50:56+02:00\",\"merchantAccountCode\":\"ScenericCOM\",\"merchantReference\":\"testMerchantRef1\",\"pspReference\":\"test_REPORT_AVAILABLE\",\"reason\":\"will contain the url to the report\",\"success\":\"true\"}}]}";
	}

	@Test
	public void testJsonParser() throws Exception
	{
		final AdyenNotificationRequest request = objectMapper.readValue(adyenNotificationString, AdyenNotificationRequest.class);
		Assert.assertNotNull(request);
	}
}
