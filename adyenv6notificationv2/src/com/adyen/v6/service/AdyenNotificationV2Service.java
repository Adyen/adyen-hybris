package com.adyen.v6.service;

import com.adyen.model.notification.NotificationRequest;

public interface AdyenNotificationV2Service
{
	void onRequest(NotificationRequest notificationRequest);
}
