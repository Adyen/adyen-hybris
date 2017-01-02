<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:formatNumber maxFractionDigits="0" value="${(product.averageRating > 0 ? product.averageRating: 0) * 17}" var="starWidth"/>
	<span class="stars large" style="display: inherit;" tabindex="5">
		<span style="width: ${starWidth}px;"></span>
	</span>
<c:if test="${not empty product.numberOfReviews}">
    <div class="numberOfReviews">( ${product.numberOfReviews} )</div>
</c:if>
