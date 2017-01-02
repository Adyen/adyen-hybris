<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:url value="/checkout/multi/adyen/summary/placeOrder" var="placeOrderUrl"/>
<spring:url value="/checkout/multi/termsAndConditions" var="getTermsAndConditionsUrl"/>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

<div class="row">
    <div class="col-sm-6">
    	<div class="checkout-headline">
    		<span class="glyphicon glyphicon-lock"></span>
            <spring:theme code="checkout.multi.secure.checkout" />
        </div>
		<multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
			<ycommerce:testId code="checkoutStepFour">
				<div class="checkout-review hidden-xs">
                    <div class="checkout-order-summary">
                        <multi-checkout:orderTotals cartData="${cartData}" showTaxEstimate="${showTaxEstimate}" showTax="${showTax}" subtotalsCssClasses="dark"/>
                    </div>
                </div>
                <div class="place-order-form hidden-xs">
                    <form:form action="${placeOrderUrl}" id="placeOrderForm1" commandName="placeOrderForm">
                        <div class="checkbox">
                            <label> <form:checkbox id="Terms1" path="termsCheck" />
                                <spring:theme code="checkout.summary.placeOrder.readTermsAndConditions" arguments="${getTermsAndConditionsUrl}" text="Terms and Conditions"/>
                            </label>
                        </div>

                        <button id="placeOrder" type="submit" class="btn btn-primary btn-place-order btn-block">
                            <spring:theme code="checkout.summary.placeOrder" text="Place Order"/>
                        </button>
                    </form:form>
                </div>
			</ycommerce:testId>
		</multi-checkout:checkoutSteps>
    </div>

    <div class="col-sm-6">
		<multi-checkout:checkoutOrderSummary cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="true" showTaxEstimate="true" showTax="true" />
	</div>

    <div class="col-sm-12 col-lg-12">
        <br class="hidden-lg">
        <cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
            <cms:component component="${feature}"/>
        </cms:pageSlot>
    </div>
</div>
</template:page>