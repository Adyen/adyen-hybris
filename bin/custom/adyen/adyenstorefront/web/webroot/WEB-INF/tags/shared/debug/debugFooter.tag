<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%--
~ [y] hybris Platform
~
~  Copyright (c) 2000-2011 hybris AG
~  All rights reserved.
~
~  This software is the confidential and proprietary information of hybris
~  ("Confidential Information"). You shall not disclose such Confidential
~  Information and shall use it only in accordance with the terms of the
~  license agreement you entered into with hybris.
--%>

<%-- Debug footer. Not for production. Outputs in an HTML comment --%>

<c:if test="${showStorefrontDebugInfo}">
<!-- TODO: Remove From Production

DEBUG INFO

cmsPageName=${cmsPage.name}
cmsSiteUid=${cmsSite.uid}
secure=${request.secure}
contextPath=${request.contextPath}
siteRootPath=<c:url value="/"/>

siteResourcePath=${siteResourcePath}
themeResourcePath=${themeResourcePath}
commonResourcePath=${commonResourcePath}
requestURI=${request.requestURI}


Jalo Session details:

${storefrontDebugJaloSessionAttributes}


UiExperienceLevel:

uiExperienceLevel=${uiExperienceLevel}
uiExperienceOverride=${uiExperienceOverride}
detectedUiExperienceCode=${detectedUiExperienceCode}
overrideUiExperienceCode=${overrideUiExperienceCode}


Detected Browser:

detectedDeviceId=${detectedDevice.id}
detectedDeviceUserAgent=${detectedDevice.userAgent}
detectedDeviceCapabilities=${detectedDevice.capabilities}

-->
</c:if>
