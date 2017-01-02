<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="actionUrl" value="${fn:replace(url,
	                                '{productCode}', product.code)}" scope="request"/>

<c:if test="${product.availableForPickup}">
	<storepickup:clickPickupInStore product="${product}" entryNumber="0" cartPage="false" searchResultsPage="true"/>
</c:if>
