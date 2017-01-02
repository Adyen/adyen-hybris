<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ attribute name="heading" required="false"%>
<%@ attribute name="previousPage" required="false"%>
<%@ attribute name="pageTransition" required="false"%>
<%@ attribute name="leftButtonId" required="false"%>
<%@ attribute name="rightButtonId" required="false"%>

<c:if test="${not empty previousPage}">
	<a href="#${previousPage}" id="${leftButtonId}" data-role="button" data-icon="arrow-l" data-transition="${pageTransition}">
		<spring:theme code="search.nav.refine.button" />
	</a>
</c:if>
<h3>
	<spring:theme code="${heading}hi" />
</h3>
<a href="#" data-role="button" id="${rightButtonId}" data-icon="check"><spring:theme code="search.nav.done.button" /></a>
