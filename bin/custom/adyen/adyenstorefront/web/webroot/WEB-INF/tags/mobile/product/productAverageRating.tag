<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ attribute name="format" required="true" type="java.lang.String"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div>
	<c:if test="${not empty product.reviews}">
		<span class="stars large"
			style="display: inherit;"
			tabindex='5'
			alt='<spring:theme code="product.average.review.rating" arguments="${(product.averageRating > 0 ? product.averageRating: 0)}"/>'>
			<span style="width: <fmt:formatNumber maxFractionDigits="0" value="${product.averageRating * 17}" />px;"></span>
		</span>
		<p><fmt:formatNumber maxFractionDigits="1" value="${product.averageRating}"/>/5</p>
	</c:if>
</div>
