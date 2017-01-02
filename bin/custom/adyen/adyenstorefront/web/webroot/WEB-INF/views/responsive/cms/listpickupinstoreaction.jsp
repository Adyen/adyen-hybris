<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/responsive/storepickup" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="actionUrl" value="${fn:replace(url,
	                                '{productCode}', product.code)}" scope="request"/>
<c:if test="${product.availableForPickup}">
	<storepickup:clickPickupInStore product="${product}" entryNumber="0" cartPage="false" searchResultsPage="true"/>
</c:if>
