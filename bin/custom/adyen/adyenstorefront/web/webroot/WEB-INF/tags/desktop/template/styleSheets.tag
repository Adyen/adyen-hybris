<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="g" uri="http://granule.com/tags/accelerator"%>
<%@ taglib prefix="compressible" tagdir="/WEB-INF/tags/desktop/template/compressible" %>
<%@ taglib prefix="cms" tagdir="/WEB-INF/tags/desktop/template/cms" %>


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


<%-- <link rel="stylesheet" href="${commonResourcePath}/blueprint/print.css" type="text/css" media="print" /> --%>
<style type="text/css" media="print">
	@IMPORT url("${commonResourcePath}/blueprint/print.css");
</style>


<%-- our site css --%>
<!--[if IE 8]> <link type="text/css" rel="stylesheet" href="${commonResourcePath}/css/ie_8.css" media="screen, projection" /> <![endif]-->
<!--[if IE 7]> <link type="text/css" rel="stylesheet" href="${commonResourcePath}/css/ie_7.css" media="screen, projection" /> <![endif]-->

<%-- theme specific css --%>
<!--[if IE 8]> <link type="text/css" rel="stylesheet" href="${themeResourcePath}/css/ie_8.css" media="screen, projection" /> <![endif]-->
<!--[if IE 7]> <link type="text/css" rel="stylesheet" href="${themeResourcePath}/css/ie_7.css" media="screen, projection" /> <![endif]-->

<cms:previewCSS cmsPageRequestContextData="${cmsPageRequestContextData}" />
