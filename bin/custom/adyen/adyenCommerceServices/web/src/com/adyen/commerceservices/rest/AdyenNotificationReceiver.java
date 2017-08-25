/**
 *
 */
package com.adyen.commerceservices.rest;

import de.hybris.platform.servicelayer.event.EventService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.adyen.services.event.AdyenNotificationEvent;
import com.adyen.services.integration.data.NotificationItemDto;
import com.adyen.services.integration.data.request.AdyenNotificationRequest;
import com.adyen.services.listener.AdyenNotificationListener;


/**
 * @author Kenneth Zhou
 *
 */
@Controller
@RequestMapping(value = "/notification/receiver")
public class AdyenNotificationReceiver
{
	/**
	 *
	 */
	private static final String ACCEPTED = "[accepted]";

	private static final Logger LOG = Logger.getLogger(AdyenNotificationReceiver.class);

	@Resource
	private EventService eventService;
	@Resource
	private AdyenNotificationListener adyenNotificationListener;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public String onReceive(@RequestBody final String requestString)
	{
		LOG.info("Received Adyen notification:" + requestString);
		final ObjectMapper om = new ObjectMapper();
		AdyenNotificationRequest request = null;
		try
		{
			om.setSerializationInclusion(Inclusion.NON_NULL);
			om.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
			request = om.readValue(requestString, AdyenNotificationRequest.class);

            getEventService().registerEventListener(getAdyenNotificationListener());

            //Publish a separate event for every notification item
			for(NotificationItemDto notificationItemDto : request.getNotificationItems()) {
				AdyenNotificationRequest emittedRequest = new AdyenNotificationRequest();
				emittedRequest.setLive(request.isLive());
				List<NotificationItemDto> notificationItemDtoList = new ArrayList<>();
				notificationItemDtoList.add(notificationItemDto);
				emittedRequest.setNotificationItems(notificationItemDtoList);

				LOG.info("Publish Adyen event.");
				getEventService().publishEvent(new AdyenNotificationEvent(emittedRequest));
			}
		}
		catch (final JsonParseException e)
		{
			LOG.error("JsonParseException", e);
		}
		catch (final JsonMappingException e)
		{
			LOG.error("JsonMappingException", e);
		}
		catch (final IOException e)
		{
			LOG.error("IOException", e);
		}
		if (null == request)
		{
			LOG.info("Error parsing request.");
		}
		return ACCEPTED;
	}

	/**
	 * @return the eventService
	 */
	public EventService getEventService()
	{
		return eventService;
	}

	/**
	 * @param eventService
	 *           the eventService to set
	 */
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	/**
	 * @return the adyenNotificationListener
	 */
	public AdyenNotificationListener getAdyenNotificationListener()
	{
		return adyenNotificationListener;
	}

	/**
	 * @param adyenNotificationListener
	 *           the adyenNotificationListener to set
	 */
	public void setAdyenNotificationListener(final AdyenNotificationListener adyenNotificationListener)
	{
		this.adyenNotificationListener = adyenNotificationListener;
	}
}
