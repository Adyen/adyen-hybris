/**
 *
 */
package com.adyen.services.integration.data.request;

import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.adyen.services.integration.data.NotificationItemDto;


/**
 * @author Kenneth Zhou
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdyenNotificationRequest implements java.io.Serializable
{
	private boolean live;

	private List<NotificationItemDto> notificationItems;

	/**
	 * @return the live
	 */
	public boolean isLive()
	{
		return live;
	}

	/**
	 * @param live
	 *           the live to set
	 */
	public void setLive(final boolean live)
	{
		this.live = live;
	}

	/**
	 * @return the notificationItems
	 */
	public List<NotificationItemDto> getNotificationItems()
	{
		return notificationItems;
	}

	/**
	 * @param notificationItems
	 *           the notificationItems to set
	 */
	public void setNotificationItems(final List<NotificationItemDto> notificationItems)
	{
		this.notificationItems = notificationItems;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuffer toString = new StringBuffer("[live=" + live);

		for (final Iterator iterator = notificationItems.iterator(); iterator.hasNext();)
		{
			final NotificationItemDto notificationItemDto = (NotificationItemDto) iterator.next();
			toString.append("[");
			toString.append(notificationItemDto.toString());
			toString.append("]");
		}
		toString.append("]");
		return toString.toString();
	}


}
