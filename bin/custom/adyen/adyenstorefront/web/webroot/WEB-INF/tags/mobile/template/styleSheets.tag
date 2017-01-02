<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="g" uri="http://granule.com/tags/accelerator"%>
<%@ taglib prefix="compressible" tagdir="/WEB-INF/tags/mobile/template/compressible" %>
<%@ taglib prefix="cms" tagdir="/WEB-INF/tags/mobile/template/cms" %>

<c:choose>
	<c:when test="${granuleEnabled}">
		<g:compress urlpattern="${encodingAttributes}">
			<compressible:css/>
		</g:compress>
	</c:when>
	<c:otherwise>
		<compressible:css/>
	</c:otherwise>
</c:choose>

<cms:previewCSS cmsPageRequestContextData="${cmsPageRequestContextData}" />
