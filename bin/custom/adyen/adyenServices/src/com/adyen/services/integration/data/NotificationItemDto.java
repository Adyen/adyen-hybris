/**
 *
 */
package com.adyen.services.integration.data;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSetter;


/**
 * @author Kenneth Zhou
 *
 */
public class NotificationItemDto implements java.io.Serializable
{

	@JsonProperty(value = "NotificationRequestItem")
	private NotificationItem notificationRequestItem;

	/**
	 * @return the notificationRequestItem
	 */
	public NotificationItem getNotificationRequestItem()
	{
		return notificationRequestItem;
	}

	/**
	 * @param notificationRequestItem
	 *           the notificationRequestItem to set
	 */
	@JsonSetter(value = "NotificationRequestItem")
	public void setNotificationRequestItem(final NotificationItem notificationRequestItem)
	{
		this.notificationRequestItem = notificationRequestItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return notificationRequestItem.toString();
	}



}
