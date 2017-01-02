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
<c:url value="/checkout/multi/adyen/select-payment-method/cse" var="addCseDataUrl"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

    <jsp:attribute name="pageScripts">
    <script type="text/javascript" src="https://test.adyen.com/hpp/cse/js/7814780137220009.shtml"></script>
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


                            <ycommerce:testId code="paymentDetailsForm">

                                <form:form method="post" commandName="csePaymentForm" class="create_update_payment_form"
                                           id="adyen-encrypted-form" action="${addCseDataUrl}">

                                    <form:hidden path="cseToken"/>
                                    <input type="hidden" id="paymentDetailsForm-expiry-generationtime"
                                           value="${generationTime}" data-encrypted-name="generationtime"/>

                                    <div id="cardDetailsFieldSet">
                                        <fieldset class="cardForm">
                                            <div class="form-group">
                                                <label for="card_number" class="control-label">
                                                        <%--<spring:theme code="Number"/>--%>
                                                    <spring:theme code="Number"/>
                                                </label>
                                                <input type="text" size="20"
                                                       <%--autocomplete="off"--%>
                                                       data-encrypted-name="number"/>
                                                <input type="text" size="4" maxlength="4"
                                                       <%--autocomplete="off"--%>
                                                       data-encrypted-name="cvc"/>
                                            </div>

                                            <div class="form-group">
                                                <label for="card_name" class="control-label">
                                                        <%--<spring:theme code="Number"/>--%>
                                                    <spring:theme code="Holder Name"/>
                                                </label>
                                                <input type="text" size="20"
                                                       <%--autocomplete="off"--%>
                                                       data-encrypted-name="holderName"/>
                                            </div>

                                            <div class="form-group">
                                                <label for="card_name" class="control-label">
                                                        <%--<spring:theme code="Number"/>--%>
                                                    <spring:theme code="Expiration"/>
                                                </label>
                                                <input type="text" size="2" maxlength="2"
                                                       <%--autocomplete="off"--%>
                                                       data-encrypted-name="expiryMonth"/>
                                                <input type="text" size="4" maxlength="4"
                                                       <%--autocomplete="off"--%>
                                                       data-encrypted-name="expiryYear"/>

                                            </div>
                                        </fieldset>
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

                                    <%--<input type="hidden" value="${silentOrderPageData.parameters['billTo_email']}"--%>
                                           <%--class="text" name="billTo_email" id="billTo_email">--%>
                                    <%--<address:billAddressFormSelector supportedCountries="${countries}"--%>
                                                                     <%--regions="${regions}" tabindex="12"/>--%>

                                    <%--<p class="help-block"><spring:theme--%>
                                            <%--code="checkout.multi.paymentMethod.seeOrderSummaryForMoreInformation"/></p>--%>

                                </form:form>
                            </ycommerce:testId>

                        </div>
                    </div>

                    <button type="button" class="btn btn-primary btn-block submit_silentOrderPostForm checkout-next">
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
