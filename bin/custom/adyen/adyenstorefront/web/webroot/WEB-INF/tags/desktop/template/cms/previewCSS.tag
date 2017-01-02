<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cmsPageRequestContextData" required="true" type="de.hybris.platform.acceleratorcms.data.CmsPageRequestContextData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${cmsPageRequestContextData.liveEdit}">
	<link rel="stylesheet" type="text/css" href="${commonResourcePath}/css/hybris.cms.live.edit.css" />
</c:if>
