<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="mproduct" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<h2 class="productHeadline" data-dialogheader="${product.name}">
	<div class="productTitle">${product.name}</div>
	<br/>
	<mproduct:productAverageReviewDetailPage product="${product}"/>
</h2>
<c:url value="/cart/add" var="addToCartUrl"/>
<form:form id="addToCartForm" class="add_to_cart_form" action="${addToCartUrl}" method="post">
	<div class='ui-grid-a accmob-quickviewItemInfo'>
		<div class='ui-block-a'>
			<a href='<c:url value="${product.url}"/>' data-url="${productQuickViewUrl}" data-rel="dialog" data-transition="pop">
				<mproduct:productPrimaryImage product="${productReference.target}" format="thumbnail" zoomable='false'/>
			</a>
		</div>
		<div class='ui-block-b'>
			<a href='<c:url value="${product.url}"/>'> <spring:theme code="product.product.details.more"/></a>
			<%-- promotions --%>
			<div class="itemPromotionBox itemPromotionBoxSmall" data-theme="b">
				<ycommerce:testId code="productDetails_promotion_label">
					<c:if test="${not empty product.potentialPromotions}">
						<c:choose>
							<c:when test="${not empty product.potentialPromotions[0].couldFireMessages}">
								<p>${product.potentialPromotions[0].couldFireMessages[0]}</p>
							</c:when>
							<c:otherwise>
								<p>${product.potentialPromotions[0].description}</p>
							</c:otherwise>
						</c:choose>
					</c:if>
				</ycommerce:testId>
			</div>
			<span class="accmob-quickviewItemPrice"><format:price priceData="${product.price}"/></span>
		</div>
	</div>
	<c:set var="purchasable" value="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock'}"/>
	<c:set var="disabled" value="${purchasable ? '' : 'disabled=\"disabled\"'}" />
	<div class='ui-grid-a accmob-quickviewItemQA'>
		<div class='ui-block-a'>
			<div class="prod_add_to_cart_quantity">
				<label for="qty" class="skip"><spring:theme code="basket.page.quantity"/></label>
				<select id="qty" class='qty' name="qty" data-theme="d" ${disabled}>
					<option value="1"><spring:theme code="basket.page.quantity"/></option>
					<c:choose>
						<c:when test="${product.stock.stockLevel le 100}">
							<c:set var="maxQuantity" value="${product.stock.stockLevel}"/>
						</c:when>
						<c:otherwise>
							<c:set var="maxQuantity" value="100"/>
						</c:otherwise>
					</c:choose>
					<c:forEach var="i" begin="1" end="${maxQuantity}">
						<option value="${i}">${i}</option>
					</c:forEach>
				</select>
				<input type="hidden" name="productCodePost" value="${product.code}"/>
			</div>
		</div>
		<div class="ui-block-b">
			<div class="accmob-quickviewItemAvailability">
				<c:choose>
					<c:when test="${purchasable}">
						${product.stock.stockLevel} &nbsp; <spring:theme code="product.variants.in.stock"/>
					</c:when>
					<c:otherwise>
						<div id="outOfStock">
							<spring:theme code="text.addToCart.outOfStock"/>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
	<div id='addToBasket'>
		<c:set var="buttonType" value="${purchasable ? 'submit' : 'button'}"/>
		<button type="${buttonType}"
				data-rel="dialog"
				data-transition="pop"
				data-theme="b"
				class="positive large" ${disabled}>
			<spring:theme code="text.addToCart${purchasable ? '' : '.outOfStock'}"/>
		</button>
		<c:if test="${product.availableForPickup}">
			<a href="#" class="pickUpInStoreButton" data-productcode="${product.code}" data-rel="dialog" data-transition="pop" data-role="button" data-theme="c">
				<spring:theme code="pickup.in.store"/>
			</a>
		</c:if>
	</div>
</form:form>
