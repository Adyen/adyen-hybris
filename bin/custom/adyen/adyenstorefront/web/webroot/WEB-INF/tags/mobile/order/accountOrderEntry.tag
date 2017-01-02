<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="entry" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>


<div class="cartLi">
	<c:url value="${entry.product.url}" var="entryProductUrl"/>
	<ycommerce:testId code="cart_product_name">
		<h4 class="ui-li-heading cartProductTitle ">
			<ycommerce:testId code="orderDetails_productName_link">
				<a href="${entryProductUrl}" data-transition="slide">${entry.product.name}</a>
			</ycommerce:testId>
		</h4>
	</ycommerce:testId>
	<div class="ui-grid-a productItemHolder">
		<div class="ui-block-a">
			<a href="${entryProductUrl}" data-transition="slide">
				<p><product:productPrimaryImage product="${entry.product}" format="thumbnail" zoomable="false"/></p>
			</a>
		</div>
		<div class="ui-block-b productItemListDetailsHolder">
			<c:forEach items="${entry.product.baseOptions}" var="option">
				<c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
					<c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
						<div class="ui-grid-a">
							${selectedOption.name}:
							&nbsp;<span class="quantityItemHolder">${selectedOption.value}</span>
						</div>
					</c:forEach>
				</c:if>
			</c:forEach>
			<div class="ui-grid-a">
				<spring:theme code="basket.page.quantity"/>:
				<span class="quantityItemHolder">
					${entry.quantity}
				</span>
			</div>
			<div class="ui-grid-a">
				<spring:theme code="basket.page.itemPrice"/>:
				<span class="priceItemHolder">
					<format:price priceData="${entry.basePrice}" displayFreeForZero="true"/>
				</span>
			</div>
			<div class="ui-grid-a">
				<spring:theme code="basket.page.total"/>:
				<span class="priceItemHolder">
					<format:price priceData="${entry.totalPrice}" displayFreeForZero="true"/>
				</span>
			</div>
		</div>
	</div>
	<div class="ui-grid-a potential-product-promotions">
		<c:if test="${not empty order.appliedProductPromotions}">
			<ul class="itemPromotionBox">
				<c:forEach items="${order.appliedProductPromotions}" var="promotion">
					<c:set var="displayed" value="false"/>
					<c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
						<c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
							<c:set var="displayed" value="true"/>
							<li class="cart-promotions-applied"><span>${promotion.description}</span></li>
						</c:if>
					</c:forEach>
				</c:forEach>
			</ul>
		</c:if>
	</div>
</div>