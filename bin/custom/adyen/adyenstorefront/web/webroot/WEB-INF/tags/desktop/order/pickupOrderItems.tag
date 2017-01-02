<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="groupData" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryGroupData" %>
<%@ attribute name="index" required="true" type="java.lang.Integer" %>
<%@ attribute name="showPotentialPromotions" required="false" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<div class="pickup_item_container_holder"  id="pickup_item_container_holder_${index}">
	<div class="item_container">
		<table class="your_cart">
			<thead>
				<tr>
					<th id="header_2" colspan="2"><span class="hidden"><spring:theme code="basket.page.title"/></span></th>
					<th id="header_3"><spring:theme code="basket.page.quantity"/></th>
					<th id="header_4"><spring:theme code="basket.page.itemPrice"/></th>
					<th id="header_5"><spring:theme code="basket.page.total"/></th>
				</tr>
			</thead>
			<tbody id="cart_items_tbody">
				<c:forEach items="${groupData.entries}" var="entry">
					
						<c:url value="${entry.product.url}" var="productUrl"/>
						<tr>
							<td headers="header_2" class="product_image">
								<span class="product_image">
									<a href="${productUrl}">
										<product:productPrimaryImage product="${entry.product}" format="thumbnail"/>
									</a>
								</span>
							</td>
							<td headers="header_2" class="product_details">
	
								<h2><a href="${productUrl}">${entry.product.name}</a></h2>
	
								<c:forEach items="${entry.product.baseOptions}" var="option">
									<c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
										<c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
											<dl>
												<dt>${selectedOption.name}:</dt>
												<dd>${selectedOption.value}</dd>
											</dl>
										</c:forEach>
									</c:if>
								</c:forEach>
		
								<c:if test="${ycommerce:doesPotentialPromotionExistForOrderEntry(cartData, entry.entryNumber) && showPotentialPromotions}">
									<ul class="cart-promotions">
										<c:forEach items="${cartData.potentialProductPromotions}" var="promotion">
											<c:set var="displayed" value="false"/>
											<c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
												<c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
													<c:set var="displayed" value="true"/>
													<li class="cart-promotions-potential"><span>${promotion.description}</span></li>
												</c:if>
											</c:forEach>
										</c:forEach>
									</ul>
								</c:if>
								<c:if test="${ycommerce:doesAppliedPromotionExistForOrderEntry(cartData, entry.entryNumber)}">
									<ul class="cart-promotions">
										<c:forEach items="${cartData.appliedProductPromotions}" var="promotion">
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
	
							</td>
							<td headers="header_3" class="quantity">
								${entry.quantity}
							</td>
							<td headers="header_4" class="itemPrice">
								<format:price priceData="${entry.basePrice}" displayFreeForZero="true"/>
							</td>
							<td headers="header_5" class="total">
								<format:price priceData="${entry.totalPrice}" displayFreeForZero="true"/>
							</td>
						</tr>
					
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>
