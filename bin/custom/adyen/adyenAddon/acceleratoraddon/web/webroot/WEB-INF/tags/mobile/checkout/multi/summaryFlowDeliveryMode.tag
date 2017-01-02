<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="deliveryMode" value="${cartData.deliveryMode}" />
<c:set var="deliveryItems" value="${cartData.deliveryItemsQuantity}" />
<c:set var="pickupItems" value="${cartData.pickupItemsQuantity}" />
<c:set var="pickupDestinations" value="${fn:length(cartData.pickupOrderGroups)}" />

<div data-theme="b" data-role="content">
	<ycommerce:testId code="checkout_deliveryModeData_text">
		<div>
			<h4 class="subItemHeader">
				<spring:theme code="checkout.summary.deliveryMode.header" htmlEscape="false" />
				<span></span>
			</h4>
		</div>
		<div data-theme="b">
			<div class="ui-block-a" style="width: 85%">
				<ul class="mFormList">
					<c:if test="${deliveryItems > 0}">
						<li>${deliveryMode.name} (${deliveryMode.code})</li>
						<li class="deliverymode-description" title="${deliveryMode.description} - ${deliveryMode.deliveryCost.formattedValue}">
							${deliveryMode.description}&nbsp;-&nbsp;${deliveryMode.deliveryCost.formattedValue}
						</li>
					</c:if>
					<c:if test="${deliveryItems > 0 && pickupItems > 0}"></br></c:if>
					<c:if test="${pickupItems > 0}">
						<li><spring:theme code="checkout.summary.deliveryMode.items.for.pickup" arguments="${pickupItems}"/></li>
						<li><spring:theme code="checkout.summary.deliveryMode.number.of.pickup.destinations" arguments="${pickupDestinations}"/></li>
					</c:if>
				</ul>
			</div>
			<div class="ui-block-b" style="width: 15%">
				<c:if test="${deliveryItems > 0}">
					<ycommerce:testId code="checkout_changeDeliveryMode_element">
						<c:url value="${currentStepUrl}" var="chooseDeliveryMethodUrl" />
						<a href="${chooseDeliveryMethodUrl}" data-theme="c">
							<spring:theme code="mobile.checkout.edit.link" />
						</a>
					</ycommerce:testId>
				</c:if>
			</div>
		</div>
	</ycommerce:testId>
</div>
