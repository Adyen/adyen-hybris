<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>

<%@ attribute name="productData" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ attribute name="cartPage" required="false" type="java.lang.Boolean"%>
<%@ attribute name="entryNumber" required="false" type="java.lang.Long"%>
<%@ attribute name="qty" required="false" type="java.lang.Long"%>

<c:url value="${productData.url}" var="productDataUrl" />

<div class="item_container_holder_full pickup-productinfo clearfix">
	<div class="item_container ui-block-a" >
		<div class="ui-grid-a">
			<div class="ui-block-a">
				<a href="${productDataUrl}" data-transition="slide">
					<product:productCartImage product="${productData}" format="thumbnail" />
				</a>
			</div>
			<div class="ui-block-b">
				<div id="priceAndQuantity">
					<c:forEach items="${productData.baseOptions}" var="option">
						<c:if test="${option.selected.url eq productData.url}">
							<c:forEach items="${option.selected.variantOptionQualifiers}" var="variant">
								<c:if test="${not empty variant.name and not empty variant.value}">
									<spring:theme code="basket.pickup.product.variant" arguments="${variant.name},${variant.value}" /><br/>
								</c:if>
							</c:forEach>
						</c:if>
					</c:forEach>
					<div class="productprice">
						<ycommerce:testId code="product_price_value">
							<product:productPricePanel product="${productData}"/>
						</ycommerce:testId>
					</div>
				</div>
				<div class="prod_find_pickupinstore_quantity">
				<label for="qty" class="skip"><spring:theme code="basket.page.quantity"/></label>
					<select id="qty" class='qty' name="qty" data-theme="d">
						<option value="1"><spring:theme code="basket.page.quantity"/></option>
						<formElement:formProductQuantitySelectOption stockLevel="${productData.stock.stockLevel}" startSelectBoxCounter="1" quantity="${qty}"/>
					</select>
					<input type="hidden" name="productCodePost" value="${productData.code}"/>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="item_container_holder">
	<div id="globalMessages"><common:globalMessages/></div>

	<div id="pickUpInStoreSearchForms">
		<h3><spring:theme code="pickup.product.by.store.location" text="Product by store location" /></h3>
		<p><spring:theme code="pickup.location.required" text="Please provide your location to see products available in your area." /></p>
		<div class="item_container pickUpInStore">
			<c:url value="/store-pickup/${productData.code}/pointOfServices" var="pickUpInStoreFormAction" />
			<div data-role="fieldcontain" class="accmob-storeSearch">
				<form:form id="locationQueryStorefinderForm" name="location_query_storefinder_form" action="${pickUpInStoreFormAction}" method="POST">
					<div class="accmob-storeFinderFieldHolder">
						<label for="storelocator-query" class="skip"><spring:theme code="storeFinder.stores.nearby"/></label>
						<input id="storelocator-query" name="locationQuery" class="storeSearchBox" type="search" placeholder="<spring:theme code="storelocator.postcode.city.search"/>">
						<input type="hidden" name="entryNumber" value="${entryNumber}"/>
						<input type="hidden" name="cartPage" value="${cartPage}"/>
						<div class="accmob-storeSearch-trigger">
							<button class="form search storeSearchButton" data-role="button" data-theme="c" id="findStoresByQuery">
								<span class="search-icon"><spring:theme code="storeFinder.search" /></span>
							</button>
						</div>
					</div>
				</form:form>
				<div class="line-text clearfix"><span><spring:theme code="storeFinder.line.text"/></span></div>
				<form:form id="nearMeStorefinderForm" name="near_me_storefinder_form" action="${pickUpInStoreFormAction}" method="POST" >
					<input type="hidden" id="latitude" name="latitude"/>
					<input type="hidden" id="longitude" name="longitude"/>
					<input type="hidden" name="entryNumber" value="${entryNumber}"/>
					<input type="hidden" name="cartPage" value="${cartPage}"/>
					<a href="#" id="findStoresNearMe" class="form search findStoresNearMeButton" data-theme="d" data-role="button" data-icon="custom ui-icon-custom-storesearch">
						<span class="search-icon"><spring:theme code="storeFinder.findStoresNearMe"/></span>
					</a>
				</form:form>
			</div>
		</div>
	</div>
	<div id="pickUpInStoreResultsList" style="display:none"></div>
</div>