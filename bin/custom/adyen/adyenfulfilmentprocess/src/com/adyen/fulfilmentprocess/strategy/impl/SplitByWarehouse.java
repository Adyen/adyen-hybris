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
package com.adyen.fulfilmentprocess.strategy.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.ordersplitting.strategy.SplittingStrategy;
import de.hybris.platform.ordersplitting.strategy.impl.OrderEntryGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class SplitByWarehouse implements SplittingStrategy
{
	private static final String WAREHOUSE_LIST_NAME = "WAREHOUSE_LIST";

	protected List<OrderEntryGroup> splitForWarehouses(final OrderEntryGroup orderEntryList)
	{
		final List<OrderEntryGroup> result = new ArrayList<OrderEntryGroup>();

		//list of orderEntry - todoList
		final OrderEntryGroup todoEntryList = orderEntryList.getEmpty();

		//List of working elements
		OrderEntryGroup workingOrderEntryList = sortOrderEntryBeforeWarehouseSplitting(orderEntryList);

		// list of entries that can't be performed by any warehouse
		final OrderEntryGroup emptyOrderEntryList = orderEntryList.getEmpty();

		do
		{
			//clear need here before normal proceeding 
			todoEntryList.clear();

			//list of warehouse after retailAll of prev. orderEntries
			List<WarehouseModel> tmpWarehouseResult = null;
			//list of orderEntries that can be realized by tmpWarehouseResult
			final OrderEntryGroup tmpOrderEntryResult = orderEntryList.getEmpty();


			for (final AbstractOrderEntryModel orderEntry : workingOrderEntryList)
			{
				final List<WarehouseModel> currentPossibleWarehouses = getPossibleWarehouses(orderEntry);

				// no warehouse can solve order entry
				if (currentPossibleWarehouses.isEmpty())
				{
					emptyOrderEntryList.add(orderEntry);
				}
				else
				{
					//first time we wish to store all warehouses
					if (tmpWarehouseResult != null)
					{
						//if not first time we take retainAll 
						currentPossibleWarehouses.retainAll(tmpWarehouseResult);
					}

					// if this orderEntry can't be realized whit previous set
					if (currentPossibleWarehouses.isEmpty())
					{
						// add entry to todoList
						todoEntryList.add(orderEntry);
					}
					else
					{
						//we store list after retainAll and add orderEntry to tmpResult
						tmpWarehouseResult = currentPossibleWarehouses;
						tmpOrderEntryResult.add(orderEntry);
					}

				}
			}

			if (!tmpOrderEntryResult.isEmpty())
			{
				//add chosen one to result
				tmpOrderEntryResult.setParameter(WAREHOUSE_LIST_NAME, tmpWarehouseResult);
				result.add(tmpOrderEntryResult);
			}
			//starting process with new (not split yet) orderEntry List
			//remember to make clean at begin of new loop - if will not done unfinished loop will appear
			workingOrderEntryList = todoEntryList.getEmpty();
			workingOrderEntryList.addAll(todoEntryList);
		}
		//still something to do
		while (!todoEntryList.isEmpty());

		//entries for which warehouse can't be chosen 
		if (!emptyOrderEntryList.isEmpty())
		{
			result.add(emptyOrderEntryList);
		}

		return result;
	}

	protected List<WarehouseModel> getPossibleWarehouses(final AbstractOrderEntryModel orderEntry)
	{
		final List<WarehouseModel> possibleWarehouses = new ArrayList<WarehouseModel>();

		if (orderEntry.getOrder().getStore() != null)
		{
			possibleWarehouses.addAll(orderEntry.getDeliveryPointOfService() == null ? orderEntry.getOrder().getStore()
					.getWarehouses() : orderEntry.getDeliveryPointOfService().getWarehouses());
		}

		return possibleWarehouses;
	}

	/**
	 * Choose best warehouse this function is called by getWarehouseList after we have set of possible warehouses.
	 * 
	 * @param orderEntries
	 *           the order entries
	 * 
	 * @return the warehouse model
	 */
	@SuppressWarnings("unchecked")
	protected WarehouseModel chooseBestWarehouse(final OrderEntryGroup orderEntries)
	{
		final List<WarehouseModel> warehouses = (List<WarehouseModel>) orderEntries.getParameter(WAREHOUSE_LIST_NAME);
		if ((warehouses == null) || (warehouses.isEmpty()))
		{
			return null;
		}
		final Random rnd = new Random(new Date().getTime());

		//basic solution is to random
		return warehouses.get(rnd.nextInt(warehouses.size()));
	}

	/**
	 * Sort order entry before warehouse splitting.
	 * 
	 * @param listOrderEntry
	 *           the list order entry
	 * 
	 * @return the list< order entry model>
	 */
	protected OrderEntryGroup sortOrderEntryBeforeWarehouseSplitting(final OrderEntryGroup listOrderEntry)
	{
		// basic - not sort
		return listOrderEntry;
	}

	@Override
	public List<OrderEntryGroup> perform(final List<OrderEntryGroup> orderEntryGroup)
	{
		final List<OrderEntryGroup> result = new ArrayList<OrderEntryGroup>();

		for (final OrderEntryGroup orderEntry : orderEntryGroup)
		{
			for (final OrderEntryGroup tmpOrderEntryGroup : splitForWarehouses(orderEntry))
			{
				result.add(tmpOrderEntryGroup);
			}
		}

		return result;
	}

	@Override
	public void afterSplitting(final OrderEntryGroup group, final ConsignmentModel createdOne)
	{
		createdOne.setWarehouse(chooseBestWarehouse(group));
	}
}
