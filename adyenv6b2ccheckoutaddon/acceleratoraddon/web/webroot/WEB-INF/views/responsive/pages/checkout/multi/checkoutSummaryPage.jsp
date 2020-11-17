<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="adyen" tagdir="/WEB-INF/tags/addons/adyenv6b2ccheckoutaddon/responsive" %>

<spring:url value="/checkout/multi/adyen/summary/placeOrder" var="placeOrderUrl"/>
<spring:url value="/checkout/multi/termsAndConditions" var="getTermsAndConditionsUrl"/>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
<jsp:attribute name="pageScripts">
    <script type="text/javascript" src="${dfUrl}"></script>
    <script type="text/javascript" src="https://${checkoutShopperHost}/checkoutshopper/sdk/3.10.0/adyen.js"></script>
    <link rel="stylesheet" href="https://checkoutshopper-live.adyen.com/checkoutshopper/css/chckt-default-v1.css"/>
    <link rel="stylesheet" href="https://${checkoutShopperHost}/checkoutshopper/sdk/3.10.0/adyen.css"/>

    <script type="text/javascript">
        AdyenCheckoutHybris.initiateCheckout("${shopperLocale}", "${environmentMode}", "${originKey}");

        <c:choose>
            <%-- Configure components --%>
            <c:when test="${selectedPaymentMethod eq 'paypal' && (not empty paypalMerchantId || environmentMode eq 'test')}">
                var amountJS = {value: "${amount.value}", currency: "${amount.currency}"};
                AdyenCheckoutHybris.initiatePaypal(amountJS, "${immediateCapture}", "${paypalMerchantId}");
            </c:when>

            <c:when test="${selectedPaymentMethod eq 'mbway'}">
                AdyenCheckoutHybris.initiateMbway();
            </c:when>

            <%-- API only payments methods --%>
            <c:otherwise>
                AdyenCheckoutHybris.configureButton($( "#placeOrderForm1" ), true);
            </c:otherwise>
        </c:choose>

    </script>
</jsp:attribute>

<jsp:body>
    <div id="spinner_wrapper" style="display: none">
        <div id="spinner"></div>
        <div id="spinner_text">
            <p>
                <spring:theme code="checkout.summary.spinner.message"/>
            </p>
        </div>
    </div>

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
                        <%-- Components --%>
                        <c:if test="${selectedPaymentMethod eq 'mbway' || selectedPaymentMethod eq 'paypal'}">
                            <adyen:componentPayment
                                    paymentMethod="${selectedPaymentMethod}"/>
                        </c:if>

                        <%-- Paypal has it's own button --%>
                        <c:if test="${selectedPaymentMethod ne 'paypal'}">
                            <form:form action="${placeOrderUrl}" id="placeOrderForm1" modelAttribute="placeOrderForm">
                                <div class="checkbox">
                                    <label> <form:checkbox id="terms-conditions-check" path="termsCheck" />
                                        <spring:theme code="checkout.summary.placeOrder.readTermsAndConditions" arguments="${getTermsAndConditionsUrl}" text="Terms and Conditions"/>
                                    </label>
                                </div>
                            </form:form>

                            <button id="placeOrder" type="submit" class="btn btn-primary btn-place-order btn-block">
                                <spring:theme code="checkout.summary.placeOrder" text="Place Order" />
                            </button>
                        </c:if>

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
</jsp:body>
</template:page>
