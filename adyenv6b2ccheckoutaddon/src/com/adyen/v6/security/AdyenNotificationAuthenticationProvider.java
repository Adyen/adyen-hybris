package com.adyen.v6.security;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Base64;

import static com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants.NOTIFICATION_PASSWORD;
import static com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants.NOTIFICATION_USERNAME;

/**
 * Authenticates a request that is using Basic Authentication
 */
public class AdyenNotificationAuthenticationProvider {
    private ConfigurationService configurationService;

    private static final Logger LOG = Logger.getLogger(AdyenNotificationAuthenticationProvider.class);

    public boolean authenticateBasic(final HttpServletRequest request) {
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                    Charset.forName("UTF-8"));
            final String[] values = credentials.split(":", 2);

            return tryToAuthenticate(values[0], values[1]);
        }
        return false;
    }

    private boolean tryToAuthenticate(String name, String password) {
        final Configuration configuration = getConfigurationService().getConfiguration();
        String notificationUsername = configuration.getString(NOTIFICATION_USERNAME);
        String notificationPassword = configuration.getString(NOTIFICATION_PASSWORD);

        if (notificationUsername.isEmpty() || notificationPassword.isEmpty()) {
            return false;
        }

        if (notificationUsername.equals(name) && notificationPassword.equals(password)) {
            return true;
        }

        return false;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
