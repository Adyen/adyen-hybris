<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product" %>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/mobile/store" %>
<%@ taglib prefix="input" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/mobile/storepickup" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="cartPage" required="true" type="java.lang.Boolean"%>
<%@ attribute name="entryNumber" required="true" type="java.lang.Long"%>
<%@ attribute name="deliveryPointOfService" required="false" type="java.lang.String"%>
<%@ attribute name="quantity" required="false" type="java.lang.Integer"%>
<%@ attribute name="searchResultsPage" required="false" type="java.lang.Boolean"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<c:url var="pickupInStoreUrl" value="/store-pickup/${product.code}/pointOfServices"/>

<div class="collect_from_store">
	<c:choose>
		<c:when test="${cartPage}">
			<a href="javascript:void(0)" class="click_pickupInStore_Button" id="product_${product.code}${entryNumber}">
				<c:choose>
					<c:when test="${not empty deliveryPointOfService}">
						<spring:theme code="basket.page.shipping.change.store" text="Change Store"/>
					</c:when>
					<c:otherwise>
						<spring:theme code="basket.page.shipping.find.store" text="Find a Store"/>
					</c:otherwise>
				</c:choose>
			</a>
		</c:when>
		<c:when test="${searchResultsPage}">
			<button class="neutral large click_pickupInStore_Button" id="product_${product.code}${entryNumber}" type="submit">
				<!-- Style the image for Product lister page-->
				<spring:theme code="pickup.in.store"/>
			</button>
		</c:when>
		<c:otherwise>
			<button class="neutral large click_pickupInStore_Button" id="product_${product.code}${entryNumber}" type="submit">
				<spring:theme code="pickup.in.store"/>
			</button>
		</c:otherwise>
	</c:choose>
</div>

<div id="popup_store_pickup_form_product_${product.code}${entryNumber}" class="pickup_store_search" style="display:none">
	<div class="item_container_holder clearfix">
		<div class="title_holder">
			<h2><spring:theme code="pickup.product.availability" /></h2>
		</div>
	</div>

	<div class="prod_grid span-4">
		<span class="thumb">
			<product:productPrimaryImage product="${product}" format="thumbnail"/>
		</span>
		<span class="details">
			<strong class="strong prod_grid-name">${product.name}</strong>
		</span>
		<div class="cart">
			<p>${product.price.formattedValue}</p>
			<c:if test="${not empty product.baseOptions[0].selected.variantOptionQualifiers}">
				<c:forEach var="variant" items="${product.baseOptions[0].selected.variantOptionQualifiers}">
					<c:if test="${not empty variant.value}">
						<p>
							<spring:theme code="basket.pickup.product.variant" arguments="${variant.name},${variant.value}" />
						</p>
					</c:if>
				</c:forEach>
			</c:if>
		</div>
		<div class="quantity pickup_store_search-quantity">
			<label for="pickupQty"><spring:theme code="basket.page.quantity" /></label>
			<input type="text" size="1" id="pickupQty" name="qty" class="qty"
				<c:choose>
					<c:when test="${cartPage}">
						value="${quantity}"
					</c:when>
					<c:otherwise>
						value="1"
					</c:otherwise>
				</c:choose>
			/>
		</div>
	</div>
	
	<div class="span-17 last">
		<div class="pickup_store_search-form">
			<form:form name="pickupInStoreForm" action="${pickupInStoreUrl}" method="post" id="pickup_in_store_search_form_product_${product.code}${entryNumber}" class="form_field-input">
				<spring:theme code="pickup.search.message" />
				<input type="text" name="locationQuery" id="locationForSearch" />
				<input type="hidden" name="cartPage" id="atCartPage" value="${cartPage}" />
				<input type="hidden" name="entryNumber" value="${entryNumber}" class="entryNumber" />
				<button type="submit" class="form" id="pickupstore_search_button"><spring:theme code="pickup.search.button" /></button>
			</form:form>
		</div>
		<div id="pickup_store_results"></div>
	</div>
</div>
