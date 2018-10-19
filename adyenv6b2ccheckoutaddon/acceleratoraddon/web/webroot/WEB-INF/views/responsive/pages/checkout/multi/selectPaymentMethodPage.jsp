<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="multiCheckout" tagdir="/WEB-INF/tags/responsive/checkout/multi" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="adyen" tagdir="/WEB-INF/tags/addons/adyenv6b2ccheckoutaddon/responsive" %>

<c:url value="${currentStepUrl}" var="choosePaymentMethodUrl"/>
<c:url value="/checkout/multi/adyen/select-payment-method" var="selectPaymentMethod"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
    <jsp:attribute name="pageScripts">
        <script type="text/javascript" src="${dfUrl}"></script>
        <script type="text/javascript" src="https://${checkoutShopperHost}/checkoutshopper/assets/js/sdk/checkoutSecuredFields.1.1.0.js"></script>
        <link rel="stylesheet" href="https://checkoutshopper-live.adyen.com/checkoutshopper/css/chckt-default-v1.css"/>

        <script type="text/javascript">
            <c:if test="${not empty allowedCards}">
            //Set the allowed cards
            var allowedCards = [];
            <c:forEach items="${allowedCards}" var="allowedCard">
            allowedCards.push( "${allowedCard.code}" );
            </c:forEach>

            var cardLogosContainer = document.getElementById( 'cardLogos' );
            AdyenCheckout.enableCardTypeDetection( allowedCards, cardLogosContainer );

            var securedFieldsForm = AdyenCheckout.createSecuredFieldsForm( "${originKey}", ".js-chckt-pm-list" );
            </c:if>

            //Handle form submission
            $( ".submit_silentOrderPostForm" ).click( function ( event ) {
                if ( !AdyenCheckout.validateForm() ) {
                    return false;
                }

                AdyenCheckout.setCustomPaymentMethodValues();

                $( "#adyen-encrypted-form" ).submit();
            } );

            <c:if test="${not empty selectedPaymentMethod}">
            AdyenCheckout.togglePaymentMethod( "${selectedPaymentMethod}" );
            $( 'input[type=radio][name=paymentMethod][value="${selectedPaymentMethod}"]' ).prop("checked", true);
            </c:if>

            // Toggle payment method specific areas (credit card form and issuers list)
            $( 'input[type=radio][name=paymentMethod]' ).change( function () {
                var paymentMethod = this.value;
                AdyenCheckout.togglePaymentMethod( paymentMethod );
            } );

            AdyenCheckout.createDobDatePicker("p_method_adyen_hpp_dob");
            AdyenCheckout.createDfValue();
            AdyenCheckout.initiateCheckout("${shopperLocale}");

            <c:if test="${not empty iDealissuerList}">
                AdyenCheckout.initiateIdeal(${iDealissuerList});
            </c:if>


        </script>
    </jsp:attribute>

    <jsp:body>
        <div class="row">
            <div class="col-sm-6">
                <div class="checkout-headline">
                    <span class="glyphicon glyphicon-lock"></span>
                    <spring:theme code="checkout.multi.secure.checkout"/>
                </div>
                <multiCheckout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
                    <jsp:body>
                        <form:form method="post" commandName="adyenPaymentForm"
                                   class="create_update_payment_form"
                                   id="adyen-encrypted-form" action="${selectPaymentMethod}">

                            <form:hidden path="cseToken"/>
                            <form:hidden path="selectedReference"/>
                            <form:hidden path="issuerId"/>
                            <form:hidden path="dob"/>
                            <form:hidden path="socialSecurityNumber"/>
                            <form:hidden path="dfValue"/>

                            <form:hidden path="encryptedCardNumber"/>
                            <form:hidden path="encryptedExpiryMonth"/>
                            <form:hidden path="encryptedExpiryYear"/>
                            <form:hidden path="encryptedSecurityCode"/>

                            <div class="chckt-pm-list js-chckt-pm-list">
                                <c:forEach items="${storedCards}" var="storedCard">
                                    <adyen:storedCardMethod
                                            variant="${storedCard.variant}"
                                            cardReference="${storedCard.recurringDetailReference}"
                                            cardNumber="${storedCard.card.number}"
                                            expiryMonth="${storedCard.card.expiryMonth}"
                                            expiryYear="${storedCard.card.expiryYear}"
                                    />
                                </c:forEach>

                                <c:if test="${not empty allowedCards}">
                                    <adyen:securedFieldsMethod showRememberTheseDetails="${showRememberTheseDetails}"/>
                                </c:if>

                                    <%--to-do populate issuers and rest of items via checkout components--%>
                                <c:forEach items="${paymentMethods}" var="paymentMethod">
                                    <adyen:alternativeMethod
                                            brandCode="${paymentMethod.type}"
                                            name="${paymentMethod.name}"
                                            showDob="${openInvoiceMethods.contains(paymentMethod.type)}"
                                            showSocialSecurityNumber="${showSocialSecurityNumber}"
                                    />
                                </c:forEach>

                                <c:if test="${not empty iDealissuerList}">
                                    <adyen:alternativeMethod
                                            brandCode="ideal"
                                            name="ideal"
                                    />
                                </c:if>

                                <c:if test="${showBoleto}">
                                    <adyen:alternativeMethod
                                            brandCode="boleto"
                                            name="Boleto"
                                            showSocialSecurityNumber="true"
                                            showFirstName="true"
                                            showLastName="true"
                                            countryCode="BR"
                                    />
                                </c:if>
                            </div>
                        </form:form>

                        <button type="button"
                                class="btn btn-primary btn-block submit_silentOrderPostForm checkout-next">
                            <spring:theme code="checkout.multi.paymentMethod.continue"/>
                        </button>

                    </jsp:body>
                </multiCheckout:checkoutSteps>
            </div>

            <div class="col-sm-6 hidden-xs">
                <multiCheckout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="true"
                                                    showPaymentInfo="false" showTaxEstimate="false" showTax="true"/>
            </div>

            <div class="col-sm-12 col-lg-12">
                <cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
                    <cms:component component="${feature}"/>
                </cms:pageSlot>
            </div>
        </div>
    </jsp:body>
</template:page>
