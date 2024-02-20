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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<spring:url value="/checkout/multi/adyen/summary/component-result" var="handleComponentResult"/>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
<jsp:attribute name="pageScripts">
    <adyen:adyenLibrary
            dfUrl="${dfUrl}"
            showDefaultCss="${true}"
    />

    <script type="text/javascript">
        <c:set var="initConfig">
        <json:object escapeXml="false">
        <json:property name="shopperLocale" value="${shopperLocale}"/>
        <json:property name="environment" value="${environmentMode}"/>
        <json:property name="clientKey" value="${clientKey}"/>
        <json:property name="sessionId" value="${sessionData.id}"/>
        <json:property name="sessionData" value="${sessionData.sessionData}"/>
        <json:object escapeXml="false" name="session">
        <json:property name="id" value="${sessionData.id}"/>
        <json:property name="sessionData" value="${sessionData.sessionData}"/>
        </json:object>
        </json:object>
        </c:set>

        <c:set var="callbackConfig">
        <json:object escapeXml="false" name="callbackConfig">
        <json:object escapeXml="false" name="amount">
        <json:property name="value" value="${amount.value}"/>
        <json:property name="currency" value="${amount.currency}"/>
        </json:object>
        <json:property name="merchantAccount" value="${merchantAccount}"/>
        <json:array name="label" items="${['visible-xs', 'hidden-xs']}"/>
        </json:object>
        </c:set>

        const initConfig = ${initConfig};
        const callbackConfig = ${callbackConfig};
        const fnCallbackArray = {};
        console.log("TEST: ${selectedPaymentMethod}");
        <c:choose>
        <%-- Configure components --%>
        <c:when test="${selectedPaymentMethod eq 'paypal' && (not empty paypalMerchantId || environmentMode eq 'test')}">
        fnCallbackArray['initiatePaypal'] = {
            ...callbackConfig,
            isImmediateCapture: ${immediateCapture},
            paypalMerchantId: "${paypalMerchantId}"
        }
        </c:when>

        <c:when test="${selectedPaymentMethod eq 'mbway'}">
        fnCallbackArray['initiateMbway'] = callbackConfig
        </c:when>

        <c:when test="${selectedPaymentMethod eq 'bizum'}">
        fnCallbackArray['initiateBizum'] = callbackConfig
        </c:when>

        <c:when test="${selectedPaymentMethod eq 'applepay'}">
        fnCallbackArray['initiateApplePay'] = {
            ...callbackConfig,
            countryCode: "${countryCode}",
            applePayMerchantIdentifier: "${applePayMerchantIdentifier}",
            applePayMerchantName: "${applePayMerchantName}",
        }
        </c:when>

        <c:when test="${selectedPaymentMethod eq 'pix'}">
        fnCallbackArray['initiatePix'] = callbackConfig
        </c:when>

        <c:when test="${selectedPaymentMethod eq 'googlepay'}">
        fnCallbackArray['initiateGooglePay'] = callbackConfig
        </c:when>

        <c:when test="${selectedPaymentMethod eq 'amazonpay'}">
        fnCallbackArray['initiateAmazonPay'] = {
            ...callbackConfig,
            deliveryAddress: ${deliveryAddress},
            amazonPayConfiguration: ${amazonPayConfiguration},
            locale: ${locale},
            label: null,
        };
        </c:when>

        <c:when test="${selectedPaymentMethod eq 'upi'}">
        fnCallbackArray['initiateUPI'] = callbackConfig
        </c:when>

        <c:when test="${selectedPaymentMethod eq 'bcmc_mobile'}">
        fnCallbackArray['initiateBcmcMobile'] = callbackConfig;
        </c:when>

        <c:when test="${selectedPaymentMethod eq 'giftcard'}">
        fnCallbackArray['initiateGiftCard'] = callbackConfig;
        </c:when>

        <c:when test="${selectedPaymentMethod eq 'blik'}">
        fnCallbackArray['initiateBlik'] = callbackConfig
        </c:when>

        <%-- API only payments methods --%>
        <c:otherwise>
        AdyenCheckoutHybris.configureButton($("#placeOrderForm-hidden-xs"), true, "hidden-xs");
        AdyenCheckoutHybris.configureButton($("#placeOrderForm-visible-xs"), true, "visible-xs");
        </c:otherwise>
        </c:choose>

        AdyenCheckoutHybris.initiateCheckout(initConfig, fnCallbackArray);

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

            <div id="adyen-checkout-visible-xs" class="visible-xs clearfix side-margins">
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
