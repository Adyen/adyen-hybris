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

import java.util.Date;
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

    public boolean isNewerNotificationExists(String merchantReference, Date eventDate, String merchantAccountCode) {
        String query = "SELECT {pk} FROM {" + NotificationItemModel._TYPECODE + "}"
                + " WHERE {" + NotificationItemModel.PROCESSEDAT + "} IS NOT NULL";
        final Map<String, Object> queryParams = new HashMap<>();

        if (merchantReference != null) {
            queryParams.put("merchantReference", merchantReference);
            query += " AND {" + NotificationItemModel.MERCHANTREFERENCE + "} = ?merchantReference";
        } else {
            query += " AND {" + NotificationItemModel.MERCHANTREFERENCE + "} IS NULL";
        }
        queryParams.put("eventDate", eventDate);
        queryParams.put("merchantAccountCode", merchantAccountCode);
        query += " AND {" + NotificationItemModel.EVENTDATE + "} > ?eventDate"
                + " AND {" + NotificationItemModel.MERCHANTACCOUNTCODE + "} = ?merchantAccountCode"
                + " ORDER BY {eventDate} desc";

        final FlexibleSearchQuery laterNotificationQuery = new FlexibleSearchQuery(query, queryParams);
        LOG.debug("Checking if a newer notification already exists");
        List<Object> newerNotificationList = flexibleSearchService
                .search(laterNotificationQuery)
                .getResult();

        return newerNotificationList != null && newerNotificationList.size() > 0;
    }

    /**
     * Checks if the notification is already processed
     *
     * @param pspReference Notification psp reference
     * @param eventCode Notification eventCode
     * @param success Notification success
     * @return true|false
     */
    public NotificationItemModel notificationProcessed(String pspReference, String eventCode, boolean success) {
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("pspReference", pspReference);
        queryParams.put("eventCode", eventCode);
        queryParams.put("success", success);

        final FlexibleSearchQuery selectProcessedNotificationsQuery = new FlexibleSearchQuery(
                "SELECT {pk} FROM {" + NotificationItemModel._TYPECODE + "}"
                        + " WHERE {" + NotificationItemModel.PSPREFERENCE + "} = ?pspReference"
                        + " AND {" + NotificationItemModel.EVENTCODE + "} = ?eventCode"
                        + " AND {" + NotificationItemModel.SUCCESS + "} = ?success"
                        + " AND {" + NotificationItemModel.PROCESSEDAT + "} IS NOT NULL"
                        + " ORDER BY {" + NotificationItemModel.PROCESSEDAT + "} ASC",
                queryParams
        );

        LOG.debug("Checking if notification already exists");
        NotificationItemModel processed = flexibleSearchService
                .search(selectProcessedNotificationsQuery)
                .getResult()
                .stream()
                .map(element -> (NotificationItemModel) element)
                .findFirst()
                .orElse(null);

        if(processed != null) {
            LOG.debug(processed.getUuid() + " - processed item found");
        }

        return processed;
    }
}
