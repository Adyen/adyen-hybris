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
    <script type="text/javascript" src="https://test.adyen.com/hpp/cse/js/${cseId}.shtml"></script>
    <script type="text/javascript">
        // The form element to encrypt.
        var form = document.getElementById('adyen-encrypted-form');

        // Form and encryption options. See adyen.encrypt.simple.html for details.
        var options = {};

        // Create the form.
        // Note that the method is on the adyen object, not the adyen.encrypt object.
        var encryptedForm = adyen.createEncryptedForm(form, options);

        $(".submit_silentOrderPostForm").click(function (event) {
            var encryptedData = encryptedForm.encrypt();
            $("#cseToken").val(encryptedData);

            $("#adyen-encrypted-form").submit();
        });

        var Adyen = {};
        Adyen.setBrandCode = function (brandCode) {
            $("#brandCode").val(brandCode);
        }
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
                                    <form:hidden path="brandCode"/>
                                    <form:hidden path="issuerId"/>

                                    <div class="fieldset">
                                        <dl class="sp-methods" id="checkout-payment-method-load">
                                            <dt id="dt_method_adyen_cc">
                                                <input id="p_method_adyen_cc" value="adyen_cc" type="radio"
                                                       name="paymentMethod" title="Adyen Credit Card" class="radio"
                                                       autocomplete="off">
                                                <label for="p_method_adyen_cc">
                                                    <span>Credit Card</span>
                                                </label>
                                            </dt>
                                            <dd id="dd_method_adyen_cc" class="payment_method_details">
                                                <ul class="form-list" id="payment_form_adyen_cc">
                                                    <li class="adyen_payment_input_fields adyen_payment_input_fields_cc_number">
                                                        <label for="creditCardNumber" class="required"><em>*</em>Credit
                                                            Card Number</label>
                                                        <div class="input-box">
                                                            <input type="text" id="creditCardNumber"
                                                                   data-encrypted-name="number"
                                                                   title="Credit Card Number"
                                                                   class="input-text validate-cc-type required-entry"
                                                                   value="" maxlength="23" autocomplete="off">
                                                        </div>
                                                    </li>
                                                    <li class="adyen_payment_input_fields adyen_payment_input_fields_cc_name">
                                                        <label for="creditCardHolderName" class="required"><em>*</em>Name
                                                            on Card</label>
                                                        <div class="input-box">
                                                            <input type="text" title="Name on Card"
                                                                   class="input-text required-entry"
                                                                   id="creditCardHolderName"
                                                                   data-encrypted-name="holderName" value=""
                                                                   maxlength="100" autocomplete="off">
                                                        </div>
                                                    </li>
                                                    <li class="adyen_payment_input_fields adyen_payment_input_fields_expiry">
                                                        <label for="adyen_cc_expiration" class="required"><em>*</em>Expiration
                                                            Date</label>
                                                        <div class="input-box">
                                                            <div class="v-fix adyen_expiry_month">
                                                                <select id="adyen_cc_expiration"
                                                                        data-encrypted-name="expiryMonth"
                                                                        class="month validate-cc-exp required-entry"
                                                                        autocomplete="off">
                                                                    <option value="" selected="selected">Month</option>
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
                                                            </div>
                                                            <div class="v-fix adyen_expiry_year">
                                                                <select id="adyen_cc_expiration_yr"
                                                                        data-encrypted-name="expiryYear"
                                                                        class="year required-entry" autocomplete="off">
                                                                    <option value="" selected="selected">Year</option>
                                                                    <option value="2017">2017</option>
                                                                    <option value="2018">2018</option>
                                                                    <option value="2019">2019</option>
                                                                    <option value="2020">2020</option>
                                                                    <option value="2021">2021</option>
                                                                    <option value="2022">2022</option>
                                                                    <option value="2023">2023</option>
                                                                    <option value="2024">2024</option>
                                                                    <option value="2025">2025</option>
                                                                    <option value="2026">2026</option>
                                                                    <option value="2027">2027</option>
                                                                </select>
                                                            </div>
                                                        </div>
                                                    </li>

                                                    <li class="adyen_payment_input_fields adyen_payment_input_fields_cc_verify">
                                                        <label id="adyen_cc_cc_cid_label" for="adyen_cc_cc_cid"
                                                               class="required"><em id="adyen_cc_cc_cid_label_em">*</em>Card
                                                            Verification Number</label>
                                                        <div class="input-box">
                                                            <div class="v-fix">
                                                                <input type="text" title="Card Verification Number"
                                                                       class="input-text cvv required-entry validate-digits validate-length"
                                                                       id="adyen_cc_cc_cid" data-encrypted-name="cvc"
                                                                       value="" size="7" maxlength="4"
                                                                       autocomplete="off">
                                                            </div>
                                                        </div>
                                                    </li>

                                                    <input type="hidden" id="paymentDetailsForm-expiry-generationtime"
                                                           value="${generationTime}"
                                                           data-encrypted-name="generationtime"/>
                                                </ul>
                                            </dd>

                                            <c:forEach items="${paymentMethods}" var="paymentMethod">
                                                <dt id="dt_method_adyen_hpp_${paymentMethod.brandCode}">
                                                    <input id="p_method_adyen_hpp_${paymentMethod.brandCode}"
                                                           value="${paymentMethod.brandCode}" type="radio"
                                                           name="paymentMethod" title="${paymentMethod.name}"
                                                           onclick="Adyen.setBrandCode('${paymentMethod.brandCode}')"
                                                           autocomplete="off">
                                                    <label for="p_method_adyen_hpp_${paymentMethod.brandCode}">
                                                        <span>${paymentMethod.name}</span>
                                                    </label>
                                                </dt>
                                            </c:forEach>

                                        </dl>
                                    </div>


                                    <hr/>
                                    <div class="headline">
                                        <spring:theme
                                                code="checkout.multi.paymentMethod.addPaymentDetails.billingAddress"/>
                                    </div>


                                    <c:if test="${cartData.deliveryItemsQuantity > 0}">

                                        <div id="useDeliveryAddressData"
                                             data-titlecode="${deliveryAddress.titleCode}"
                                             data-firstname="${deliveryAddress.firstName}"
                                             data-lastname="${deliveryAddress.lastName}"
                                             data-line1="${deliveryAddress.line1}"
                                             data-line2="${deliveryAddress.line2}"
                                             data-town="${deliveryAddress.town}"
                                             data-postalcode="${deliveryAddress.postalCode}"
                                             data-countryisocode="${deliveryAddress.country.isocode}"
                                             data-regionisocode="${deliveryAddress.region.isocodeShort}"
                                             data-address-id="${deliveryAddress.id}"
                                        ></div>
                                        <formElement:formCheckbox
                                                path="useDeliveryAddress"
                                                idKey="useDeliveryAddress"
                                                labelKey="checkout.multi.sop.useMyDeliveryAddress"
                                                tabindex="11"/>

                                    </c:if>

                                </form:form>
                            </div>
                        </div>

                        <button type="button"
                                class="btn btn-primary btn-block submit_silentOrderPostForm checkout-next">
                            <spring:theme code="checkout.multi.paymentMethod.continue"/></button>

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
