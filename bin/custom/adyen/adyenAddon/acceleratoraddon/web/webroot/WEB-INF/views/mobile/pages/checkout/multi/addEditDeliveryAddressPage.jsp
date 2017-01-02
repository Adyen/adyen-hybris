<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/mobile/address"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/mobile/checkout/multi" %>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	<jsp:body>
		<div id="globalMessages" data-theme="b">
			<common:globalMessages/>
		</div>
		<multi-checkout:checkoutProgressBar steps="${checkoutSteps}" currentStep="1" stepName="deliveryAddress"/>
		<div data-theme="e" class="item_container_holder">
			<div class="item_container">
				<div class="payment_details_left_col">
					<h3>
						<spring:theme code="checkout.multi.addressDetails" text="Address Details"/>
					</h3>

					<p></p>

					<p class="continuous-text">
						<spring:theme code="checkout.multi.addEditform" text="Please use this form to add/edit an address."/>
					</p>
					<p class="continuous-text">
						<spring:theme code="form.required" text="Fields marked * are required"/>
					</p>

					<address:addressFormSelector supportedCountries="${countries}"
												 regions="${regions}"
												 cancelUrl="/checkout/multi/add-delivery-address"
												 country="${country}"/>
					<address:suggestedAddresses selectedAddressUrl="/checkout/multi/select-suggested-address"/>
				</div>

				<c:if test="${not empty deliveryAddresses}">
					<div class="payment_details_right_col saved-payment-list">
						<c:forEach items="${deliveryAddresses}" var="deliveryAddress" varStatus="status">
							<div class="saved-payment-list-entry">
								<form action="${request.contextPath}/checkout/multi/select-delivery-address" method="GET">
									<input type="hidden" name="selectedAddressCode" value="${deliveryAddress.id}"/>

									<span class="saved-payment-list-item">${fn:escapeXml(deliveryAddress.title)}&nbsp; ${fn:escapeXml(deliveryAddress.firstName)}&nbsp; ${fn:escapeXml(deliveryAddress.lastName)}</span>
									<span class="saved-payment-list-item">${fn:escapeXml(deliveryAddress.line1)}</span>
									<span class="saved-payment-list-item">${fn:escapeXml(deliveryAddress.line2)}</span>
									<c:choose>
										<c:when test="${not empty deliveryAddress.region.name}">
											<span class="saved-payment-list-item">${fn:escapeXml(deliveryAddress.town)}&nbsp; ${fn:escapeXml(deliveryAddress.postalCode)}</span>
											<span class="saved-payment-list-item">${fn:escapeXml(deliveryAddress.country.name)}&nbsp; ${fn:escapeXml(deliveryAddress.region.name)}</span>
										</c:when>
										<c:otherwise>
											<span class="saved-payment-list-item">${fn:escapeXml(deliveryAddress.town)}&nbsp; ${fn:escapeXml(deliveryAddress.postalCode)}</span>
											<span class="saved-payment-list-item">${fn:escapeXml(deliveryAddress.country.name)}</span>
										</c:otherwise>
									</c:choose>
									<button type="submit" class="form" data-theme="c" tabindex="${status.count + 21}">Use this Delivery Address</button>
								</form>
								<form:form action="${request.contextPath}/checkout/multi/remove-address" method="POST">
									<input type="hidden" name="addressCode" value="${deliveryAddress.id}"/>
									<button type="submit" class="text-button remove-payment-item" tabindex="${status.count + 22}">Remove</button>
								</form:form>
							</div>
						</c:forEach>
					</div>
				</c:if>
			</div>
		</div>
	</jsp:body>
</template:page>
