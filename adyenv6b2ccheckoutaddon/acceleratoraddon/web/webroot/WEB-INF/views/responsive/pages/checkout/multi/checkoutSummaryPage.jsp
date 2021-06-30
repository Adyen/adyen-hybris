<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="adyen" tagdir="/WEB-INF/tags/addons/adyenv6b2ccheckoutaddon/responsive" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>

<spring:url value="/checkout/multi/adyen/summary/component-result" var="handleComponentResult"/>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
<jsp:attribute name="pageScripts">
    <script type="text/javascript" src="${dfUrl}"></script>
    <script src="https://${checkoutShopperHost}/checkoutshopper/sdk/4.3.1/adyen.js"
            integrity="sha384-eNk32fgfYxvzNLyV19j4SLSHPQdLNR+iUS1t/D7rO4gwvbHrj6y77oJLZI7ikzBH"
            crossorigin="anonymous"></script>
    <link rel="stylesheet" href="https://${checkoutShopperHost}/checkoutshopper/css/chckt-default-v1.css"/>
    <link rel="stylesheet"
          href="https://${checkoutShopperHost}/checkoutshopper/sdk/4.3.1/adyen.css"
          integrity="sha384-5CDvDZiVPuf+3ZID0lh0aaUHAeky3/ACF1YAKzPbn3GEmzWgO53gP6stiYHWIdpB"
          crossorigin="anonymous"/>

    <script type="text/javascript">
        AdyenCheckoutHybris.initiateCheckout("${shopperLocale}", "${environmentMode}", "${clientKey}");

        <c:choose>
            <%-- Configure components --%>
            <c:when test="${selectedPaymentMethod eq 'paypal' && (not empty paypalMerchantId || environmentMode eq 'test')}">
                var amountJS = {value: "${amount.value}", currency: "${amount.currency}"};
                AdyenCheckoutHybris.initiatePaypal(amountJS, "${immediateCapture}", "${paypalMerchantId}", "hidden-xs");
                AdyenCheckoutHybris.initiatePaypal(amountJS, "${immediateCapture}", "${paypalMerchantId}", "visible-xs");
            </c:when>

            <c:when test="${selectedPaymentMethod eq 'mbway'}">
                AdyenCheckoutHybris.initiateMbway("hidden-xs");
                AdyenCheckoutHybris.initiateMbway("visible-xs");
            </c:when>

            <c:when test="${selectedPaymentMethod eq 'applepay'}">
                var amountJS = {value: "${amount.value}", currency: "${amount.currency}"};
                AdyenCheckoutHybris.initiateApplePay(amountJS, "${countryCode}", "${applePayMerchantIdentifier}", "${applePayMerchantName}", "hidden-xs");
                AdyenCheckoutHybris.initiateApplePay(amountJS, "${countryCode}", "${applePayMerchantIdentifier}", "${applePayMerchantName}", "visible-xs");
            </c:when>

            <c:when test="${selectedPaymentMethod eq 'pix'}">
                AdyenCheckoutHybris.initiatePix("hidden-xs");
                AdyenCheckoutHybris.initiatePix("visible-xs");
            </c:when>
        
            <c:when test="${selectedPaymentMethod eq 'paywithgoogle'}">
                var amountJS = {value: "${amount.value}", currency: "${amount.currency}"};
                AdyenCheckoutHybris.initiateGooglePay(amountJS, "${merchantAccount}", "hidden-xs");
                AdyenCheckoutHybris.initiateGooglePay(amountJS, "${merchantAccount}", "visible-xs");
            </c:when>
        
            <c:when test="${selectedPaymentMethod eq 'amazonpay'}">
                var amountJS = {value: "${amount.value}", currency: "${amount.currency}"};
                AdyenCheckoutHybris.initiateAmazonPay(amountJS, ${deliveryAddress}, ${amazonPayConfiguration});
            </c:when>

            <%-- API only payments methods --%>
            <c:otherwise>
                AdyenCheckoutHybris.configureButton($( "#placeOrderForm-hidden-xs" ), true, "hidden-xs");
                AdyenCheckoutHybris.configureButton($( "#placeOrderForm-visible-xs" ), true, "visible-xs");
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
                    <div id="adyen-checkout-hidden-xs" class="place-order-form hidden-xs">
                        <adyen:checkoutOrderSummary paymentMethod="${selectedPaymentMethod}" label="hidden-xs"/>
                    </div>
                </ycommerce:testId>

                <form:form id="handleComponentResultForm"
                           class="create_update_payment_form"
                           action="${handleComponentResult}"
                           method="post">
                    <input type="hidden" id="resultData" name="resultData"/>
                    <input type="hidden" id="isResultError" name="isResultError" value="false"/>
                </form:form>
            </multi-checkout:checkoutSteps>
        </div>

        <div class="col-sm-6">
            <div class="checkout-summary-headline hidden-xs">
                <spring:theme code="checkout.multi.order.summary" />
            </div>
            <div class="checkout-order-summary">
                <ycommerce:testId code="orderSummary">
                    <multi-checkout:deliveryCartItems cartData="${cartData}" showDeliveryAddress="true" />

                    <c:forEach items="${cartData.pickupOrderGroups}" var="groupData" varStatus="status">
                        <multi-checkout:pickupCartItems cartData="${cartData}" groupData="${groupData}" showHead="true" />
                    </c:forEach>

                    <order:appliedVouchers order="${cartData}" />

                    <multi-checkout:paymentInfo cartData="${cartData}" paymentInfo="${cartData.paymentInfo}" showPaymentInfo="true" />


                    <multi-checkout:orderTotals cartData="${cartData}" showTaxEstimate="true" showTax="true" />
                </ycommerce:testId>
            </div>

            <div id="adyen-checkout-visible-xs" class="visible-xs clearfix">
                <adyen:checkoutOrderSummary paymentMethod="${selectedPaymentMethod}" label="visible-xs"/>
            </div>
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
