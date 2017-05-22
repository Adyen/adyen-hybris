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

<c:url value="${currentStepUrl}" var="choosePaymentMethodUrl"/>
<c:url value="/checkout/multi/adyen/select-payment-method" var="selectPaymentMethod"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
    <jsp:attribute name="pageScripts">
        <script type="text/javascript" src="${cseUrl}"></script>
        <script type="text/javascript" src="${dfUrl}"></script>

        <script type="text/javascript">
            <c:if test="${not empty allowedCards}">
            //Set the allowed cards
            var allowedCards = [];
            <c:forEach items="${allowedCards}" var="allowedCard">
            allowedCards.push( "${allowedCard.code}" );
            </c:forEach>

            var encryptedForm = AdyenCheckout.createForm();
            var cardLogosContainer = document.getElementById( 'cardLogos' );
            AdyenCheckout.enableCardTypeDetection( allowedCards, cardLogosContainer, encryptedForm );
            </c:if>

            <c:forEach items="${storedCards}" var="storedCard">
            AdyenCheckout.createOneClickForm( "${storedCard.recurringDetailReference}" );
            </c:forEach>

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
            </c:if>

            // Toggle payment method specific areas (credit card form and issuers list)
            $( 'input[type=radio][name=paymentMethod]' ).change( function () {
                var paymentMethod = this.value;
                AdyenCheckout.togglePaymentMethod( paymentMethod );
            } );

            AdyenCheckout.createDobDatePicker( "p_method_adyen_hpp_dob" );

            AdyenCheckout.createDfValue();
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
                        <div class="checkout-paymentmethod">
                            <div class="checkout-indent">

                                <div class="headline"><spring:theme code="checkout.multi.paymentMethod"/></div>

                                <form:form method="post" commandName="adyenPaymentForm"
                                           class="create_update_payment_form"
                                           id="adyen-encrypted-form" action="${selectPaymentMethod}">

                                    <form:hidden path="cseToken"/>
                                    <form:hidden path="selectedReference"/>
                                    <form:hidden path="issuerId"/>
                                    <form:hidden path="dob"/>
                                    <form:hidden path="socialSecurityNumber"/>
                                    <form:hidden path="dfValue"/>

                                    <div class="fieldset">
                                        <dl class="sp-methods" id="checkout-payment-method-load">
                                            <c:if test="${not empty allowedCards}">
                                                <dt id="dt_method_adyen_cc">
                                                    <input id="p_method_adyen_cc" value="adyen_cc" type="radio"
                                                           name="paymentMethod" title="Credit Card"
                                                    <c:if test="${selectedPaymentMethod == 'adyen_cc'}"> checked </c:if>
                                                           autocomplete="off">
                                                    <label for="p_method_adyen_cc">
                                                        <span>Credit Card</span>
                                                    </label>
                                                </dt>

                                                <div id="dd_method_adyen_cc" class="payment_method_details">
                                                    <ul class="form-list" id="payment_form_adyen_cc">
                                                        <li class="adyen_payment_input_fields">
                                                            <span id="cardLogos"></span>
                                                        </li>
                                                        <li class="adyen_payment_input_fields adyen_payment_input_fields_cc_number">
                                                            <label for="creditCardNumber" class="required">Credit Card
                                                                Number</label>
                                                            <div class="input-box">
                                                                <input type="text" id="creditCardNumber"
                                                                       data-encrypted-name="number"
                                                                       title="Credit Card Number"
                                                                       class="input-text validate-cc-type required-entry"
                                                                       value="" maxlength="23" autocomplete="off">
                                                            </div>
                                                        </li>
                                                        <li class="adyen_payment_input_fields adyen_payment_input_fields_cc_name">
                                                            <label for="creditCardHolderName" class="required">Name on
                                                                Card</label>
                                                            <div class="input-box">
                                                                <input type="text" title="Name on Card"
                                                                       class="input-text required-entry"
                                                                       id="creditCardHolderName"
                                                                       data-encrypted-name="holderName" value=""
                                                                       maxlength="100" autocomplete="off">
                                                            </div>
                                                        </li>
                                                        <li class="adyen_payment_input_fields adyen_payment_input_fields_expiry">
                                                            <label for="adyen_cc_expiration" class="required">Expiration
                                                                Date</label>
                                                            <div class="input-box">
                                                                <div class="v-fix adyen_expiry_month">
                                                                    <select id="adyen_cc_expiration"
                                                                            data-encrypted-name="expiryMonth"
                                                                            class="month validate-cc-exp required-entry"
                                                                            autocomplete="off">
                                                                        <option value="" selected="selected">Month
                                                                        </option>
                                                                        <option value="1">01 - January</option>
                                                                        <option value="2">02 - February</option>
                                                                        <option value="3">03 - March</option>
                                                                        <option value="4">04 - April</option>
                                                                        <option value="5">05 - May</option>
                                                                        <option value="6">06 - June</option>
                                                                        <option value="7">07 - July</option>
                                                                        <option value="8">08 - August</option>
                                                                        <option value="9">09 - September</option>
                                                                        <option value="10">10 - October</option>
                                                                        <option value="11">11 - November</option>
                                                                        <option value="12">12 - December</option>
                                                                    </select>
                                                                    <select id="adyen_cc_expiration_yr"
                                                                            data-encrypted-name="expiryYear"
                                                                            class="year required-entry"
                                                                            autocomplete="off">
                                                                        <option value="" selected="selected">Year
                                                                        </option>
                                                                        <c:forEach items="${expiryYears}" var="year">
                                                                            <option value="${year}">${year}</option>
                                                                        </c:forEach>
                                                                    </select>
                                                                </div>
                                                            </div>
                                                        </li>

                                                        <li class="adyen_payment_input_fields adyen_payment_input_fields_cc_verify">
                                                            <label id="adyen_cc_cc_cid_label" for="adyen_cc_cc_cid"
                                                                   class="required">Card Verification Number</label>
                                                            <div class="input-box">
                                                                <div class="v-fix">
                                                                    <input type="text" title="Card Verification Number"
                                                                           class="input-text cvv required-entry validate-digits validate-length"
                                                                           id="adyen_cc_cc_cid"
                                                                           data-encrypted-name="cvc"
                                                                           value="" size="7" maxlength="4"
                                                                           autocomplete="off">
                                                                </div>
                                                            </div>
                                                        </li>

                                                        <c:if test="${showRememberTheseDetails}">
                                                            <li class="adyen_payment_input_fields adyen_payment_input_fields_remember_these_details">
                                                                <label id="adyen_cc_remember_these_details_label" for="adyen_cc_remember_these_details">
                                                                    Remember these details
                                                                </label>
                                                                <div class="input-box">
                                                                    <div class="v-fix">
                                                                        <input type="checkbox" title="Remember these details"
                                                                               class="input-checkbox remember-these-details"
                                                                               name="rememberTheseDetails"
                                                                               id="adyen_cc_remember_these_details"
                                                                               value="1" checked>
                                                                    </div>
                                                                </div>
                                                            </li>
                                                        </c:if>

                                                        <input type="hidden" id="paymentDetailsForm-expiry-generationtime"
                                                               value="${generationTime}"
                                                               data-encrypted-name="generationtime"/>
                                                    </ul>
                                                </div>
                                            </c:if>


                                            <c:forEach items="${storedCards}" var="storedCard">
                                                <dt id="dt_method_adyen_oneclick_${storedCard.recurringDetailReference}">
                                                    <c:set var="cardReference" value="adyen_oneclick_${storedCard.recurringDetailReference}"/>
                                                    <input id="p_method_adyen_oneclick_${storedCard.recurringDetailReference}"
                                                           value="${cardReference}" type="radio"
                                                           name="paymentMethod" title="Credit Card"
                                                           autocomplete="off"
                                                    <c:if test="${selectedPaymentMethod == cardReference}"> checked </c:if>
                                                    >
                                                    <img src="https://live.adyen.com/hpp/img/pm/${storedCard.variant}.png"/>
                                                    <label for="p_method_adyen_oneclick_${storedCard.recurringDetailReference}">
                                                        <span>${storedCard.card.holderName} - ****${storedCard.card.number}</span>
                                                    </label>
                                                </dt>

                                                <div id="dd_method_adyen_oneclick_${storedCard.recurringDetailReference}" class="payment_method_details">
                                                    <ul class="form-list" id="payment_form_adyen_oneclick_${storedCard.recurringDetailReference}">
                                                        <li class="adyen_payment_input_fields adyen_payment_input_fields_expiry">
                                                            <label for="adyen_cc_expiration" class="required">Expiration
                                                                Date</label>
                                                            <div class="input-box">
                                                                <div class="v-fix adyen_expiry_month">
                                                                    <select data-encrypted-name-${storedCard.recurringDetailReference}="expiryMonth"
                                                                            class="month validate-cc-exp required-entry"
                                                                            autocomplete="off">
                                                                        <option value="1" <c:if test="${storedCard.card.expiryMonth == 1}">selected</c:if>>01 - January</option>
                                                                        <option value="2" <c:if test="${storedCard.card.expiryMonth == 2}">selected</c:if>>02 - February</option>
                                                                        <option value="3" <c:if test="${storedCard.card.expiryMonth == 3}">selected</c:if>>03 - March</option>
                                                                        <option value="4" <c:if test="${storedCard.card.expiryMonth == 4}">selected</c:if>>04 - April</option>
                                                                        <option value="5" <c:if test="${storedCard.card.expiryMonth == 5}">selected</c:if>>05 - May</option>
                                                                        <option value="6" <c:if test="${storedCard.card.expiryMonth == 6}">selected</c:if>>06 - June</option>
                                                                        <option value="7" <c:if test="${storedCard.card.expiryMonth == 7}">selected</c:if>>07 - July</option>
                                                                        <option value="8" <c:if test="${storedCard.card.expiryMonth == 8}">selected</c:if>>08 - August</option>
                                                                        <option value="9" <c:if test="${storedCard.card.expiryMonth == 9}">selected</c:if>>09 - September</option>
                                                                        <option value="10" <c:if test="${storedCard.card.expiryMonth == 10}">selected</c:if>>10 - October</option>
                                                                        <option value="11" <c:if test="${storedCard.card.expiryMonth == 11}">selected</c:if>>11 - November</option>
                                                                        <option value="12" <c:if test="${storedCard.card.expiryMonth == 12}">selected</c:if>>12 - December</option>
                                                                    </select>
                                                                    <select data-encrypted-name-${storedCard.recurringDetailReference}="expiryYear"
                                                                            class="year required-entry"
                                                                            autocomplete="off">
                                                                        <c:forEach items="${expiryYears}" var="year">
                                                                            <option value="${year}" <c:if test="${storedCard.card.expiryYear == year}">selected</c:if>>${year}</option>
                                                                        </c:forEach>
                                                                    </select>
                                                                </div>
                                                            </div>
                                                        </li>

                                                        <li class="adyen_payment_input_fields adyen_payment_input_fields_cc_verify">
                                                            <label class="required">Card Verification Number</label>
                                                            <div class="input-box">
                                                                <div class="v-fix">
                                                                    <input type="text" title="Card Verification Number"
                                                                           class="input-text cvv required-entry validate-digits validate-length"
                                                                           data-encrypted-name-${storedCard.recurringDetailReference}="cvc"
                                                                           value="" size="7" maxlength="4"
                                                                           autocomplete="off">

                                                                    <input type="hidden" value="${generationTime}"
                                                                           data-encrypted-name-${storedCard.recurringDetailReference}="generationtime"/>
                                                                </div>
                                                            </div>
                                                        </li>
                                                    </ul>
                                                </div>
                                            </c:forEach>


                                            <c:forEach items="${paymentMethods}" var="paymentMethod">
                                                <c:if test="${not paymentMethod.isCard()}">
                                                    <dt id="dt_method_adyen_hpp_${paymentMethod.brandCode}">
                                                    <input id="p_method_adyen_hpp_${paymentMethod.brandCode}"
                                                           value="${paymentMethod.brandCode}" type="radio"
                                                           name="paymentMethod" title="${paymentMethod.name}"
                                                           autocomplete="off"
                                                    <c:if test="${selectedPaymentMethod == paymentMethod.brandCode}"> checked </c:if>
                                                    >
                                                    <img src="https://live.adyen.com/hpp/img/pm/${paymentMethod.brandCode}.png"/>
                                                    <label for="p_method_adyen_hpp_${paymentMethod.brandCode}">
                                                        <span>${paymentMethod.name}</span>
                                                    </label>

                                                    <c:if test="${not empty paymentMethod.issuers}">
                                                        <div id="adyen_hpp_${paymentMethod.brandCode}_container"
                                                             class="extra-fields-container">
                                                            <select class="issuer-select" tabindex="4"
                                                                    id="p_method_adyen_hpp_${paymentMethod.brandCode}_issuer">
                                                                <option value="" label="Please select Issuer"/>
                                                                <c:forEach items="${paymentMethod.issuers}"
                                                                           var="issuer">
                                                                    <option value="${issuer.issuerId}">${issuer.name}</option>
                                                                </c:forEach>
                                                            </select>
                                                        </div>
                                                    </c:if>


                                                    <c:if test="${openInvoiceMethods.contains(paymentMethod.brandCode)}">

                                                        <dl id="adyen_hpp_${paymentMethod.brandCode}_container"
                                                            class="extra-fields-container">

                                                            <dt>
                                                                <label for="p_method_adyen_hpp_${paymentMethod.brandCode}_dob">
                                                                    <span>Date of birth</span>
                                                                </label>
                                                                <input id="p_method_adyen_hpp_${paymentMethod.brandCode}_dob"
                                                                       class="p_method_adyen_hpp_dob"
                                                                       type="text"
                                                                       title="date of birth">
                                                            </dt>

                                                            <c:if test="${showSocialSecurityNumber}">
                                                                <dt>
                                                                    <label for="p_method_adyen_hpp_${paymentMethod.brandCode}_ssn">
                                                                        <span>Personal Number (last 4 digits)</span>
                                                                    </label>

                                                                    <input id="p_method_adyen_hpp_${paymentMethod.brandCode}_ssn"
                                                                           class="p_method_adyen_hpp_ssn"
                                                                           type="text"
                                                                           size="4"
                                                                           title="personal number">
                                                                </dt>
                                                            </c:if>
                                                        </dl>
                                                    </c:if>
                                                    </dt>
                                                </c:if>
                                            </c:forEach>

                                            <c:if test="${showBoleto}">
                                                <dt id="dt_method_adyen_boleto">
                                                    <input id="p_method_adyen_boleto"
                                                           value="boleto" type="radio"
                                                           name="paymentMethod" title="Boleto"
                                                           autocomplete="off"
                                                    <c:if test="${selectedPaymentMethod == 'boleto'}"> checked </c:if>
                                                    >
                                                    <img src="https://live.adyen.com/hpp/img/pm/boleto.png"/>
                                                    <label for="p_method_adyen_boleto">
                                                        <span>Boleto</span>
                                                    </label>
                                                </dt>
                                                <div id="dd_method_boleto"
                                                     class="extra-fields-container">

                                                    <ul class="form-list">
                                                        <li class="adyen_payment_input_fields">
                                                            <label class="required">First name</label>
                                                            <input type="text" name="firstName" value="${cartData.deliveryAddress.firstName}" />
                                                        </li>
                                                        <li class="adyen_payment_input_fields">
                                                            <label class="required">Last name</label>
                                                            <input type="text" name="lastName" value="${cartData.deliveryAddress.lastName}" />
                                                        </li>
                                                        <li class="adyen_payment_input_fields">
                                                            <label class="required">Social Security Number</label>

                                                            <input id="p_method_adyen_hpp_boleto_ssn"
                                                                   class="p_method_adyen_hpp_ssn"
                                                                   type="text">
                                                        </li>
                                                    </ul>
                                                </div>
                                            </c:if>
                                        </dl>
                                    </div>
                                </form:form>
                            </div>
                        </div>

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
