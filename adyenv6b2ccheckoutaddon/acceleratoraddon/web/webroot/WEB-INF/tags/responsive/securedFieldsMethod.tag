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
            <div id="card-div"></div>
        </div>
    </div>
</div>
