<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="allowAddToCart" required="true" type="java.lang.Boolean" %>
<%@ attribute name="isMain" required="true" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<div class="qty">
	<c:if test="${product.purchasable}">
		<label for="qtyInput"><spring:theme code="basket.page.quantity"/></label>
		<input type="text" maxlength="3" size="1" id="qtyInput" name="qtyInput" class="qty" value="1">
	</c:if>

	<c:if test="${product.stock.stockLevel gt 0}">
		<c:set var="productStockLevel">${product.stock.stockLevel}&nbsp;<spring:theme code="product.variants.in.stock"/></c:set>
	</c:if>
	<c:if test="${product.stock.stockLevelStatus.code eq 'lowStock'}">
		<c:set var="productStockLevel"><spring:theme code="product.variants.only.left" arguments="${product.stock.stockLevel}"/></c:set>
	</c:if>
	<c:if test="${product.stock.stockLevelStatus.code eq 'inStock' and empty product.stock.stockLevel}">
		<c:set var="productStockLevel"><spring:theme code="product.variants.available"/></c:set>
	</c:if>

	<ycommerce:testId code="productDetails_productInStock_label">
		<p class="stock_message">${productStockLevel}</p>
	</ycommerce:testId>
</div>

<div class="productAddToCartPanel clearfix">
	<c:url value="/cart/add" var="addToCartUrl"/>
	<form:form method="post" id="addToCartForm" class="add_to_cart_form" action="${addToCartUrl}" >
	<c:if test="${product.purchasable}">
		<input type="hidden" maxlength="3" size="1" id="qty" name="qty" class="qty" value="1">
	</c:if>
	<input type="hidden" name="productCodePost" value="${product.code}"/>

	<c:if test="${allowAddToCart}">
		<c:set var="buttonType">button</c:set>

		<c:if test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock' }">
			<c:set var="buttonType">submit</c:set>
		</c:if>

		<c:choose>
			<c:when test="${fn:contains(buttonType, 'button')}">
				<button type="${buttonType}" class="addToCartButton outOfStock" disabled="disabled">
					<spring:theme code="product.variants.out.of.stock"/>
				</button>
			</c:when>

			<c:otherwise>
				<button id="addToCartButton" type="${buttonType}" class="addToCartButton" disabled="disabled">
					<spring:theme code="basket.add.to.basket"/>
				</button>
			</c:otherwise>
		</c:choose>
	</c:if>
	</form:form>
	<c:if test="${allowAddToCart and product.availableForPickup}">
		<storepickup:clickPickupInStore product="${product}" cartPage="false"/>
		<c:if test="${isMain}">
			<storepickup:pickupStorePopup/>
		</c:if>
	</c:if>
</div>
