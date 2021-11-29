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
package com.adyen.v6.cronjob;

import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.adyen.v6.model.NotificationItemModel;
import com.adyen.v6.repository.NotificationItemRepository;
import com.adyen.v6.service.AdyenNotificationService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Notification handling cronjob
 */
public class AdyenProcessNotificationCronJob extends AbstractJobPerformable<CronJobModel> {
    private static final Logger LOG = Logger.getLogger(AdyenProcessNotificationCronJob.class);

    private ModelService modelService;
    private NotificationItemRepository notificationItemRepository;
    private AdyenNotificationService adyenNotificationService;

    @Override
    public PerformResult perform(final CronJobModel cronJob) {
        LOG.debug("Start processing..");

        List<NotificationItemModel> nonProcessedNotifications = notificationItemRepository.getNonProcessedNotifications();

        for (final NotificationItemModel notificationItemModel : nonProcessedNotifications) {
            notificationItemModel.setProcessedAt(new Date());

            LOG.debug("Processing event " + notificationItemModel.getEventCode() + " for order with code " + notificationItemModel.getMerchantReference());

            NotificationItemModel processedNotification = notificationItemRepository.notificationProcessed(notificationItemModel.getPspReference(), notificationItemModel.getEventCode(), notificationItemModel.getSuccess());
            if (processedNotification != null) {
                notificationItemModel.setEventDate(processedNotification.getEventDate());
                LOG.debug("Skipping duplicate notification");
            } else {
                boolean isOldNotification = notificationItemRepository.isNewerNotificationExists(notificationItemModel.getMerchantReference(),
                                                                                                 notificationItemModel.getEventDate(),
                                                                                                 notificationItemModel.getMerchantAccountCode());
                if (isOldNotification) {
                    LOG.debug("Skipping delayed notification");
                } else {
                    adyenNotificationService.processNotification(notificationItemModel);
                    LOG.debug("Notification with PSPReference " + notificationItemModel.getPspReference() + " was processed");
                }
            }

            modelService.save(notificationItemModel);
        }

        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public NotificationItemRepository getNotificationItemRepository() {
        return notificationItemRepository;
    }

    public void setNotificationItemRepository(NotificationItemRepository notificationItemRepository) {
        this.notificationItemRepository = notificationItemRepository;
    }

    public AdyenNotificationService getAdyenNotificationService() {
        return adyenNotificationService;
    }

    public void setAdyenNotificationService(AdyenNotificationService adyenNotificationService) {
        this.adyenNotificationService = adyenNotificationService;
    }
}
