<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="allowAddToCart" required="true" type="java.lang.Boolean" %>
<%@ attribute name="isMain" required="true" type="java.lang.Boolean" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="mproduct" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/mobile/action" %>

<div class="prod_add_to_cart" data-theme="b">

	<div class="prod_add_to_cart_submit">
		<div class="ui-grid-a">
			<div class="ui-block-a">
				<div class="prod_add_to_cart_quantity">
					<c:choose>
						<c:when test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock'}">
							<label for="qty" class="skip">
								<spring:theme code="basket.page.quantity"/>
							</label>
							<select id="qty" class='qty qtySelector' name="qty" data-theme="d">
								<option value="1">
									<spring:theme code="basket.page.quantity"/>
								</option>
								<formElement:formProductQuantitySelectOption stockLevel="${product.stock.stockLevel}" startSelectBoxCounter="1"/>
							</select>
						</c:when>
						<c:otherwise>
							<select class="noSelectMenu" disabled='true'>
								<option value="1">
									<spring:theme code="basket.page.quantity"/>
								</option>
							</select>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="ui-block-b" id="priceAndQuantity">
				<c:choose>
					<c:when test="${not empty variantSizes}">
						<c:choose>
							<c:when test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock'}">
								<div class="productprice">
									<ycommerce:testId code="product_price_value">
										<format:price
												priceData="${product.price}"/>
									</ycommerce:testId>
								</div>
								<c:if test="${product.stock.stockLevelStatus.code == 'lowStock' and showAddToCart}">
									<div id="outOfStock">
										<spring:theme code="product.variants.only.left" arguments="${product.stock.stockLevel}"/>
									</div>
								</c:if>
							</c:when>
							<c:when test="${(sizeSelected == false) and not empty variantSizes}">
								<div class="productprice">
									<format:price priceData="${product.price}"/>
								</div>
								<div id="stockLevel">
									<spring:theme code="product.variants.select.size"/>
								</div>
							</c:when>
							<c:otherwise>
								<div class="productprice">
									<format:price priceData="${product.price}"/>
								</div>
								<c:if test="${product.stock.stockLevelStatus.code == 'lowStock'}">
									<div id="outOfStock">
										<spring:theme code="product.variants.only.left" arguments="${product.stock.stockLevel}"/>
									</div>
								</c:if>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${product.purchasable and product.stock.stockLevelStatus.code ne 'outOfStock'}">
								<c:choose>
									<c:when test="${product.stock.stockLevelStatus == 'lowStock'}">
										<c:set var="productStockLevel">
											<spring:theme code="product.variants.only.left" arguments="${product.stock.stockLevel}"/>
										</c:set>
									</c:when>
									<c:otherwise>
										<c:set var="productStockLevel">${product.stock.stockLevel} &nbsp;
											<spring:theme code="product.variants.in.stock"/>
										</c:set>
									</c:otherwise>
								</c:choose>
								<div class="productprice">
									<ycommerce:testId code="product_price_value">
										<mproduct:productPricePanel product="${product}"/>
									</ycommerce:testId>
								</div>
								<div id="stockLevel">${productStockLevel}</div>
							</c:when>
							<c:otherwise>
								<div class="productprice">
									<ycommerce:testId code="product_price_value">
										<format:price
												priceData="${product.price}"/>
									</ycommerce:testId>
								</div>
								<c:if test="${showAddToCart}">
									<div id="outOfStock">
										<spring:theme code="product.variants.out.of.stock"/>
									</div>
								</c:if>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<%-- promotions --%>
		<ycommerce:testId code="productDetails_promotion_label">
			<c:if test="${not empty product.potentialPromotions}">
				<div class="itemPromotionBox" data-theme="b">
					<c:choose>
						<c:when test="${not empty product.potentialPromotions[0].couldFireMessages}">
							<p>${product.potentialPromotions[0].couldFireMessages[0]}</p>
						</c:when>
						<c:otherwise>
							<p>${product.potentialPromotions[0].description}</p>
						</c:otherwise>
					</c:choose>
				</div>
			</c:if>
		</ycommerce:testId>


		<div id="actions-container-for-${component.uid}" class="productAddToCartPanelContainer clearfix">
			<action:actions/>
		</div>

	</div>
</div>
