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
        <script type="text/javascript" src="https://${checkoutShopperHost}/checkoutshopper/sdk/3.0.0/adyen.js"></script>
        <link rel="stylesheet" href="https://checkoutshopper-live.adyen.com/checkoutshopper/css/chckt-default-v1.css"/>
        <link rel="stylesheet" href="https://${checkoutShopperHost}/checkoutshopper/sdk/3.0.0/adyen.css"/>

        <script type="text/javascript">
            AdyenCheckoutHybris.initiateCheckout("${shopperLocale}", "${environmentMode}", "${originKey}" );

            <c:if test="${not empty allowedCards}">
            //Set the allowed cards
            var allowedCards = [];
            <c:forEach items="${allowedCards}" var="allowedCard">
            allowedCards.push( "${allowedCard.code}" );
            </c:forEach>
            AdyenCheckoutHybris.initiateCard(allowedCards);

            </c:if>

            //Handle form submission
            $( ".submit_silentOrderPostForm" ).click( function ( event ) {
                if ( !AdyenCheckoutHybris.validateForm() ) {
                    return false;
                }
                AdyenCheckoutHybris.setCustomPaymentMethodValues();
                AdyenCheckoutHybris.setBrowserData();

                $( "#adyen-encrypted-form" ).submit();
            } );

            <c:if test="${not empty selectedPaymentMethod}">
            AdyenCheckoutHybris.togglePaymentMethod( "${selectedPaymentMethod}" );
            $( 'input[type=radio][name=paymentMethod][value="${selectedPaymentMethod}"]' ).prop("checked", true);
            </c:if>

            // Toggle payment method specific areas (credit card form and issuers list)
            $( 'input[type=radio][name=paymentMethod]' ).change( function () {
                var paymentMethod = this.value;
                AdyenCheckoutHybris.togglePaymentMethod( paymentMethod );
            } );

            AdyenCheckoutHybris.createDobDatePicker("p_method_adyen_hpp_dob");
            AdyenCheckoutHybris.createDfValue();


            <c:if test="${not empty iDealissuerList}">
            AdyenCheckoutHybris.initiateIdeal(${iDealissuerList});
            </c:if>

            <c:forEach items="${storedCards}" var="storedCard">

            //convert java stored card object to javascript object
            var storedCardJS= {
                    type: "${storedCard.variant}",
                    reference: "${storedCard.recurringDetailReference}",
                    number: "${storedCard.card.number}",
                    expiryMonth: "${storedCard.card.expiryMonth}",
                    expiryYear: "${storedCard.card.expiryYear}"
                };

            AdyenCheckoutHybris.initiateOneClickCard(storedCardJS);
            </c:forEach>


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
                            <form:hidden path="cardHolder"/>
                            <form:hidden path="cardBrand"/>

                            <form:hidden path="encryptedCardNumber"/>
                            <form:hidden path="encryptedExpiryMonth"/>
                            <form:hidden path="encryptedExpiryYear"/>
                            <form:hidden path="encryptedSecurityCode"/>

                            <form:hidden path="browserInfo"/>
                            <form:hidden path="rememberTheseDetails" value="false"/>

                            <div class="chckt-pm-list js-chckt-pm-list">
                                <c:forEach items="${storedCards}" var="storedCard">
                                    <adyen:storedCardMethod
                                            variant="${storedCard.variant}"
                                            cardReference="${storedCard.recurringDetailReference}"
                                            cardNumber="${storedCard.card.number}"
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
                                            name="iDEAL"
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
