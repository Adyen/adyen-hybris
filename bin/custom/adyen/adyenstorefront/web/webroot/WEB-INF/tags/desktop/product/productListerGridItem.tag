<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<spring:theme code="text.addToCart" var="addToCartText"/>
<c:url value="${product.url}" var="productUrl"/>

<c:set value="${not empty product.potentialPromotions}" var="hasPromotion"/>

<ycommerce:testId code="product_wholeProduct">
	<div class="productGridItem ${hasPromotion ? 'productGridItemPromotion' : ''}">
		<a href="${productUrl}" title="${product.name}" class="productMainLink">
			<div class="thumb">
					<product:productPrimaryImage product="${product}" format="product"/>
				<c:if test="${not empty product.potentialPromotions and not empty product.potentialPromotions[0].productBanner}">
					<img class="promo" src="${product.potentialPromotions[0].productBanner.url}" alt="${product.potentialPromotions[0].description}" title="${product.potentialPromotions[0].description}"/>
				</c:if>
			</div>
		
			<div class="priceContainer">
				<c:set var="buttonType">submit</c:set>
	      		<ycommerce:testId code="product_productPrice">
	          		<span class="price"><format:price priceData="${product.price}"/></span>
	      		</ycommerce:testId>
		     </div>
			 
			 
			<div class="details">
				<ycommerce:testId code="product_productName">${product.name}</ycommerce:testId>
			</div>
			
			
			
		
  		<c:choose>
        	<c:when test="${product.stock.stockLevelStatus.code eq 'outOfStock' }">
            	<c:set var="buttonType">button</c:set>
              	<spring:theme code="text.addToCart.outOfStock" var="addToCartText"/>
              	<span class='listProductLowStock listProductOutOfStock mlist-stock'>${addToCartText}</span>
          </c:when>
          <c:when test="${product.stock.stockLevelStatus.code eq 'lowStock' }">
              	<span class='listProductLowStock mlist-stock'><spring:theme code="product.variants.only.left" arguments="${product.stock.stockLevel}"/></span>
          </c:when>
      </c:choose>
		
	</a>
		
		
	<div class="cart clearfix">
	
		<c:url value="/cart/add" var="addToCartUrl"/>
		 <form:form id="addToCartForm${product.code}" action="${addToCartUrl}" method="post" class="add_to_cart_form clear_fix">
			<input type="hidden" name="productCodePost" value="${product.code}"/>
			<c:if test="${not empty product.averageRating}">
				<product:productStars rating="${product.averageRating}" />
			</c:if>
			<ycommerce:testId code="product_addProduct_button">
				<button type="${buttonType}" class="addToCartButton <c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock' }">out-of-stock</c:if>" <c:if test="${product.stock.stockLevelStatus.code eq 'outOfStock' }"> disabled="disabled" aria-disabled="true"</c:if>>${addToCartText}</button>
			</ycommerce:testId>
		</form:form>
	
	
         


		<c:if test="${product.availableForPickup}">
			<storepickup:clickPickupInStore product="${product}" entryNumber="0" cartPage="false" searchResultsPage="true"/>
		</c:if>

	</div>
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	</div>
</ycommerce:testId>
