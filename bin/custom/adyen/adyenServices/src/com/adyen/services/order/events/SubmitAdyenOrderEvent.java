/**
 *
 */
package com.adyen.services.order.events;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import java.io.Serializable;


/**
 * @author Kenneth Zhou
 *
 */
public class SubmitAdyenOrderEvent extends AbstractEvent {
	private OrderModel order;

	public SubmitAdyenOrderEvent()
	{
		super();
	}

	/**
	 * Attention: for backward compatibility this constructor invokes
	 *
	 * <pre>
	 * setOrder(source)
	 * </pre>
	 *
	 * in case the source object is a OrderModel!
	 */
	public SubmitAdyenOrderEvent(final Serializable source)
	{
		super(source);

		// compatibility!
		if (source instanceof OrderModel)
		{
			setOrder((OrderModel) source);
		}
	}


	public void setOrder(final OrderModel order)
	{
		this.order = order;
	}


	public OrderModel getOrder()
	{
		return order;
	}
}
