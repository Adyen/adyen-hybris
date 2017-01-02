/**
 *
 */
package com.adyen.services.event;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import com.adyen.services.integration.data.request.AdyenNotificationRequest;


/**
 * @author delli
 *
 */
public class AdyenNotificationEvent extends AbstractEvent
{
	private final AdyenNotificationRequest notificationRequest;

	public AdyenNotificationEvent(final AdyenNotificationRequest notificationRequest)
	{
		super();
		this.notificationRequest = notificationRequest;
	}

	public AdyenNotificationRequest getNotificationRequest()
	{
		return notificationRequest;
	}


}
