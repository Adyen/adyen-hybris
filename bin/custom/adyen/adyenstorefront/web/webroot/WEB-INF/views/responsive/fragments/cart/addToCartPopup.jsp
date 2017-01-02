<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

{"cartData": {
"total": "${cartData.totalPrice.value}",
"products": [
<c:forEach items="${cartData.entries}" var="cartEntry" varStatus="status">
	{
		"sku":		"${cartEntry.product.code}",
		"name": 	"<c:out value='${cartEntry.product.name}' />",
		"qty": 		"${cartEntry.quantity}",
		"price": 	"${cartEntry.basePrice.value}",
		"categories": [
		<c:forEach items="${cartEntry.product.categories}" var="category" varStatus="categoryStatus">
			"<c:out value='${category.name}' />"<c:if test="${not categoryStatus.last}">,</c:if>
		</c:forEach>
		]
	}<c:if test="${not status.last}">,</c:if>
</c:forEach>
]
},
"addToCartLayer":"<spring:escapeBody javaScriptEscape="true">
	<spring:theme code="text.addToCart" var="addToCartText"/>
	<c:url value="/cart" var="cartUrl"/>
	<c:url value="/cart/checkout" var="checkoutUrl"/>
	<ycommerce:testId code="addToCartPopup">
		<div id="addToCartLayer" class="add-to-cart">

			<div class="add-to-cart-item">

				<div class="thumb">
					<a href="${entryProductUrl}">
						<product:productPrimaryImage product="${entry.product}" format="cartIcon"/>
					</a>
				</div>
				<div class="details">
					<div class="cart_popup_error_msg"><spring:theme code="${errorMsg}" /></div>
					<a class="name" href="${entryProductUrl}">${entry.product.name}</a>
					<div class="qty"><span><spring:theme code="popup.cart.quantity.added"/></span>&nbsp;${quantity}</div>
					<c:forEach items="${product.baseOptions}" var="baseOptions">
						<c:forEach items="${baseOptions.selected.variantOptionQualifiers}" var="baseOptionQualifier">
							<c:if test="${baseOptionQualifier.qualifier eq 'style' and not empty baseOptionQualifier.image.url}">
								<div class="itemColor">
									<span class="label"><spring:theme code="product.variants.colour"/></span>
									<img src="${baseOptionQualifier.image.url}"  alt="${baseOptionQualifier.value}" title="${baseOptionQualifier.value}"/>
								</div>
							</c:if>
							<c:if test="${baseOptionQualifier.qualifier eq 'size'}">
								<div class="itemSize">
									<span class="label"><spring:theme code="product.variants.size"/></span>
										${baseOptionQualifier.value}
								</div>
							</c:if>
						</c:forEach>
					</c:forEach>
					<c:if test="${not empty entry.deliveryPointOfService.name}">
						<div class="itemPickup"><span class="itemPickupLabel"><spring:theme code="popup.cart.pickup"/></span>&nbsp;${entry.deliveryPointOfService.name}</div>
					</c:if>
					<div class="price"><format:price priceData="${entry.basePrice}"/></div>
				</div>
			</div>

			<ycommerce:testId code="checkoutLinkInPopup">
				<a href="${cartUrl}" class="btn btn-primary btn-block add-to-cart-button">
					<spring:theme code="checkout.checkout" />
				</a>
			</ycommerce:testId>


			<a href="" class="btn btn-default btn-block js-mini-cart-close-button">
				<spring:theme text="Continue Shopping" code="cart.page.continue"/>
			</a>

		</div>
	</ycommerce:testId>
</spring:escapeBody>"
}



