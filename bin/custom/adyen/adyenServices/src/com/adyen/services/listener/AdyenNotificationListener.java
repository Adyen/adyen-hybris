/**
 *
 */
package com.adyen.services.listener;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import java.util.Map;

import org.apache.log4j.Logger;

import com.adyen.services.event.AdyenNotificationEvent;
import com.adyen.services.strategy.AdyenNotificationStrategy;


/**
 * @author delli
 *
 */
public class AdyenNotificationListener extends AbstractEventListener<AdyenNotificationEvent>
{
	private static final Logger LOG = Logger.getLogger(AdyenNotificationListener.class);
	private Map notificationHandlerMap;

	private AdyenNotificationStrategy defaultHandler;

	@Override
	protected void onEvent(final AdyenNotificationEvent event)
	{
		final String eventCode = getDefaultHandler().getEventCode(event.getNotificationRequest());
		AdyenNotificationStrategy handler = (AdyenNotificationStrategy) notificationHandlerMap.get(eventCode);
		if (handler == null)
		{
			handler = defaultHandler;
		}
		LOG.info(String.format("Listened %s event ,it'll be handled by %s ", eventCode, handler.getClass().getSimpleName()));
		handler.handleNotification(event.getNotificationRequest());
	}

	/**
	 * @return the notificationHandlerMap
	 */
	public Map getNotificationHandlerMap()
	{
		return notificationHandlerMap;
	}

	/**
	 * @param notificationHandlerMap
	 *           the notificationHandlerMap to set
	 */
	public void setNotificationHandlerMap(final Map notificationHandlerMap)
	{
		this.notificationHandlerMap = notificationHandlerMap;
	}

	/**
	 * @return the defaultHandler
	 */
	public AdyenNotificationStrategy getDefaultHandler()
	{
		return defaultHandler;
	}

	/**
	 * @param defaultHandler
	 *           the defaultHandler to set
	 */
	public void setDefaultHandler(final AdyenNotificationStrategy defaultHandler)
	{
		this.defaultHandler = defaultHandler;
	}

}
