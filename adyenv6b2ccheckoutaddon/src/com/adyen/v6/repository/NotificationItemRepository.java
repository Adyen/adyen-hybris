package com.adyen.v6.repository;

import com.adyen.v6.model.NotificationItemModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Repository class for NotificationItems
 */
public class NotificationItemRepository extends AbstractRepository {
    private static final Logger LOG = Logger.getLogger(NotificationItemRepository.class);

    public List<NotificationItemModel> getNonProcessedNotifications() {
        //Select the non-processed notifications
        final FlexibleSearchQuery selectNonProcessedNotificationsQuery = new FlexibleSearchQuery(
                "SELECT {pk} FROM {" + NotificationItemModel._TYPECODE + "}"
                        + " WHERE {" + NotificationItemModel.PROCESSED + "} = false ORDER BY {pk} ASC LIMIT 1000"
        );
        LOG.info("Querying notification items");
        final List nonProcessedNotifications = flexibleSearchService
                .search(selectNonProcessedNotificationsQuery)
                .getResult();

        LOG.info(nonProcessedNotifications.size() + " items found ");

        return nonProcessedNotifications;
    }
}
