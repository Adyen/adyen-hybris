<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>

<c:set var="parentComponent" value="${component}" scope="request"/>
<ul>
	<c:forEach items="${actions}" var="action" varStatus="idx">
		<c:if test="${action.visible}">
			<li id="${parentComponent.uid}-${action.uid}" data-index="${idx.index + 1}">
				<cms:component component="${action}" parentComponent="${parentComponent}" evaluateRestriction="true"/>
			</li>
		</c:if>
	</c:forEach>
</ul>
<c:remove var="parentComponent" scope="request"/>
