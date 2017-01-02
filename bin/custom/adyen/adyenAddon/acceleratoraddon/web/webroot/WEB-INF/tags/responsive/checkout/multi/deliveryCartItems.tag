<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="showDeliveryAddress" required="true" type="java.lang.Boolean" %>
<%@ attribute name="showPotentialPromotions" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:set var="hasShippedItems" value="${cartData.deliveryItemsQuantity > 0}" />
<c:set var="deliveryAddress" value="${cartData.deliveryAddress}"/>

<c:if test="${not hasShippedItems}">
	<spring:theme code="checkout.pickup.no.delivery.required"/>
</c:if>

<c:if test="${hasShippedItems}">
	<li class="section">
		<c:choose>
			<c:when test="${showDeliveryAddress and not empty deliveryAddress}">
				<div class="title"><spring:theme code="checkout.pickup.items.to.be.shipped" text="Ship To:"/></div>
				<div class="address">
					${fn:escapeXml(deliveryAddress.title)}&nbsp;${fn:escapeXml(deliveryAddress.firstName)}&nbsp;${fn:escapeXml(deliveryAddress.lastName)}
					<br>
					<c:if test="${ not empty deliveryAddress.line1 }">
						${fn:escapeXml(deliveryAddress.line1)},&nbsp;
					</c:if>
					<c:if test="${ not empty deliveryAddress.line2 }">
						${fn:escapeXml(deliveryAddress.line2)},&nbsp;
					</c:if>
					<c:if test="${not empty deliveryAddress.town }">
						${fn:escapeXml(deliveryAddress.town)},&nbsp;
					</c:if>
					<c:if test="${ not empty deliveryAddress.region.name }">
						${fn:escapeXml(deliveryAddress.region.name)},&nbsp;
					</c:if>
					<c:if test="${ not empty deliveryAddress.postalCode }">
						${fn:escapeXml(deliveryAddress.postalCode)},&nbsp;
					</c:if>
					<c:if test="${ not empty deliveryAddress.country.name }">
						${fn:escapeXml(deliveryAddress.country.name)}
					</c:if>
				</div>
			</c:when>
			<c:otherwise>
				<div class="alternatetitle"><spring:theme code="checkout.pickup.items.to.be.delivered" /></div>
			</c:otherwise>
		</c:choose>
		
	</li>
</c:if>

<c:forEach items="${cartData.entries}" var="entry">
	<c:if test="${entry.deliveryPointOfService == null}">
		<c:url value="${entry.product.url}" var="productUrl"/>
		<li>
			<div class="thumb">
				<a href="${productUrl}">
					<product:productPrimaryImage product="${entry.product}" format="thumbnail"/>
				</a>
			</div>
			<div class="price"><format:price priceData="${entry.basePrice}" displayFreeForZero="true"/></div>
			<div class="details">
				<div class="name"><a href="${productUrl}">${entry.product.name}</a></div>
				<div class="qty"><spring:theme code="basket.page.qty"/>&nbsp;${entry.quantity}</div>
				<div class="variants">
					<c:forEach items="${entry.product.baseOptions}" var="option">
						<c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
							<c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
								<div>${selectedOption.name}: ${selectedOption.value}</div>
							</c:forEach>
						</c:if>
					</c:forEach>
					
					<c:if test="${ycommerce:doesPotentialPromotionExistForOrderEntry(cartData, entry.entryNumber) && showPotentialPromotions}">
						<ul>
							<c:forEach items="${cartData.potentialProductPromotions}" var="promotion">
								<c:set var="displayed" value="false"/>
								<c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
									<c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
										<c:set var="displayed" value="true"/>
										<span>${promotion.description}</span>
									</c:if>
								</c:forEach>
							</c:forEach>
						</ul>
					</c:if>
					
					
					<c:if test="${ycommerce:doesAppliedPromotionExistForOrderEntry(cartData, entry.entryNumber)}">
						<ul>
							<c:forEach items="${cartData.appliedProductPromotions}" var="promotion">
								<c:set var="displayed" value="false"/>
								<c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
									<c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
										<c:set var="displayed" value="true"/>
										<span>${promotion.description}</span>
									</c:if>
								</c:forEach>
							</c:forEach>
						</ul>
					</c:if>
				</div>
			</div>
			<!--  <div class="stock-status">Item In Stock</div> -->
		
		</li>
	</c:if>
</c:forEach>

