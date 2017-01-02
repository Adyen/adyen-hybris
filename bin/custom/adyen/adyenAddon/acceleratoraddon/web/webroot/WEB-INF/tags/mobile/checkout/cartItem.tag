<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="entry" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryData" %>
<%@ attribute name="showPotentialPromotions" required="false" type="java.lang.Boolean" %>

<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product" %>

<c:url value="${entry.product.url}" var="entryProductUrl"/>
<li class='cartLi'>
	<ycommerce:testId code="cart_product_name">
		<h4 class="ui-li-heading cartProductTitle">
			<a href="${entryProductUrl}" data-transition="slide">${entry.product.name}</a>
		</h4>
	</ycommerce:testId>

	<div class="ui-grid-a cartItemproductImage">
		<div class="ui-block-a">
			<a href="${entryProductUrl}" data-transition="slide">
				<product:productCartImage product="${entry.product}" format="thumbnail"/>
			</a>
		</div>
		<div class="ui-block-b">
			<div class="ui-grid-a" data-role="content">
				<div class="ui-block-a">
					<p>${entry.product.description}</p>
				</div>
			</div>
			<c:forEach items="${entry.product.baseOptions}" var="option">
				<c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
					<c:forEach items="${option.selected.variantOptionQualifiers}"
							   var="selectedOption">
						<div class="ui-grid-a">
							${selectedOption.name}:&nbsp;<span
								class="quantityItemHolder">${selectedOption.value}</span>
						</div>
					</c:forEach>
				</c:if>
			</c:forEach>

			<div class="ui-grid-a">
				<spring:theme code="basket.page.quantity"/>
				:&nbsp;<span class="quantityItemHolder">${entry.quantity}</span>
			</div>
			<div class="ui-grid-a">
				<spring:theme code="basket.page.itemPrice"/>
				:&nbsp;<span class="priceItemHolder"><format:price priceData="${entry.basePrice}"
																   displayFreeForZero="true"/></span>
			</div>
			<div class="ui-grid-a">
				<spring:theme code="basket.page.total"/>
				:&nbsp;<span class="priceItemHolder"><format:price priceData="${entry.totalPrice}"
																   displayFreeForZero="true"/></span>
			</div>
		</div>
	</div>
	<div class="ui-grid-a">
		<c:if test="${not empty cartData.potentialProductPromotions && showPotentialPromotions}">
			<c:forEach items="${cartData.potentialProductPromotions}" var="promotion">
				<c:set var="displayed" value="false"/>
				<c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
					<c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber && not empty promotion.description}">
						<c:set var="displayed" value="true"/>
						<ul class="cart-promotions itemPromotionBox">
							<li class="cart-promotions-potential">
								<ycommerce:testId code="cart_promotion_label">
									<span>${promotion.description}</span>
								</ycommerce:testId>
							</li>
						</ul>
					</c:if>
				</c:forEach>
			</c:forEach>
		</c:if>
		<c:if test="${not empty cartData.appliedProductPromotions}">
			<ul class="cart-promotions  itemPromotionBox">
				<c:forEach items="${cartData.appliedProductPromotions}" var="promotion">
					<c:set var="displayed" value="false"/>
					<c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
						<c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
							<c:set var="displayed" value="true"/>
							<li class="cart-promotions-applied">
								<ycommerce:testId code="cart_appliedPromotion_label">
									<span>${promotion.description}</span>
								</ycommerce:testId>
							</li>
						</c:if>
					</c:forEach>
				</c:forEach>
			</ul>
		</c:if>
	</div>
</li>