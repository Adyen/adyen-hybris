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
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/mobile/checkout/multi" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/mobile/address" %>


<c:url value="/checkout/multi/add-payment-method" var="choosePaymentMethodUrl"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

	<div id="globalMessages">
		<common:globalMessages/>
	</div>

	<multi-checkout:checkoutProgressBar steps="${checkoutSteps}" currentStep="3" stepName="paymentMethod"/>

	<c:if test="${not empty paymentFormUrl}">
		<div class="span-20 last multicheckout silent-order-post-page">
			<div class="item_container_holder">
				<div class="title_holder">
					<div class="title">
						<div class="title-top"><span></span></div>
					</div>
					<h2><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.header" text="Payment Details"/></h2>
				</div>
				<div class="item_container">
					
					<c:if test="${not empty paymentInfos}">
					<div class="payment_details_right_col saved-payment-list">
						<h2>Choose from existing Payment Details</h2>
						<c:forEach items="${paymentInfos}" var="paymentInfo">
							<div class="saved-payment-list-entry">
								<form action="${request.contextPath}/checkout/multi/select-payment-method" method="GET">
									<input type="hidden" name="selectedPaymentMethodId" value="${paymentInfo.id}" />
									<span class="saved-payment-list-item">${fn:escapeXml(paymentInfo.cardType)}</span>
									<span class="saved-payment-list-item">${fn:escapeXml(paymentInfo.cardNumber)}</span>
									<span class="saved-payment-list-item"><spring:theme code="checkout.multi.paymentMethod.paymentDetails.expires" arguments="${fn:escapeXml(paymentInfo.expiryMonth)},${fn:escapeXml(paymentInfo.expiryYear)}"/></span>
									<span class="saved-payment-list-item">${fn:escapeXml(paymentInfo.billingAddress.firstName)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.lastName)}</span>
									<span class="saved-payment-list-item">${fn:escapeXml(paymentInfo.billingAddress.line1)}</span>
									<span class="saved-payment-list-item">${fn:escapeXml(paymentInfo.billingAddress.postalCode)}&nbsp; ${fn:escapeXml(paymentInfo.billingAddress.town)}</span>
									<button type="submit" class="form" data-theme="c">Use this Payment Details</button>
								</form>
								<form:form action="${request.contextPath}/checkout/multi/remove-payment-method" method="POST" class="remove-payment-item-form">
									<input type="hidden" name="paymentInfoId" value="${paymentInfo.id}"/>
									<button type="submit" class="text-button remove-payment-item">Remove</button>
								</form:form>
							</div>
						</c:forEach>
					</div>
					</c:if>
					
					<div class="payment_details_left_col">
						<form:form id="silentOrderPostForm" name="silentOrderPostForm" commandName="sopPaymentDetailsForm" class="create_update_payment_form" action="${paymentFormUrl}" method="POST">
							<input type="hidden" name="orderPage_receiptResponseURL" value="${silentOrderPageData.parameters['orderPage_receiptResponseURL']}"/>
							<input type="hidden" name="orderPage_declineResponseURL" value="${silentOrderPageData.parameters['orderPage_declineResponseURL']}"/>
							<input type="hidden" name="orderPage_cancelResponseURL" value="${silentOrderPageData.parameters['orderPage_cancelResponseURL']}"/>
							<c:forEach items="${sopPaymentDetailsForm.signatureParams}" var="entry" varStatus="status">
								<input type="hidden" id="${entry.key}" name="${entry.key}" value="${entry.value}" />
							</c:forEach>
							<c:forEach items="${sopPaymentDetailsForm.subscriptionSignatureParams}" var="entry" varStatus="status">
								<input type="hidden" id="${entry.key}" name="${entry.key}" value="${entry.value}" />
							</c:forEach>

							<div class="payment_details_left_col-card">
								<h2>Enter new Payment Details</h2>
								<div class="infotext">
									<p><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.enterYourCardDetails"/></p>
									<p><spring:theme code="form.required"/></p>
								</div>
								<div class="form_field-elements">
									<formElement:formSelectBox idKey="card_cardType" labelKey="payment.cardType" path="card_cardType" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.cardType.pleaseSelect" items="${sopCardTypes}" tabindex="1"/>
									<formElement:formInputBox idKey="card_nameOnCard" labelKey="payment.nameOnCard" path="card_nameOnCard" inputCSS="text" tabindex="2" mandatory="false" />
									<formElement:formInputBox idKey="card_accountNumber" labelKey="payment.cardNumber" path="card_accountNumber" inputCSS="text" mandatory="true" tabindex="2" autocomplete="off"/>
									<formElement:formInputBox idKey="card_cvNumber" labelKey="payment.cvn" path="card_cvNumber" inputCSS="text" mandatory="true" tabindex="3"/>
									<template:errorSpanField path="card_startMonth">
										<div id="startDate">
											<dt><label for="card_startMonth"><spring:theme code="payment.startDate"/></label></dt>
											<dd>
												<form:select id="card_startMonth" path="card_startMonth" cssClass="card_date" tabindex="4">
													<option value="" label="<spring:theme code='payment.month'/>"/>
													<form:options items="${months}" itemValue="code" itemLabel="name"/>
												</form:select>

												<form:select id="card_startYear" path="card_startYear" cssClass="card_date" tabindex="5">
													<option value="" label="<spring:theme code='payment.year'/>"/>
													<form:options items="${startYears}" itemValue="code" itemLabel="name"/>
												</form:select>
											</dd>
										</div>
										<div class="form_field-label form_field-label-headline">
											<label for="card_expirationMonth"><spring:theme code="payment.expiryDate"/></label>
										</div>
										<div class="form_field-input">
											<template:errorSpanField path="card_expirationMonth">
												<formElement:formSelectBox idKey="card_expirationMonth" labelKey="payment.month" path="card_expirationMonth" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.expiryMonth.invalid" items="${months}" tabindex="6" itemValue="code"/>
											</template:errorSpanField>
											<template:errorSpanField path="card_expirationYear">
												<formElement:formSelectBox idKey="card_expirationYear" labelKey="payment.year" path="card_expirationYear" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.expiryYear.invalid" items="${expiryYears}" tabindex="7" itemValue="code"/>
											</template:errorSpanField>
										</template:errorSpanField>
									</div>

									<div id="issueNum">
										<formElement:formInputBox idKey="card_issueNumber" labelKey="payment.issueNumber" path="card_issueNumber" inputCSS="text" mandatory="false" tabindex="8"/>
									</div>
									<div class="form_field-input">
										<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
											<formElement:formCheckbox idKey="checkout.multi.sop.savePaymentInfo" labelKey="checkout.multi.sop.savePaymentInfo" path="savePaymentInfo" labelCSS="add-address-left-label" mandatory="false" tabindex="9"/>
										</sec:authorize>
									</div>
							</div>
						</div>

						<div class="payment_details_left_col-billing">
							<div class="title_holder">
								<div class="title">
									<div class="title-top"><span></span></div>
								</div>
								<h2><spring:theme code="text.billingAddress" text="Billing Address"/></h2>
							</div>
							<c:if test="${cartData.deliveryItemsQuantity > 0}">
								<formElement:formCheckbox idKey="useDeliveryAddress" labelKey="checkout.multi.sop.useMyDeliveryAddress" path="useDeliveryAddress" labelCSS="add-address-left-label" mandatory="false" tabindex="10"/>
								<input type="hidden" id="useDeliveryAddressFields"
								                    name="orderPage_receiptResponseURL"
								                    data-firstname="${deliveryAddress.firstName}"
								                    data-lastname="${deliveryAddress.lastName}"
													data-line1="${deliveryAddress.line1}"
													data-line2="${deliveryAddress.line2}"
													data-town="${deliveryAddress.town}"
													data-postalcode="${deliveryAddress.postalCode}"
													data-countryisocode="${deliveryAddress.country.isocode}"
													data-regionisocode="${deliveryAddress.region.isocodeShort}"
													data-address-id="${deliveryAddress.id}"/>
							</c:if>
							<input type="hidden" value="${silentOrderPageData.parameters['billTo_email']}" class="text" name="billTo_email" id="billTo_email">
							<address:billAddressFormSelector supportedCountries="${countries}" regions="${regions}" tabindex="11"/>
						</div>


						<div class="save_payment_details">
							<span class="clear_fix">
								<button data-theme="c" class="form submit_silentOrderPostForm" tabindex="19"><spring:theme code="Submit" text="Submit"/></button>
							</span>
						</div>
						</form:form>
					</div>

				</div>
			</div>
		</div>
	</c:if>
</template:page>
