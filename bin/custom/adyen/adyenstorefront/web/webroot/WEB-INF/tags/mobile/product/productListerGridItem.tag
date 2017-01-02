<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="mproduct" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<spring:theme code="text.addToCart" var="addToCartText"/>
<c:url value="${product.url}" var="productUrl"/>
<ycommerce:testId code="product_wholeProduct">
	<a href="${productUrl}">
		<div class="productTitle">${product.name}</div>
		<mproduct:productPrimaryImage product="${product}" format="thumbnail" zoomable='false'/>
		<span id="productPrice" class="mlist-price"><format:price priceData="${product.price}"/></span>
    <c:choose>
        <c:when test="${product.stock.stockLevelStatus.code eq 'outOfStock' }">
            <span class='listProductOutOfStock mlist-stock'><spring:theme code="product.variants.out.of.stock"/></span>
        </c:when>
        <c:when test="${product.stock.stockLevelStatus.code eq 'lowStock' }">
            <span class='listProductLowStock mlist-stock'><spring:theme code="product.variants.only.left" arguments="${product.stock.stockLevel}"/></span>
        </c:when>
    </c:choose>
		<span class="mlist-rating"><mproduct:productAverageRatingListerItem product="${product}"/></span>
	</a>
</ycommerce:testId>
