<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cmsPageRequestContextData" required="true" type="de.hybris.platform.acceleratorcms.data.CmsPageRequestContextData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${cmsPageRequestContextData.liveEdit}">
	<script type="text/javascript" src="${commonResourcePath}/js/hybris.cms.live.edit.js"></script>
</c:if>

<c:if test="${cmsPageRequestContextData.preview}">
	<script type="text/javascript">

		var currentUserId = '${cmsPageRequestContextData.user.uid}';
		var currentJaloSessionId = '${cmsPageRequestContextData.sessionId}';
		var currentPagePk = '${cmsPageRequestContextData.page.pk}';

		$(document).ready(function ()
		{
			if (parent.notifyIframeAboutUrlChanage)
			{
				parent.notifyIframeAboutUrlChanage(window.location.href, currentPagePk, currentUserId, currentJaloSessionId);
			}
		});
	</script>
</c:if>
