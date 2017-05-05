package com.adyen.v6.controllers.pages;

import com.adyen.model.notification.NotificationRequest;
import com.adyen.model.notification.NotificationRequestItem;
import com.adyen.notification.NotificationHandler;
import com.adyen.v6.constants.AdyenControllerConstants;
import com.adyen.v6.security.AdyenNotificationAuthenticationProvider;
import com.adyen.v6.service.AdyenNotificationService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequestMapping(value = AdyenControllerConstants.NOTIFICATION_PREFIX)
public class AdyenNotificationController {
    private static final Logger LOG = Logger.getLogger(AdyenNotificationController.class);

    @Resource(name = "adyenNotificationAuthenticationProvider")
    private AdyenNotificationAuthenticationProvider adyenNotificationAuthenticationProvider;

    @Resource(name = "adyenNotificationService")
    private AdyenNotificationService adyenNotificationService;

    private static final String RESPONSE_ACCEPTED = "[accepted]";
    private static final String RESPONSE_NOT_ACCEPTED = "[not-accepted]";

    @RequestMapping(value = "/json", method = RequestMethod.POST)
    @ResponseBody
    public String onReceive(final HttpServletRequest request) {
        String requestString = null;
        try {
            //Parse response body by request input stream so that Spring doesn't try to deserialize
            requestString = IOUtils.toString(request.getInputStream());
        } catch (IOException e) {
            LOG.error(e);
            return RESPONSE_NOT_ACCEPTED;
        }

        LOG.debug("Received Adyen notification:" + requestString);
        if (!adyenNotificationAuthenticationProvider.authenticateBasic(request)) {
            throw new AccessDeniedException("Wrong credentials");
        }

        NotificationHandler notificationHandler = new NotificationHandler();
        NotificationRequest notificationRequest = notificationHandler.handleNotificationJson(requestString);
        LOG.debug(notificationRequest);

        //Save the notification items to the database
        for (NotificationRequestItem notificationRequestItem : notificationRequest.getNotificationItems()) {
            adyenNotificationService.saveFromNotificationRequest(notificationRequestItem);
        }

        return RESPONSE_ACCEPTED;
    }
}
