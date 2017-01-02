<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="g" uri="http://granule.com/tags/accelerator" %>
<%@ taglib prefix="compressible" tagdir="/WEB-INF/tags/responsive/template/compressible" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="cms" tagdir="/WEB-INF/tags/responsive/template/cms" %>

<c:url value="/" var="siteRootUrl"/>

<c:choose>
	<c:when test="${granuleEnabled}">
		<g:compress urlpattern="${encodingAttributes}">
			<compressible:js/>
		</g:compress>
	</c:when>
	<c:otherwise>
		<compressible:js/>
	</c:otherwise>
</c:choose>

<cms:previewJS cmsPageRequestContextData="${cmsPageRequestContextData}" />
