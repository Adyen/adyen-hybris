<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup" %>
<c:if test="${empty showAddToCart ? true : showAddToCart and product.availableForPickup}">
	<c:set var="actionUrl" value="${fn:replace(url,
	                                '{productCode}', product.code)}" scope="request"/>
	<storepickup:clickPickupInStore product="${product}" cartPage="false"/>
	<storepickup:pickupStorePopup/>
	<c:remove var="actionUrl"/>
</c:if>
