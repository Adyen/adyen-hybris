<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="groupData" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryGroupData" %>
<%@ attribute name="index" required="true" type="java.lang.Integer" %>
<%@ attribute name="showPotentialPromotions" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showHead" required="false" type="java.lang.Boolean" %>

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

<c:if test="${showHead}">
	<li class="section">
		<div class="title"><spring:theme code="checkout.multi.items.to.pickup" text="Pick up:"/></div>
		<div class="address">
			${groupData.deliveryPointOfService.name}
			<br>
			<c:if test="${ not empty groupData.deliveryPointOfService.address.line1 }">
						${fn:escapeXml(groupData.deliveryPointOfService.address.line1)},&nbsp;
			</c:if>
			<c:if test="${ not empty groupData.deliveryPointOfService.address.line2 }">
				${fn:escapeXml(groupData.deliveryPointOfService.address.line2)},&nbsp;
			</c:if>
			<c:if test="${not empty groupData.deliveryPointOfService.address.town }">
				${fn:escapeXml(groupData.deliveryPointOfService.address.town)},&nbsp;
			</c:if>
			<c:if test="${ not empty groupData.deliveryPointOfService.address.region.name }">
				${fn:escapeXml(groupData.deliveryPointOfService.address.region.name)},&nbsp;
			</c:if>
			<c:if test="${ not empty groupData.deliveryPointOfService.address.postalCode }">
				${fn:escapeXml(groupData.deliveryPointOfService.address.postalCode)},&nbsp;
			</c:if>
			<c:if test="${ not empty groupData.deliveryPointOfService.address.country.name }">
				${fn:escapeXml(groupData.deliveryPointOfService.address.country.name)}
			</c:if>
		</div>
	</li>
</c:if>
<c:forEach items="${groupData.entries}" var="entry">		
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
				</div>
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
		
			<!--  <div class="stock-status">PickUpInStore Availability
				Notification</div> -->
		
		</li>
</c:forEach>

