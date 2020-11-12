/*
 *                       ######
 *                       ######
 * ############    ####( ######  #####. ######  ############   ############
 * #############  #####( ######  #####. ######  #############  #############
 *        ######  #####( ######  #####. ######  #####  ######  #####  ######
 * ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 * ###### ######  #####( ######  #####. ######  #####          #####  ######
 * #############  #############  #############  #############  #####  ######
 *  ############   ############  #############   ############  #####  ######
 *                                      ######
 *                               #############
 *                               ############
 *
 * Adyen Hybris Extension
 *
 * Copyright (c) 2020 Adyen B.V.
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more info.
 */

package com.adyen.v6.utils;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.log4j.Logger.getLogger;

public class SameSiteCookieUtils {

    private static final Logger LOG = getLogger(SameSiteCookieUtils.class);

    private static final Pattern CHROME_VERSION = Pattern.compile("Chrom[^ \\/]+\\/(\\d+)[\\.\\d]*");

    private static final Pattern UC_BROWSER_VERSION = Pattern.compile("UCBrowser\\/(\\d+)\\.(\\d+)\\.(\\d+)[\\.\\d]* ");

    private static final Pattern IOS_VERSION = Pattern.compile("\\(iP.+; CPU .*OS (\\d+)[_\\d]*.*\\) AppleWebKit\\/");
    private static final Pattern MACOS_VERSION = Pattern.compile("\\(Macintosh;.*Mac OS X (\\d+)_(\\d+)[_\\d]*.*\\) AppleWebKit\\/");
    private static final Pattern MAC_EMBEDDED_VERSION = Pattern.compile("^Mozilla\\/[\\.\\d]+ \\(Macintosh;.*Mac OS X [_\\d]+\\) AppleWebKit\\/[\\.\\d]+ \\(KHTML, like Gecko\\)$");

    private SameSiteCookieUtils() {
        // ! Util class must not be initialized
    }

    public static boolean shouldSendSameSiteNone(String useragent) {
        return !isSameSiteNoneIncompatible(useragent);
    }

    private static boolean isSameSiteNoneIncompatible(String useragent) {
        return hasWebKitSameSiteBug(useragent) || dropsUnrecognizedSameSiteCookies(useragent);
    }

    private static boolean hasWebKitSameSiteBug(String useragent) {
        return isIosVersion(12, useragent) || (isMacOsVersion(10, 14, useragent) && (isSafari(useragent) || isMacEmbeddedBrowser(useragent)));
    }

    private static boolean dropsUnrecognizedSameSiteCookies(String useragent) {
        if (isUcBrowser(useragent)) {
            return !isUcBrowserVersionAtLeast(12, 13, 2, useragent);
        }
        return isChromiumBased(useragent) && isChromiumVersionAtLeast(51, useragent) && !isChromiumVersionAtLeast(67, useragent);
    }

    private static boolean isIosVersion(int major, String useragent) {
        Matcher matcher = IOS_VERSION.matcher(useragent);
        if (matcher.find()) {
            String userAgentVersion = matcher.group(1);
            return userAgentVersion.equals(String.valueOf(major));
        }
        return false;
    }


    private static boolean isMacOsVersion(int major, int minor, String useragent) {
        Matcher matcher = MACOS_VERSION.matcher(useragent);
        if (matcher.find()) {
            try {
                String macOsMajorVersion = matcher.group(1);
                String macOsMinorVersion = matcher.group(2);
                return Integer.parseInt(macOsMajorVersion) == major && Integer.parseInt(macOsMinorVersion) == minor;
            } catch (NumberFormatException e) {
                LOG.warn(e.toString());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Exception:", e);
                }
            }
        }
        return false;
    }

    private static boolean isSafari(String useragent) {
        return useragent.contains("Safari");
    }

    private static boolean isMacEmbeddedBrowser(String useragent) {
        Matcher matcher = MAC_EMBEDDED_VERSION.matcher(useragent);
        return matcher.find();
    }

    private static boolean isChromiumBased(String useragent) {
        return useragent.contains("Chrome") || useragent.contains("Chromium");
    }

    private static boolean isChromiumVersionAtLeast(int major, String useragent) {
        Matcher matcher = CHROME_VERSION.matcher(useragent);
        if (matcher.find()) {
            try {
                String chromeVersion = matcher.group(1);
                return Integer.parseInt(chromeVersion) >= major;
            } catch (NumberFormatException e) {
                LOG.warn(e.toString());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Exception:", e);
                }
            }
        }
        return false;
    }

    private static boolean isUcBrowser(String useragent) {
        return useragent.contains("UCBrowser");
    }


    private static boolean isUcBrowserVersionAtLeast(int major, int minor, int build, String useragent) {
        Matcher matcher = UC_BROWSER_VERSION.matcher(useragent);
        if (matcher.find()) {
            try {
                int ucMajorVersion = Integer.parseInt(matcher.group(1));
                if (ucMajorVersion != major) {
                    return ucMajorVersion > major;
                }
                int ucMinorVersion = Integer.parseInt(matcher.group(2));
                if (ucMinorVersion != minor) {
                    return ucMinorVersion > minor;
                }
                int ucBuildVersion = Integer.parseInt(matcher.group(3));
                return ucBuildVersion >= build;
            } catch (NumberFormatException e) {
                LOG.warn(e.toString());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Exception:", e);
                }
            }
        }
        return false;
    }

}