<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<c:if test="${empty showAddToCart ? true : showAddToCart and product.availableForPickup}">
	<c:set var="actionUrl" value="${fn:replace(url,
	                                '{productCode}', product.code)}" scope="request"/>
		<div id='pickUpInStore'>
			<%--Buy Reserve Online and Collect in Store --%>
				<a href="#" class="pickUpInStoreButton" data-productCode="${product.code}" data-productavailable="${product.availableForPickup}" data-rel="dialog" data-transition="pop" data-role="button" data-theme="c">
					<spring:theme code="pickup.in.store"/>
				</a>
		</div>
	<c:remove var="actionUrl"/>
</c:if>
