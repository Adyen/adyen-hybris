<%--
  ~                        ######
  ~                        ######
  ~  ############    ####( ######  #####. ######  ############   ############
  ~  #############  #####( ######  #####. ######  #############  #############
  ~         ######  #####( ######  #####. ######  #####  ######  #####  ######
  ~  ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
  ~  ###### ######  #####( ######  #####. ######  #####          #####  ######
  ~  #############  #############  #############  #############  #####  ######
  ~   ############   ############  #############   ############  #####  ######
  ~                                       ######
  ~                                #############
  ~                                ############
  ~
  ~  Adyen Hybris Extension
  ~
  ~  Copyright (c) 2017 Adyen B.V.
  ~  This file is open source and available under the MIT license.
  ~  See the LICENSE file for more info.
  --%>
<%@ attribute name="showRememberTheseDetails" required="true" type="java.lang.Boolean" %>
<%@ taglib prefix="adyen" tagdir="/WEB-INF/tags/addons/adyenv6b2ccheckoutaddon/responsive" %>

<div class="chckt-pm chckt-pm-card js-chckt-pm js-chckt-pm__pm-holder" data-additional-required="" data-pm="card">
    <input type="hidden" name="txvariant" value="card">
    <div class="chckt-pm__header js-chckt-pm__header">
        <adyen:methodSelector name="adyen_cc"/>
        <span class="chckt-pm__name js-chckt-pm__name">Credit Card</span>
        <span class="chckt-pm__image">
            <span id="cardLogos"></span>
            <span class="chckt-pm__image-border"></span>
        </span>
    </div>

    <div class="chckt-pm__details js-chckt-pm__details payment_method_details" id="dd_method_adyen_cc">
        <div class="chckt-form chckt-form--max-width">
            <label class="chckt-form-label chckt-form-label--full-width">
                <span class="chckt-form-label__text js-chckt-card-label">Card Number:</span>
                <span class="chckt-input-field js-chckt-hosted-input-field" data-hosted-id="hostedCardNumberField" data-cse="encryptedCardNumber"></span>
                <span class="chckt-form-label__error-text">Invalid card number</span>
            </label>

            <label class="chckt-form-label chckt-form-label--exp-date">
                <span class="chckt-form-label__text">Expiry Date:</span>
                <span class="chckt-input-field js-chckt-hosted-input-field" data-hosted-id="hostedExpiryDateField" data-cse="encryptedExpiryDate"></span>
                <span class="chckt-form-label__error-text">Invalid expiration date</span>
            </label>

            <label class="chckt-form-label chckt-form-label--cvc">
                <span class="chckt-form-label__text js-chckt-cvc-field-label">CVV:</span>
                <span class="chckt-input-field chckt-input-field--cvc js-chckt-hosted-input-field" data-hosted-id="hostedSecurityCodeField" data-cse="encryptedSecurityCode"
                      data-optional="false"></span>
                <span class="chckt-form-label__error-text">Please enter a valid CVC to continue.</span>
            </label>

            <label class="chckt-form-label chckt-form-label--full-width" data-enrich="holderName">
                <span class="chckt-form-label__text">holderName:</span>
                <input class="chckt-input-field js-chckt-holdername" name="cardHolder" placeholder="" data-shopper-locale="en-US" type="text" size="20">
            </label>

            <c:if test="${showRememberTheseDetails}">
                <label class="chckt-form-label chckt-form-label--full-width">
                    <input class="chckt-checkbox" checked="" type="checkbox" name="rememberTheseDetails" value="true">
                    <span class="chckt-form-label__text">
                        Save for my next payment
                    </span>
                </label>
            </c:if>
        </div>
    </div>
</div>
