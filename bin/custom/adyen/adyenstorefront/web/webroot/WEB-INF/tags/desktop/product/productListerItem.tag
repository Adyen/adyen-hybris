<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>



<spring:theme code="text.addToCart" var="addToCartText"/>
<c:url value="${product.url}" var="productUrl"/>

<c:set value="${not empty product.potentialPromotions}" var="hasPromotion"/>

<div class="productListItem${hasPromotion ? ' productListItemPromotion' : ''}">
	<ycommerce:testId code="test_searchPage_wholeProduct">
		<a href="${productUrl}" title="${product.name}" class="productMainLink">
			<div class="thumb">
					<product:productPrimaryImage product="${product}" format="product"/>
				<c:if test="${not empty product.potentialPromotions and not empty product.potentialPromotions[0].productBanner}">
					<img class="promo" src="${product.potentialPromotions[0].productBanner.url}" alt="${product.potentialPromotions[0].description}" title="${product.potentialPromotions[0].description}"/>
				</c:if>
			</div>
			
			<ycommerce:testId code="searchPage_price_label_${product.code}">
				<div  class="price"><format:price priceData="${product.price}"/></div>
			</ycommerce:testId>
			
		
				<ycommerce:testId code="searchPage_productName_link_${product.code}">
					<div class="head">${product.name}</div>
				</ycommerce:testId>
				<c:if test="${not empty product.averageRating}">
					<product:productStars rating="${product.averageRating}" />
				</c:if>
				<c:if test="${not empty product.summary}">
					<div class="details">${product.summary}</div>
				</c:if>			
				<product:productListerClassifications product="${product}"/>
				
				
	
				<ycommerce:testId code="searchPage_addToCart_button_${product.code}">
					<c:set var="buttonType">submit</c:set>
					<c:choose>
						<c:when test="${product.stock.stockLevelStatus.code eq 'outOfStock' }">
							<c:set var="buttonType">button</c:set>
							<spring:theme code="text.addToCart.outOfStock" var="addToCartText"/>
						</c:when>
						<c:when test="${product.stock.stockLevelStatus.code eq 'lowStock' }">
							<div class='lowStock'><spring:theme code="product.variants.only.left" arguments="${product.stock.stockLevel}"/></div>
						</c:when>
					</c:choose>
			
		</a>
		<div class="cart clearfix">
				<c:url value="/cart/add" var="addToCartUrl"/>

				<form:form id="addToCartForm${product.code}" action="${addToCartUrl}" method="post" class="add_to_cart_form">
					<input type="hidden" name="productCodePost" value="${product.code}"/>
					<button type="${buttonType}" class="addToCartButton <c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock' }">out-of-stock</c:if>" <c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock' }"> disabled="disabled" aria-disabled="true"</c:if>>${addToCartText}</button>				
				</form:form>
			</ycommerce:testId>
			<c:if test="${product.availableForPickup}">
				<storepickup:clickPickupInStore product="${product}" entryNumber="0" cartPage="false"/>
			</c:if>
		</div>
		</ycommerce:testId>	
</div>
