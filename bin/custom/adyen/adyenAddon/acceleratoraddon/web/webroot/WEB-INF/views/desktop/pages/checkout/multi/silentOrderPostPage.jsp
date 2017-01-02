<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/desktop/checkout/multi" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/desktop/address" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:url value="${currentStepUrl}" var="choosePaymentMethodUrl"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

	<div id="globalMessages">
		<common:globalMessages/>
	</div>

	<multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}"/>

	<c:if test="${not empty paymentFormUrl}">
		<div class="span-14 append-1">
			<div id="checkoutContentPanel" class="clearfix">
				<ycommerce:testId code="paymentDetailsForm">

				<form:form id="silentOrderPostForm" name="silentOrderPostForm" commandName="sopPaymentDetailsForm" class="create_update_payment_form" action="${paymentFormUrl}" method="POST">
					<input type="hidden" name="orderPage_receiptResponseURL" value="${silentOrderPageData.parameters['orderPage_receiptResponseURL']}"/>
					<input type="hidden" name="orderPage_declineResponseURL" value="${silentOrderPageData.parameters['orderPage_declineResponseURL']}"/>
					<input type="hidden" name="orderPage_cancelResponseURL" value="${silentOrderPageData.parameters['orderPage_cancelResponseURL']}"/>
					<c:forEach items="${sopPaymentDetailsForm.signatureParams}" var="entry" varStatus="status">
						<input type="hidden" id="${entry.key}" name="${entry.key}" value="${entry.value}"/>
					</c:forEach>
					<c:forEach items="${sopPaymentDetailsForm.subscriptionSignatureParams}" var="entry" varStatus="status">
						<input type="hidden" id="${entry.key}" name="${entry.key}" value="${entry.value}"/>
					</c:forEach>

					<div class="headline"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.paymentCard"/></div>
					<div class="required right"><spring:theme code="form.required"/></div>
					<div class="description"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.enterYourCardDetails"/></div>

					<c:if test="${not empty paymentInfos}">
						<button type="button" class="positive clear view-saved-payments" id="viewSavedPayments">
							<spring:theme code="checkout.multi.paymentMethod.viewSavedPayments" text="View Saved Payments"/>
						</button>
					</c:if>

					<div class="cardForm">
						<formElement:formSelectBox idKey="card_cardType" labelKey="payment.cardType" path="card_cardType" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.cardType.pleaseSelect" items="${sopCardTypes}" tabindex="1"/>
						<formElement:formInputBox idKey="card_nameOnCard" labelKey="payment.nameOnCard" path="card_nameOnCard" inputCSS="text" tabindex="2" mandatory="false"/>
						<formElement:formInputBox idKey="card_accountNumber" labelKey="payment.cardNumber" path="card_accountNumber" inputCSS="text" mandatory="true" tabindex="3" autocomplete="off"/>
						<formElement:formInputBox idKey="card_cvNumber" labelKey="payment.cvn" path="card_cvNumber" inputCSS="text" mandatory="true" tabindex="4"/>
						<fieldset id="startDate" class="cardDate" style="display:none;">
							<legend><spring:theme code="payment.startDate"/></legend>
							<formElement:formSelectBox idKey="StartMonth" labelKey="payment.month" path="card_startMonth" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.month" items="${months}" tabindex="5"/>
							<formElement:formSelectBox idKey="StartYear" labelKey="payment.year" path="card_startYear" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.year" items="${startYears}" tabindex="6"/>
						</fieldset>
						<fieldset class="cardDate">
							<legend><spring:theme code="payment.expiryDate"/></legend>
							<formElement:formSelectBox idKey="ExpiryMonth" labelKey="payment.month" path="card_expirationMonth" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.month" items="${months}" tabindex="7"/>
							<formElement:formSelectBox idKey="ExpiryYear" labelKey="payment.year" path="card_expirationYear" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.year" items="${expiryYears}" tabindex="8"/>
						</fieldset>
						<div id="issueNum" style="display:none;">
							<formElement:formInputBox idKey="card_issueNumber" labelKey="payment.issueNumber" path="card_issueNumber" inputCSS="text" mandatory="false" tabindex="9"/>
						</div>
					</div>


					<div class="form-additionals">
						<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
							<formElement:formCheckbox idKey="savePaymentInfo1" labelKey="checkout.multi.sop.savePaymentInfo" path="savePaymentInfo"
							                          inputCSS="" labelCSS="" mandatory="false"/>
						</sec:authorize>
					</div>


					<div class="headline clear"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.billingAddress"/></div>


					<c:if test="${cartData.deliveryItemsQuantity > 0}">
						<form:checkbox path="useDeliveryAddress" id="useDeliveryAddress"
						               data-firstname="${deliveryAddress.firstName}"
						               data-lastname="${deliveryAddress.lastName}"
						               data-line1="${deliveryAddress.line1}"
						               data-line2="${deliveryAddress.line2}"
						               data-town="${deliveryAddress.town}"
						               data-postalcode="${deliveryAddress.postalCode}"
						               data-countryisocode="${deliveryAddress.country.isocode}"
						               data-regionisocode="${deliveryAddress.region.isocodeShort}"
						               data-address-id="${deliveryAddress.id}"
						               tabindex="11"/>
						<spring:theme code="checkout.multi.sop.useMyDeliveryAddress"/>
					</c:if>
					<input type="hidden" value="${silentOrderPageData.parameters['billTo_email']}" class="text" name="billTo_email" id="billTo_email">
					<address:billAddressFormSelector supportedCountries="${countries}" regions="${regions}" tabindex="12"/>
					<div class="form-additionals">
					</div>


					<div class="form-actions">
						<c:url value="/checkout/multi/delivery-method/choose" var="chooseDeliveryMethodUrl"/>
						<a class="button" href="${chooseDeliveryMethodUrl}"><spring:theme code="checkout.multi.cancel" text="Cancel"/></a>
						<button class="positive right submit_silentOrderPostForm" tabindex="20">
							<spring:theme code="checkout.multi.paymentMethod.continue" text="Continue"/>
						</button>
					</div>
				</form:form>
			</div>
			</ycommerce:testId>


			<c:if test="${not empty paymentInfos}">
				<div id="savedPaymentListHolder">
					<div id="savedPaymentList" class="summaryOverlay clearfix">
						<div class="headline"><spring:theme code="checkout.summary.paymentMethod.savedCards.header"/></div>
						<div class="description"><spring:theme code="checkout.summary.paymentMethod.savedCards.selectSavedCardOrEnterNew"/></div>

						<div class="paymentList">
							<c:forEach items="${paymentInfos}" var="paymentInfo" varStatus="status">
								<div class="paymentEntry">
									<form action="${request.contextPath}/checkout/multi/payment-method/choose" method="GET">
										<input type="hidden" name="selectedPaymentMethodId" value="${paymentInfo.id}"/>
										<ul>
											<li>${fn:escapeXml(paymentInfo.cardType)}</li>
											<li>${fn:escapeXml(paymentInfo.cardNumber)}</li>
											<li><spring:theme code="checkout.multi.paymentMethod.paymentDetails.expires" arguments="${fn:escapeXml(paymentInfo.expiryMonth)},${fn:escapeXml(paymentInfo.expiryYear)}"/></li>
											<li>${fn:escapeXml(paymentInfo.billingAddress.firstName)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.lastName)}</li>
											<li>${fn:escapeXml(paymentInfo.billingAddress.line1)}</li>
											<li>${fn:escapeXml(paymentInfo.billingAddress.region.isocodeShort)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.town)}</li>
											<li>${fn:escapeXml(paymentInfo.billingAddress.postalCode)}</li>
										</ul>
										<button type="submit" class="positive right" tabindex="${status.count + 21}">
											<spring:theme code="checkout.multi.sop.useThisPaymentInfo" text="Use this Payment Info"/>
										</button>
									</form>
									<form:form action="${request.contextPath}/checkout/multi/payment-method/remove" method="POST">
										<input type="hidden" name="paymentInfoId" value="${paymentInfo.id}"/>
										<button type="submit" class="negative remove-payment-item right" tabindex="${status.count + 22}">
											<spring:theme code="checkout.multi.sop.remove" text="Remove"/>
										</button>
									</form:form>
								</div>
							</c:forEach>
						</div>
					</div>
				</div>
			</c:if>
		</div>
		<multi-checkout:checkoutOrderDetails cartData="${cartData}" showShipDeliveryEntries="true" showPickupDeliveryEntries="true" showTax="true"/>
	</c:if>


	<cms:pageSlot position="SideContent" var="feature" element="div" class="span-24 side-content-slot cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

</template:page>
