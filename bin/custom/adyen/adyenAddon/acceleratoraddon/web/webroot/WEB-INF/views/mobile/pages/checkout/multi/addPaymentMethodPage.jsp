<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/mobile/cart" %>
<%@ taglib prefix="checkout-cart" tagdir="/WEB-INF/tags/addons/adyenAddon/mobile/checkout" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/mobile/checkout/multi"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	<jsp:body>
		<common:globalMessages/>
		<multi-checkout:checkoutProgressBar steps="${checkoutSteps}" currentStep="3" stepName="paymentMethod" />
		<div class="item_container_holder">
			<form:form method="post" commandName="paymentDetailsForm" class="create_update_payment_form">
				<common:errors />
				<div data-theme="c">
					<h3>
						<spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.paymentCard" />
					</h3>
					<ul class="mFormList" data-theme="c" data-content-theme="c">
						<li>
							<p><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.enterYourCardDetails" /></p>
							<p><spring:theme code="form.required" /></p>
						</li>
						<li>
							<form:hidden path="paymentId" class="create_update_payment_id" />
							<formElement:formSelectBox idKey="payment.cardType" labelKey="payment.cardType" path="cardTypeCode" mandatory="true" 
								skipBlank="false" skipBlankMessageKey="payment.cardType.pleaseSelect" items="${cardTypes}" tabindex="1" />
						</li>
						<li><formElement:formInputBox idKey="payment.nameOnCard" labelKey="payment.nameOnCard" path="nameOnCard" inputCSS="text" mandatory="true" tabindex="2" /></li>
						<li><formElement:formInputBox idKey="payment.cardNumber" labelKey="payment.cardNumber" path="cardNumber" inputCSS="text" mandatory="true" tabindex="3" /></li>
						<li>
							<label for="StartMonth"><spring:theme code="payment.startDate" /></label>
							<div class="ui-grid-a" data-theme="b">
								<div class="ui-block-a">
									<formElement:formSelectBox idKey="payment.month" labelKey="payment.month" path="startMonth" mandatory="true" skipBlank="false"
										skipBlankMessageKey="payment.month" items="${months}" tabindex="4" selectCSSClass="card_date" />
								</div>
								<div class="ui-grid-b">
									<formElement:formSelectBox idKey="payment.year" labelKey="payment.year" path="startYear" mandatory="true" skipBlank="false"
										skipBlankMessageKey="payment.year" items="${startYears}" tabindex="5" selectCSSClass="card_date" />
								</div>
							</div>
						</li>
						<li>
							<label for="ExpiryMonth"><spring:theme code="payment.expiryDate" /></label>
							<div class="ui-grid-a" data-theme="b">
								<div class="ui-block-a">
									<formElement:formSelectBox idKey="payment.month" labelKey="payment.month" path="expiryMonth" mandatory="true" skipBlank="false"
										skipBlankMessageKey="payment.month" items="${months}" tabindex="6" />
								</div>
								<div class="ui-block-b">
									<formElement:formSelectBox idKey="payment.year" labelKey="payment.year" path="expiryYear" mandatory="true" skipBlank="false"
										skipBlankMessageKey="payment.year" items="${expiryYears}" tabindex="7" />
								</div>
							</div>
						</li>
						<li>
							<div class="ui-grid-a" data-theme="b">
								<div class="ui-block-a" style="width: 50%">
									<formElement:formInputBox idKey="payment.issueNumber" labelKey="mobile.payment.issueNumber" path="issueNumber" inputCSS="text" mandatory="false" tabindex="4" />
								</div>
							</div>
						</li>
						<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
							<li>
								<form:checkbox id="SaveDetails" path="saveInAccount" tabindex="19" data-theme="d" />
								<label for="SaveDetails"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.savePaymentDetailsInAccount" /></label>
							</li>
						</sec:authorize>
						<li><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.billingAddressDiffersFromDeliveryAddress" /></li>
						<li>
							<form:checkbox id="differentAddress" path="newBillingAddress" tabindex="9" data-theme="d" />
							<label for="differentAddress"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.enterDifferentBillingAddress" /></label>
						</li>
					</ul>
				</div>
				<div data-role="collapsible" data-collapsed="true" data-content-theme="c" data-theme="d" id="addBillingAddressForm" style="display: none">
					<h3>
						<spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.billingAddress" />
					</h3>
					<spring:theme code="form.required" />
					<ul class="mFormList" data-theme="c" data-content-theme="c">
						<form:hidden path="billingAddress.addressId" class="create_update_address_id" />
						<li>
							<formElement:formSelectBox idKey="address.title" labelKey="address.title" path="billingAddress.titleCode" mandatory="true"
								skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect" items="${titles}" tabindex="10" />
						</li>
						<li><formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="billingAddress.firstName" inputCSS="text" mandatory="true" tabindex="11" /></li>
						<li><formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="billingAddress.lastName" inputCSS="text" mandatory="true" tabindex="12" /></li>
						<li><formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="billingAddress.line1" inputCSS="text" mandatory="true" tabindex="14" /></li>
						<li><formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="billingAddress.line2" inputCSS="text" mandatory="false" tabindex="15" /></li>
						<li><formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="billingAddress.townCity" inputCSS="text" mandatory="true" tabindex="16" /></li>
						<li><formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="billingAddress.postcode" inputCSS="text" mandatory="true" tabindex="17" /></li>
						<li>
							<formElement:formSelectBox idKey="address.country" labelKey="address.country" path="billingAddress.countryIso" mandatory="true" skipBlank="false"
								skipBlankMessageKey="address.selectCountry" items="${countries}" itemValue="isocode" tabindex="18" />
						</li>
						<form:hidden path="billingAddress.shippingAddress" />
						<form:hidden path="billingAddress.billingAddress" />
					</ul>
				</div>
				<ycommerce:testId code="checkout_useThesePaymentDetails_button">
					<div class="ui-grid-a doubleButton">
						<div class="ui-block-a">
							<a data-rel="back" class="form" type="button" data-theme="d" data-icon="delete">
								<spring:theme code="checkout.multi.cancel" text="Cancel" />
							</a>
						</div>
						<div class="ui-block-b">
							<button class="form left use_these_payment_details_button" tabindex="20" id="lastInTheForm" data-theme="c" data-icon="check">
								<spring:theme code="text.button.use" />
							</button>
						</div>
					</div>
				</ycommerce:testId>
			</form:form>
		</div>
		<div class="checkoutOverviewItems">
			<div><checkout-cart:summaryCartItems cartData="${cartData}"/></div>
			<div><checkout-cart:cartItemGroupsForPickUp cartData="${cartData}"/></div>
			<div><cart:cartPromotions cartData="${cartData}"/></div>
			<div><cart:cartTotals cartData="${cartData}" showTaxEstimate="false" showTax="true"/></div>
		</div>
	</jsp:body>
</template:page>
