<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ attribute name="consignment" required="true" type="de.hybris.platform.commercefacades.order.data.ConsignmentData" %>
<%@ attribute name="inProgress" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/mobile/order" %>


<c:choose>
	<c:when test="${not inProgress}">
		<div class="productItemHeader">
			<c:if test="${consignment.status.code eq 'READY_FOR_PICKUP'}" >
				<span class="pick-up-reminder"><spring:theme code="text.account.order.warning.storePickUpItems" text="Reminder - Please pick up your items(s) soon."/></span>
			</c:if>
			<h3><spring:theme code="text.account.order.consignment.status.${consignment.statusDisplay}"/>:</strong>  <fmt:formatDate value="${consignment.statusDate}" pattern="MM/dd/yy"/></h3>
			<c:choose>
				<c:when test="${consignment.status.code eq 'SHIPPED'}" >
					<div class="ui-grid-a productItemFullPusher">
						<h5><spring:theme code="text.account.order.tracking" text="Tracking #:" />
							<c:choose>
								<c:when test="${not empty consignment.trackingID}">${consignment.trackingID}</c:when>
								<c:otherwise>
									<spring:theme code="text.account.order.consignment.trackingID.notavailable" text="Not available."/>
								</c:otherwise>
							</c:choose>
						</h5>
					</div>
				</c:when>
			</c:choose>
		</div>
	</c:when>
	<c:otherwise>
		<div class="productItemHeader productItemHeader-en">
			<c:choose>
				<c:when test="${consignment.deliveryPointOfService ne null}">
					<h3><spring:theme code="text.account.order.title.storePickUpItems" /></h3>
				</c:when>
				<c:otherwise>
					<h3><spring:theme code="text.account.order.title.deliveryItems" /></h3>
				</c:otherwise>
			</c:choose>
		</div>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${consignment.status.code eq 'READY_FOR_PICKUP'}" >
		<c:set var="pos" value="${consignment.entries[0].orderEntry.deliveryPointOfService}"/>
		<div class="ui-grid-a productItemFullPusher" data-theme="b">
			<h4>
				<spring:theme code="text.account.order.pickup.location" text="Pick Up Location:" />
			</h4>
			<ul class="mFormList itemsList productItemListDetailsHolder listview">
				<li class="checkoutOverview-PickUp-Items-Location">
					<span class="pickup_store_results-entry pickup_store_results-name">${pos.name}</span>
					<span class="pickup_store_results-entry pickup_store_results-line1">${pos.address.line1}</span>
					<span class="pickup_store_results-entry pickup_store_results-line2">${pos.address.line2}</span>
					<span class="pickup_store_results-entry pickup_store_results-town">${pos.address.line2}</span>
				</li>
			</ul>
		</div>
	</c:when>
</c:choose>
<c:forEach items="${consignment.entries}" var="entry">
	<order:accountOrderEntry entry="${entry.orderEntry}"/>
</c:forEach>
