<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="groupData" required="true" type="de.hybris.platform.acceleratorfacades.order.data.OrderEntryGroupData" %>
<%@ attribute name="index" required="true" type="java.lang.Integer" %>
<%@ attribute name="showPotentialPromotions" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showHead" required="false" type="java.lang.Boolean" %>

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



<table class="pickupCartItems">
	<c:if test="${showHead}">
	<thead>
		<tr>
			<td colspan="4"><spring:theme code="text.checkout.pickup.in.store" /> </td>
		</tr>
	</thead>
	</c:if>
	<tbody>
		<c:if test="${showHead}">
			<tr class="address"><td colspan="4">
				<div class="storeName">${groupData.deliveryPointOfService.name}</div>
				${groupData.deliveryPointOfService.address.formattedAddress}</td>
			</tr>
		</c:if>
		<c:forEach items="${groupData.entries}" var="entry">		
			<c:url value="${entry.product.url}" var="productUrl"/>
			<tr>
				<td rowspan="2" class="thumb">
					<a href="${productUrl}">
						<product:productPrimaryImage product="${entry.product}" format="thumbnail"/>
					</a>
				</td>
				<td colspan="3" class="desc">
					<div class="name"><a href="${productUrl}">${entry.product.name}</a></div>
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
			</tr>
			<tr>
				<td class="priceRow"><format:price priceData="${entry.basePrice}" displayFreeForZero="true"/></td>
				<td class="priceRow"><spring:theme code="basket.page.qty"/>: ${entry.quantity}</td>
				<td class="priceRow"><format:price priceData="${entry.totalPrice}" displayFreeForZero="true"/></td>
			</tr>
		
		
		
		
		</c:forEach>
		
	</tbody>
</table>


