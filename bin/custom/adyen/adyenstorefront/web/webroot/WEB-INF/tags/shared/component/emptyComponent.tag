<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${cmsPageRequestContextData.liveEdit}">
	<div class="yCmsComponentEmpty">
		Empty ${component.itemtype}: ${component.name}
	</div>
</c:if>
