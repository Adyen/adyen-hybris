<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="cartPage" required="true" type="java.lang.Boolean"%>
<%@ attribute name="entryNumber" required="false" type="java.lang.Long"%>
<%@ attribute name="deliveryPointOfService" required="false" type="java.lang.String"%>
<%@ attribute name="quantity" required="false" type="java.lang.Integer"%>
<%@ attribute name="searchResultsPage" required="false" type="java.lang.Boolean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>


<c:set var="defaultUrl" value="/store-pickup/${product.code}/pointOfServices"/>
<c:url var="pickUpInStoreFormAction" value="${empty actionUrl ? defaultUrl : actionUrl}"/>
<ycommerce:testId code="pickupInStoreButton">
	<c:choose>
		<c:when test="${cartPage}">


			<a href="javascript:void(0)"
			   class="btn btn-default btn-block js-pickup-in-store-button"
			   id="product_${product.code}${entryNumber}" disabled="disabled"
				data-productcart='${product.price.formattedValue}'
				data-productcart-variants='{
						<c:if test="${not empty product.baseOptions[0].selected.variantOptionQualifiers}">
							<c:forEach var="variant" items="${product.baseOptions[0].selected.variantOptionQualifiers}" varStatus="productcartVariantsNumber">
								<c:if test="${not empty variant.value}">
								"${variant.name}":"<spring:theme code="basket.pickup.product.variant" arguments="${variant.name},${variant.value}" />"<c:if test="${!productcartVariantsNumber.last}">,</c:if>
								</c:if>
							</c:forEach>
						</c:if>
				}'
			   data-img='<product:productPrimaryImage product="${product}" format="thumbnail"/>'
			   data-productname="${product.name}"
			   data-cartpage="${cartPage}"
			   data-entryNumber="${entryNumber}"
			   data-actionurl="${pickUpInStoreFormAction}"
			   data-value="${quantity}">
				<c:choose>
					<c:when test="${not empty deliveryPointOfService}">
						<spring:theme code="basket.page.shipping.change.store"/>
					</c:when>
					<c:otherwise>
						<spring:theme code="basket.page.shipping.find.store" text="Find A Store"/>
					</c:otherwise>
				</c:choose>
			</a>
		</c:when>
		<c:when test="${searchResultsPage}">
			<button class="btn btn-default btn-block js-pickup-in-store-button" disabled="disabled" id="product_${product.code}${entryNumber}" type="button submit"

			data-productcart='${product.price.formattedValue}'
			data-productcart-variants='{
					<c:if test="${not empty product.baseOptions[0].selected.variantOptionQualifiers}">
						<c:forEach var="variant" items="${product.baseOptions[0].selected.variantOptionQualifiers}" varStatus="productcartVariantsNumber">
							<c:if test="${not empty variant.value}">
							"${variant.name}":"<spring:theme code="basket.pickup.product.variant" arguments="${variant.name},${variant.value}" />"<c:if test="${!productcartVariantsNumber.last}">,</c:if>
							</c:if>
						</c:forEach>
					</c:if>
			}'

			data-img='<product:productPrimaryImage product="${product}" format="thumbnail"/>' data-productname="${product.name}" data-cartpage="false" data-entryNumber="0" data-actionurl="${pickUpInStoreFormAction}" data-value="1">
				<spring:theme code="pickup.in.store"/>
			</button>
		</c:when>
		<c:otherwise>
			<button class="btn btn-default btn-block js-pickup-in-store-button" disabled="disabled" id="product_${product.code}${entryNumber}" type="submit" data-productavailable="${product.availableForPickup}"
			data-productcart='${product.price.formattedValue}'
			data-productcart-variants='{
					<c:if test="${not empty product.baseOptions[0].selected.variantOptionQualifiers}">
						<c:forEach var="variant" items="${product.baseOptions[0].selected.variantOptionQualifiers}" varStatus="productcartVariantsNumber">
							<c:if test="${not empty variant.value}">
							"${variant.name}":"<spring:theme code="basket.pickup.product.variant" arguments="${variant.name},${variant.value}" />"<c:if test="${!productcartVariantsNumber.last}">,</c:if>
							</c:if>
						</c:forEach>
					</c:if>
			}'


				data-img='<product:productPrimaryImage product="${product}" format="thumbnail"/>' data-productname="${product.name}" data-cartpage="false" data-entryNumber="0" data-actionurl="${pickUpInStoreFormAction}" data-value="1">
				<spring:theme code="pickup.in.store"/>
			</button>
		</c:otherwise>
	</c:choose>
</ycommerce:testId>


