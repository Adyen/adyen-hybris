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
package com.adyen.fulfilmentprocess.warehouse;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commerceservices.model.PickUpDeliveryModeModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.warehouse.Process2WarehouseAdapter;
import de.hybris.platform.warehouse.Warehouse2ProcessAdapter;
import de.hybris.platform.warehouse.WarehouseConsignmentStatus;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class MockProcess2WarehouseAdapter implements Process2WarehouseAdapter
{
	private static final Logger LOG = Logger.getLogger(MockProcess2WarehouseAdapter.class);

	private ModelService modelService;
	private Warehouse2ProcessAdapter warehouse2ProcessAdapter;
	private TimeService timeService;

	@Override
	public void prepareConsignment(final ConsignmentModel consignment)
	{
		for (final ConsignmentEntryModel consignmentEntries : consignment.getConsignmentEntries())
		{
			consignmentEntries.setShippedQuantity(consignmentEntries.getQuantity());
		}
		consignment.setStatus(ConsignmentStatus.PICKPACK);
		getModelService().save(consignment);

		final Thread warehouse = new Thread(new Warehouse(Registry.getCurrentTenant().getTenantID(), consignment.getPk()
				.getLongValue()));
		warehouse.start();

		try
		{
			Thread.sleep(3000);
		}
		catch (final InterruptedException e)
		{
			//nothing to do
		}
	}

	public class Warehouse implements Runnable
	{
		private final long consignment;
		private final String tenant;

		public Warehouse(final String tenant, final long consignment)
		{
			super();

			this.consignment = consignment;
			this.tenant = tenant;
		}

		@Override
		public void run()
		{
			Registry.setCurrentTenant(Registry.getTenantByID(tenant));
			try
			{
				final ConsignmentModel model = getModelService().get(PK.fromLong(consignment));
				getWarehouse2ProcessAdapter().receiveConsignmentStatus(model, WarehouseConsignmentStatus.COMPLETE);
			}
			finally
			{
				Registry.unsetCurrentTenant();
			}
		}
	}


	@Override
	public void shipConsignment(final ConsignmentModel consignment)
	{
		if (consignment == null)
		{
			LOG.error("No consignment to ship");
		}
		else
		{
			if (consignment.getDeliveryMode() instanceof PickUpDeliveryModeModel)
			{
				consignment.setStatus(ConsignmentStatus.READY_FOR_PICKUP);
			}
			else
			{
				consignment.setStatus(ConsignmentStatus.SHIPPED);
			}
			consignment.setShippingDate(getTimeService().getCurrentTime());
			for (final ConsignmentEntryModel entry : consignment.getConsignmentEntries())
			{
				entry.setShippedQuantity(entry.getOrderEntry().getQuantity());
				getModelService().save(entry);
			}
			getModelService().save(consignment);
			if (LOG.isInfoEnabled())
			{
				LOG.info("Consignment [" + consignment.getCode() + "] shipped");
			}
		}
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setWarehouse2ProcessAdapter(final Warehouse2ProcessAdapter warehouse2ProcessAdapter)
	{
		this.warehouse2ProcessAdapter = warehouse2ProcessAdapter;
	}

	protected Warehouse2ProcessAdapter getWarehouse2ProcessAdapter()
	{
		return warehouse2ProcessAdapter;
	}

	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}
}
