<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:url var="actionUrl" value="${fn:replace(url, '{orderCode}', orderCode)}" scope="page"/>
<ycommerce:testId code="orderHistory_Actions_links">
	<p><a href="${actionUrl}">
		<spring:theme code="text.view" text="View"/>
	</a></p>
</ycommerce:testId>
