<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="priceData" required="true" type="de.hybris.platform.commercefacades.product.data.PriceData" %>
<%@ attribute name="displayFreeForZero" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%--
 Tag to render a currency formatted price.
 Includes the currency symbol for the specific currency.
--%>
<c:choose>
	<c:when test="${priceData.value > 0}">
		${priceData.formattedValue}
	</c:when>
	<c:otherwise>
		<c:if test="${displayFreeForZero}">
			<spring:theme code="text.free" text="FREE"/>
		</c:if>
		<c:if test="${not displayFreeForZero}">
			${priceData.formattedValue}
		</c:if>
	</c:otherwise>
</c:choose>
