<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="pickupConsolidationOptions" required="true" type="java.util.List" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/mobile/store" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:if test="${not empty pickupConsolidationOptions}">
	<c:url value="${currentStepUrl}" var="consolidatePickupUrl"/>
	<div class="span-20">
		<div class="selectDeliverylocation">
			<form:form id="selectDeliverylocationForm" action="${consolidatePickupUrl}" method="POST">
				<h3>
					<spring:theme code="checkout.pickup.items.available.at.one.location" />
				</h3>
				<div class="selectDeliverylocationDescText"><spring:theme code="checkout.pickup.items.at.one.location" /></div>
				<fieldset data-role="controlgroup" data-theme="d">
					<c:forEach items="${pickupConsolidationOptions}" var="option" varStatus="status">
						<div data-theme="d" class="selectDeliverylocation-list">
							<input class="selectDeliverylocationItemOption" type="radio" name="posName" value="${option.name}" id="${option.name}" data-theme="d" <c:if test='${status.first}'>checked="checked"</c:if> />
							<label class="selectDeliverylocationItemLabel" for="${option.name}">
								<ul class="mFormList pickupAdressList">
									<c:if test="${not empty userLocation}"><li class="pickupOptionDistance">${option.formattedDistance}</li></c:if>
									<li>${fn:escapeXml(option.name)}</li>
									<li>${fn:escapeXml(option.address.line1)}</li>
									<li>${fn:escapeXml(option.address.line2)}</li>
									<li>${fn:escapeXml(option.address.town)}</li>
									<li>${fn:escapeXml(option.address.postalCode)}</li>
									<li>${fn:escapeXml(option.address.country.name)}</li>
								</ul>
							</label>
						</div>
					</c:forEach>
				</fieldset>
				<div class="selectDeliverylocationItem">
					<span class="selectDeliverylocationDescText"><spring:theme code="checkout.pickup.items.simplify.pickup.location" /></span>
					<button id="chooseDeliveryLocation_simplify_button" data-role="button" data-theme="f">
						<spring:theme code="checkout.pickup.simplifyPickup" />
					</button>
				</div>
			</form:form>
		</div>
	</div>	
</c:if>
