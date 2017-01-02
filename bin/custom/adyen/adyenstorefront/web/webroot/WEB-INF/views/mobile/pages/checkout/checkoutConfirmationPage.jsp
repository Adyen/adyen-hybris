<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/mobile/order" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/mobile/user" %>

<template:page pageTitle="${pageTitle}">
	<jsp:body>
		<div id="globalMessages" data-theme="b">
			<common:globalMessages/>
		</div>

		<cms:pageSlot position="TopContent" var="feature">
			<cms:component component="${feature}" element="div" class="span-24 cms_disp-img_slot"/>
		</cms:pageSlot>

		<div class="item_container_holder" data-content-theme="d" data-theme="e">
			<div data-theme="b">
				<h3><spring:theme code="checkout.orderConfirmation.success" text="Your order was successful!"/></h3>
				<h4><spring:theme code="checkout.orderConfirmation.thankYouForOrder" text="Thank you for your order" arguments=""/></h4>
				<ul class="mFormList">
					<li><spring:theme code="checkout.orderConfirmation.orderNumberShort" text="Order # {0}" arguments="${orderData.code}"/></li>
					<c:if test="${not empty orderData.statusDisplay}">
						<li><spring:theme code="checkout.orderConfirmation.orderStatus" text="Order Status: {0}" arguments="${orderData.statusDisplay}"/></li>
					</c:if>
					<li><spring:theme code="text.account.order.orderPlaced" text="Placed on {0}" arguments="${orderData.created}"/></li>
				</ul>
			</div>
			<div class="span-20 last" data-theme="b">
				<div data-theme="d">
					<h3 class="summaryHeadline"><spring:theme code="checkout.orderConfirmation.summary" text="A summary of your order is below:"/></h3>
				</div>
				<div class="span-20 last delivery_stages" data-theme="b">
					<c:if test="${fn:length(orderData.deliveryOrderGroups) gt 0}">
						<order:addressItem address="${orderData.deliveryAddress}" type="delivery" />
					</c:if>
					<order:deliveryMethodItem order="${orderData}"/>
					<order:paymentMethodItem order="${orderData}"/>
				</div>
				<div class="orderOverviewItems">
					<h6 class="descriptionHeadline">
						<spring:theme code="text.headline.orderitems" text="All information about your order items"/>
					</h6>

					<div data-theme="b">
						<c:if test="${fn:length(orderData.deliveryOrderGroups) gt 0}">
							<h3>
								<spring:theme code="basket.page.title.yourDeliveryItems" text="Your Delivery Items"/>
							</h3>
							<c:forEach items="${orderData.deliveryOrderGroups}" var="orderGroup">
								<order:orderDetailsItem order="${orderData}" orderGroup="${orderGroup}" />
							</c:forEach>
						</c:if>
					</div>

					<c:if test="${fn:length(orderData.pickupOrderGroups) gt 0 && ycommerce:checkIfPickupEnabledForStore() eq true}">
					<div data-theme="b">
						<h3>
							<spring:theme code="basket.page.title.yourPickUpItems" text="Your Pickup Items"/>
						</h3>
						<c:forEach items="${orderData.pickupOrderGroups}" var="orderGroup">
							<c:set var="pos" value="${orderGroup.entries[0].deliveryPointOfService}"/>
							<h4><spring:theme code="basket.page.title.pickupFrom" text="Pick Up from\:" /></h4>
							<ul>
								<li>${pos.name}</li>
								<li>${pos.address.line1}</li>
								<li>${pos.address.line2}</li>
								<li>${pos.address.town}</li>
							</ul>
							<order:orderDetailsItem order="${orderData}" orderGroup="${orderGroup}" />
						</c:forEach>
					</div>
					</c:if>

					<div data-theme="b">
						<order:receivedPromotions order="${orderData}" />
					</div>
					<div data-theme="b">
						<order:orderTotalsItem order="${orderData}" />
					</div>
				</div>
			</div>

			<div data-theme="b">
				<sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
					<user:guestRegister actionNameKey="guest.register.submit"/>
				</sec:authorize>
			</div>

			<ul class="mFormList" data-theme="c" data-content-theme="c">
				<li>
					<div class="ui-grid-a doubleButton">
						<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
							<c:set var="buttonStyling" value="class='ui-block-a' style='width: 38%;'"/>
						</sec:authorize>
						<div ${buttonStyling}>
								<c:url value="/" var="homeUrl"/>
								<a href="${homeUrl}" data-role="button" data-theme="d" data-icon="arrow-l" data-iconpos="left" >
									<spring:theme code="cart.page.shop"/>
								</a>
						</div>
						<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
						<div class="ui-block-b" style="width: 58%;">
								<c:url value="/my-account/orders" var="accountOrdersUrl"/>
								<a href="${accountOrdersUrl}" data-role="button" data-theme="d" data-icon="arrow-r" data-iconpos="right" >
									<spring:theme code="text.account.orderHistory" text="Order History"/>
								</a>
						</div>
						</sec:authorize>
					</div>
				</li>
			</ul>
			<cms:pageSlot position="BottomContent" var="feature" element="div" id="bottom-disp-img" class="home-disp-img">
				<cms:component component="${feature}" element="div" class="span-24 cms_disp-img_slot"/>
			</cms:pageSlot>
		</div>
	</jsp:body>
</template:page>
