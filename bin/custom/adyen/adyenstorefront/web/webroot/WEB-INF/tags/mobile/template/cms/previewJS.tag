<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cmsPageRequestContextData" required="true" type="de.hybris.platform.acceleratorcms.data.CmsPageRequestContextData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${cmsPageRequestContextData.liveEdit}">


    <c:if test="${empty addOnLiveEditJavaScriptPaths}">
        <script type="text/javascript" src="${commonResourcePath}/js/hybris.cms.live.edit.js"></script>
        <c:if test="${cmsPageRequestContextData.preview}">
            <script type="text/javascript">

                var currentUserId = '${cmsPageRequestContextData.user.uid}';
                var currentJaloSessionId = '${cmsPageRequestContextData.sessionId}';
                var currentPagePk = '${cmsPageRequestContextData.page.pk}';

                $(document).ready(function ()
                {
                    parent.postMessage({eventName:'notifyIframeAboutUrlChange', data: [window.location.href, currentPagePk, currentUserId, currentJaloSessionId]},'*');
                });
            </script>
        </c:if>
    </c:if>
    <c:if test="${not empty addOnLiveEditJavaScriptPaths}">
        <c:forEach items="${addOnLiveEditJavaScriptPaths}" var="addOnJavaScript">
            <script type="text/javascript" src="${addOnJavaScript}"></script>
        </c:forEach>
    </c:if>

</c:if>

