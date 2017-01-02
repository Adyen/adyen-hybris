/**
 *
 */
package com.adyen.services.order.strategies.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.strategies.SubmitOrderStrategy;
import de.hybris.platform.servicelayer.event.EventService;

import org.springframework.beans.factory.annotation.Required;

import com.adyen.services.order.events.SubmitAdyenOrderEvent;


/**
 * @author Kenneth Zhou
 *
 */
public class EventPublishingSubmitOrderStrategy implements SubmitOrderStrategy
{

	private EventService eventService;

	@Override
	public void submitOrder(final OrderModel order)
	{
		eventService.publishEvent(new SubmitAdyenOrderEvent(order));
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

}
