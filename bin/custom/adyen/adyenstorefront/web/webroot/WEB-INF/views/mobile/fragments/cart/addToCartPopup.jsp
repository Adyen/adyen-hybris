<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

{
	"cartData": {
		"total": "${cartData.totalPrice.value}",
		"products": [
			<c:forEach items="${cartData.entries}" var="cartEntry" varStatus="status">
				{
					"sku":		"${cartEntry.product.code}",
					"name":		"${cartEntry.product.name}",
					"qty":		"${cartEntry.quantity}",
					"price":	"${cartEntry.basePrice.value}",
					"categories": [ <c:forEach items="${cartEntry.product.categories}" var="category" varStatus="categoryStatus">
										"${category.name}"<c:if test="${not categoryStatus.last}">,</c:if>
									</c:forEach> ]
				}<c:if test="${not status.last}">,</c:if>
			</c:forEach>
			]
	},
	"cartAnalyticsData":{"cartCode" : "${cartCode}","productPostPrice":"${entry.basePrice.value}","productName":"${product.name}"}
	,
	"cartPopupHtml":	"<spring:escapeBody javaScriptEscape="true">
							<spring:theme code="text.addToCart" var="addToCartText"/>
							<c:url value="/cart" var="cartUrl"/>
							<c:url value="/cart/checkout" var="checkoutUrl"/>
							<ycommerce:testId code="cart_popup">
							<div class="dialog_container">
								<c:if test="${not empty errorMsg}">
									<ycommerce:testId code="cart_popup_error_msg">
										<div class="cart_popup_error_msg">
											<spring:theme code="${errorMsg}"/>
										</div>
									</ycommerce:testId>
								</c:if>
								<div class="dialog_container-content">
									<ycommerce:testId code="cart_popup_product_name">
										<h3>${product.name}</h3>
									</ycommerce:testId>
									<span class="prod_productimage">
										<product:productPrimaryImage product="${product}" format="cartIcon" zoomable="false"/>
									</span>
									<ycommerce:testId code="cart_popup_product_summary">
										<div class="ui-grid-a addToCartSummary" data-theme="b">
											<ycommerce:testId code="cart_popup_product_price">
											<div class="ui-block-a prod_baseprice">
												<spring:theme code="basket.page.price"/>
											</div>
											</ycommerce:testId>
											<div class="ui-block-b prod_baseprice">
												<format:price priceData="${entry.basePrice}"/>
											</div>
											<c:forEach items="${product.baseOptions}" var="baseOptions">
												<c:forEach items="${baseOptions.selected.variantOptionQualifiers}" var="baseOptionQualifier">
													<c:if test="${baseOptionQualifier.qualifier eq 'style' and not empty baseOptionQualifier.image.url}">
														<div class="ui-block-a prod_color">
															<spring:theme code="product.variants.colour"/>
														</div>
														<div class="ui-block-b prod_color">
															<img src="${baseOptionQualifier.image.url}" alt="${baseOptionQualifier.value}" title="${baseOptionQualifier.value}"/>
														</div>
													</c:if>
													<c:if test="${baseOptionQualifier.qualifier eq 'size'}">
														<div class="ui-block-a prod_size">
															<spring:theme code="product.variants.size"/>
														</div>
														<div class="ui-block-b prod_size">
															${baseOptionQualifier.value}
														</div>
													</c:if>
												</c:forEach>
											</c:forEach>
											<ycommerce:testId code="cart_popup_product_quantity">
												<div class="ui-block-a prod_quantity">
													<spring:theme code="popup.cart.quantity"/>
												</div>
												<div class="ui-block-b prod_quantity">
													${quantity}
												</div>
											</ycommerce:testId>
										</div>
									</ycommerce:testId>
									<div class="clear"></div>
								</div>
								<div class="dialog_container-pickup">
								<c:if test="${not empty entry.deliveryPointOfService}">
									<ycommerce:testId code="cart_popup_pickup_address">
										<h4>Pick Up from:</h4>
											<ul>
												<li>${entry.deliveryPointOfService.name}</li>
												<li>${entry.deliveryPointOfService.address.line1}</li>
												<li>${entry.deliveryPointOfService.address.line2}</li>
												<li>${entry.deliveryPointOfService.address.town}</li>
											</ul>
										</div>
									</ycommerce:testId>
								</c:if>
								<ycommerce:testId code="cart_popup_view_bag">
									<a href="${cartUrl}" data-role="button" data-theme="b">
										<spring:theme code="basket.view.basket"/>
									</a>
								</ycommerce:testId>
							</div></div>
							</ycommerce:testId>
						</spring:escapeBody>"
}
