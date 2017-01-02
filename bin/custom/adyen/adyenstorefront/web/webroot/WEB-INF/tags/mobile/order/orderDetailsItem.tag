<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData"%>
<%@ attribute name="orderGroup" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryGroupData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<div data-theme="b" data-role="content">
	<c:forEach items="${orderGroup.entries}" var="entry">
		<div class="cartLi">
			<ycommerce:testId code="cart_product_name">
				<h4 class="ui-li-heading cartProductTitle">
					<ycommerce:testId code="orderDetails_productName_link">
						<p>${entry.product.name}</p>
					</ycommerce:testId>
				</h4>
			</ycommerce:testId>
			<div class="ui-grid-a cartItemproductImage">
				<div class="ui-block-a">
					<p>
						<product:productPrimaryImage product="${entry.product}" format="thumbnail" zoomable="false" />
					</p>
				</div>
				<div class="ui-block-b cartItemproductOptions">
					<c:forEach items="${entry.product.baseOptions}" var="option">
						<c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
							<c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
								<div class="ui-grid-a">
									<div class="ui-block-a">${selectedOption.name}</div>
									<div class="ui-block-b">${selectedOption.value}</div>
								</div>
							</c:forEach>
						</c:if>
					</c:forEach>
					<div class="ui-grid-a">
						<div class="ui-block-a">
							<spring:theme code="basket.page.quantity" />
						</div>
						<div class="ui-block-b">${entry.quantity}</div>
					</div>
					<div class="ui-grid-a">
						<div class="ui-block-a">
							<spring:theme code="basket.page.itemPrice" />
						</div>
						<div class="ui-block-b">
							<format:price priceData="${entry.basePrice}" displayFreeForZero="true" />
						</div>
					</div>
					<div class="ui-grid-a">
						<div class="ui-block-a">
							<spring:theme code="basket.page.total" />
						</div>
						<div class="ui-block-b">
							<format:price priceData="${entry.totalPrice}" displayFreeForZero="true" />
						</div>
					</div>
				</div>
			</div>
			<div class="ui-grid-a">
				<c:if test="${not empty order.appliedProductPromotions}">
					<ul class="cart-promotions itemPromotionBox">
						<c:forEach items="${order.appliedProductPromotions}" var="promotion">
							<c:set var="displayed" value="false" />
							<c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
								<c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
									<c:set var="displayed" value="true" />
									<li class="cart-promotions-applied"><span>${promotion.description}</span></li>
								</c:if>
							</c:forEach>
						</c:forEach>
					</ul>
				</c:if>
			</div>
		</div>
	</c:forEach>
</div>
