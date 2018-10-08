/*
 *                        ######
 *                        ######
 *  ############    ####( ######  #####. ######  ############   ############
 *  #############  #####( ######  #####. ######  #############  #############
 *         ######  #####( ######  #####. ######  #####  ######  #####  ######
 *  ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 *  ###### ######  #####( ######  #####. ######  #####          #####  ######
 *  #############  #############  #############  #############  #####  ######
 *   ############   ############  #############   ############  #####  ######
 *                                       ######
 *                                #############
 *                                ############
 *
 *  Adyen Hybris Extension
 *
 *  Copyright (c) 2017 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.v6.repository;

import com.adyen.v6.model.NotificationItemModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repository class for NotificationItems
 */
public class NotificationItemRepository extends AbstractRepository {
    private static final Logger LOG = Logger.getLogger(NotificationItemRepository.class);

    public List<NotificationItemModel> getNonProcessedNotifications() {
        //Select the non-processed notifications
        final FlexibleSearchQuery selectNonProcessedNotificationsQuery = new FlexibleSearchQuery(
                "SELECT {pk} FROM {" + NotificationItemModel._TYPECODE + "}"
                        + " WHERE {" + NotificationItemModel.PROCESSEDAT + "} IS NULL ORDER BY {pk} ASC"
        );
        selectNonProcessedNotificationsQuery.setCount(1000);
        LOG.debug("Querying notification items");
        List<NotificationItemModel> nonProcessedNotifications = flexibleSearchService
                .search(selectNonProcessedNotificationsQuery)
                .getResult()
                .stream()
                .map(element->(NotificationItemModel) element)
                .collect(Collectors.toList());

        LOG.debug(nonProcessedNotifications.size() + " items found ");

        return nonProcessedNotifications;
    }

    /**
     * Checks if the notification is already processed
     *
     * @param pspReference Notification psp reference
     * @param eventCode Notification eventCode
     * @param success Notification success
     * @return true|false
     */
    public boolean notificationProcessed(String pspReference, String eventCode, boolean success) {
        final Map queryParams = new HashMap();
        queryParams.put("pspReference", pspReference);
        queryParams.put("eventCode", eventCode);
        queryParams.put("success", success);

        final FlexibleSearchQuery selectNonProcessedNotificationsQuery = new FlexibleSearchQuery(
                "SELECT {pk} FROM {" + NotificationItemModel._TYPECODE + "}"
                        + " WHERE {" + NotificationItemModel.PSPREFERENCE + "} = ?pspReference"
                        + " AND {" + NotificationItemModel.EVENTCODE + "} = ?eventCode"
                        + " AND {" + NotificationItemModel.SUCCESS + "} = ?success"
                        + " AND {" + NotificationItemModel.PROCESSEDAT + "} IS NOT NULL",
                queryParams
        );
        LOG.debug("Checking if notification already exists");
        int count = flexibleSearchService
                .search(selectNonProcessedNotificationsQuery)
                .getCount();

        LOG.debug(count + " items found ");

        return (count > 0);
    }
}
