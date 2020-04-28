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
        <script type="text/javascript" src="https://${checkoutShopperHost}/checkoutshopper/sdk/3.4.0/adyen.js"></script>
        <link rel="stylesheet" href="https://checkoutshopper-live.adyen.com/checkoutshopper/css/chckt-default-v1.css"/>
        <link rel="stylesheet" href="https://${checkoutShopperHost}/checkoutshopper/sdk/3.4.0/adyen.css"/>

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


            <c:if test="${not empty issuerLists['ideal']}">
            AdyenCheckoutHybris.initiateIdeal(${issuerLists['ideal']});
            </c:if>

            <c:if test="${not empty issuerLists['eps']}">
            AdyenCheckoutHybris.initiateEps(${issuerLists['eps']});
            </c:if>

            <c:forEach items="${storedCards}" var="storedCard">

            var storedCardJS=
                {
                    storedPaymentMethodId: "${storedCard.id}",
                    name: "${storedCard.name}",
                    type: "${storedCard.type}",
                    brand: "${storedCard.brand}",
                    lastFour: "${storedCard.lastFour}",
                    expiryMonth: "${storedCard.expiryMonth}",
                    expiryYear: "${storedCard.expiryYear}",
                    holderName: "${storedCard.holderName}",
                    supportedShopperInteractions: "${storedCard.supportedShopperInteractions}",
                    shopperEmail: "${storedCard.shopperEmail}"
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
                        <form:form method="post" modelAttribute="adyenPaymentForm"
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
                            <form:hidden path="cardType"/>

                            <form:hidden path="encryptedCardNumber"/>
                            <form:hidden path="encryptedExpiryMonth"/>
                            <form:hidden path="encryptedExpiryYear"/>
                            <form:hidden path="encryptedSecurityCode"/>

                            <form:hidden path="browserInfo"/>
                            <form:hidden path="terminalId"/>
                            <form:hidden path="rememberTheseDetails" value="false"/>


                            <%-- Billing Information --%>
                            <div class="section_break"><spring:message text="Billing Information"/></div>
                            <c:if test="${cartData.deliveryItemsQuantity > 0}">

                                <formElement:formCheckbox
                                        path="useDeliveryAddress"
                                        idKey="useDeliveryAddress"
                                        labelKey="checkout.multi.sop.useMyDeliveryAddress"
                                        tabindex="11"/>
                            </c:if>
                            <div id="newBillingAddressFields" class="cardForm">

                                <formElement:formSelectBox idKey="address.titleCode" labelKey="address.title" path="billingAddress.titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect" items="${titles}" tabindex="13"/>
                                <formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="billingAddress.firstName" inputCSS="text" mandatory="true" tabindex="11"/>
                                <formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="billingAddress.lastName" inputCSS="text" mandatory="true" tabindex="12"/>
                                <formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="billingAddress.line1" inputCSS="text" mandatory="true" tabindex="14"/>
                                <formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="billingAddress.line2" inputCSS="text" mandatory="false" tabindex="15"/>
                                <formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="billingAddress.townCity" inputCSS="text" mandatory="true" tabindex="16"/>
                                <formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="billingAddress.postcode" inputCSS="text" mandatory="true" tabindex="17"/>
                                <formElement:formSelectBox idKey="address.country" labelKey="address.country" path="billingAddress.countryIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectCountry" items="${billingCountries}" itemValue="isocode" tabindex="18"/>
                                <formElement:formInputBox idKey="address.phoneNumber" labelKey="Phone Number" path="billingAddress.phoneNumber" inputCSS="text" mandatory="false" tabindex="17"/>

                            </div>
                            <%-- Billing Information end --%>

                            <div class="chckt-pm-list js-chckt-pm-list">
                                <c:forEach items="${storedCards}" var="storedCard">
                                    <adyen:storedCardMethod
                                            variant="${storedCard.brand}"
                                            cardReference="${storedCard.id}"
                                            cardNumber="${storedCard.lastFour}"
                                    />
                                </c:forEach>

                                <c:if test="${not empty allowedCards}">
                                    <adyen:securedFieldsMethod
                                            showRememberTheseDetails="${showRememberTheseDetails}"
                                            showComboCard="${showComboCard}"/>
                                </c:if>

                                    <%--to-do populate issuers and rest of items via checkout components--%>
                                <c:forEach items="${paymentMethods}" var="paymentMethod">
                                    <adyen:alternativeMethod
                                            brandCode="${paymentMethod.type}"
                                            name="${paymentMethod.name}"
                                            showDob="${paymentMethod.type=='ratepay'}"
                                            showSocialSecurityNumber="${showSocialSecurityNumber}"
                                    />
                                </c:forEach>

                                <c:if test="${not empty issuerLists['ideal']}">
                                    <adyen:alternativeMethod
                                            brandCode="ideal"
                                            name="iDEAL"
                                    />
                                </c:if>

                                <c:if test="${not empty issuerLists['eps']}">
                                    <adyen:alternativeMethod
                                            brandCode="eps"
                                            name="EPS"
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
                                <c:if test="${not empty connectedTerminalList}">
                                    <adyen:alternativeMethod
                                            brandCode="pos"
                                            name="POS"
                                            showTerminalList="true"
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
