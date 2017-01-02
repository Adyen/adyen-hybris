<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/desktop/checkout/multi" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:url value="/checkout/multi/payment-method/add" var="choosePaymentMethodUrl"/>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	<jsp:attribute name="pageScripts">
		<script type="text/javascript" src="${contextPath}/_ui/addons/adyenAddon/desktop/common/js/adyen.encrypt.nodom.min.js?0_1_13"></script>
		<script type="text/javascript">
		var key = "${apiCESKey}";
		var options = {};
		var cseInstance = adyen.encrypt.createEncryption(key, options);
		$("#submitPaymentDetailsBtn").on("click", function(){
			var postData = {};
			$("#useBoleto").val(false);
			$("#useSavedPayment").val(false);
			$("#useCreditCard").val(true);
			
			if(!validateCCData()){
				return false;
			}
			
            // Generate this
            // serverside!
			var formData = {
		        number : $("#cardNumber").val(),
		        cvc : $("#securityCode").val(),
		        holderName : $("#nameOnCard").val(),
		        expiryMonth : $("#ExpiryMonth").val(),
		        expiryYear : $("#ExpiryYear").val(),
		        generationtime : "${generationTime}"
		    };
            
            $("#adyen-encrypted-data").val(cseInstance.encrypt(formData));
            
            $("#adyenPaymentDetailsForm").submit();
		});
		
		$("#submitBoletoDetailsBtn").on("click", function(){
			$("#useBoleto").val(true);
			$("#useSavedPayment").val(false);
			$("#useCreditCard").val(false);
			$("#adyenPaymentDetailsForm").submit();
		});
		
		$(".adyenPaymentDetailsFormElement").on("change", function(){
			if($(this).val() != '') {
				$(this).parent().parent().removeClass('error');
				$(this).css("border","1px solid #cccccc");
			}
		});
		
		
		function validateCCData(){
			var formData = {
		        number : $("#cardNumber").val(),
		        cvc : $("#securityCode").val(),
		        holderName : $("#nameOnCard").val(),
		        month : $("#ExpiryMonth").val(),
		        year : $("#ExpiryYear").val()
		    };
			var valid = cseInstance.validate(formData);		
			
			if(!valid.valid){
				if(!valid.number){
					showError($("#cardNumber"));
				} else if(!valid.holderName){
					showError($("#nameOnCard"));
				} else if(!valid.month){
					showError($("#ExpiryMonth"));
				} else if(!valid.year){
					showError($("#ExpiryYear"));
				} else if(!valid.cvc){
					showError($("#securityCode"));
				}
				return false;
			}
			return true;
		}
		
		function showError(element){
			$(element).parent().parent().addClass('error');
			$(element).css("border","1px solid #c90400");
			$(element).focus();
		}
		
		$(".useThisCard").on("click", function(){
			var savedPaymentMethodId = $(this).attr("saved-payment-method-id");
			$("#useBoleto").val(false);
			$("#useSavedPayment").val(true);
			$("#useCreditCard").val(false);
			var formData = {
		        cvc : $("#"+savedPaymentMethodId+"_cvc").val(),
		        month : $("#"+savedPaymentMethodId+"_ExpiryMonth").val(),
		        year : $("#"+savedPaymentMethodId+"_ExpiryYear").val()
		    };
			var valid = cseInstance.validate(formData);
			if(!valid.valid){
				
				if(!valid.month){
					showError($("#"+savedPaymentMethodId+"_ExpiryMonth"));
				} else if(!valid.year){
					showError("#"+savedPaymentMethodId+"_ExpiryYear");
				} else if(!valid.cvc){
					showError("#"+savedPaymentMethodId+"_cvc");
				}
				return;
			}
			
			$("#savedPaymentMethodId").val(savedPaymentMethodId);
			$("#savedPaymentMethodCVC").val($("#"+savedPaymentMethodId+"_cvc").val());
			$("#savePaymentMethodExpiryMonth").val($("#"+savedPaymentMethodId+"_ExpiryMonth").val());
			$("#savePaymentMethodExpiryYear").val($("#"+savedPaymentMethodId+"_ExpiryYear").val());
			$("#savedPaymentMethodCardNumber").val($("#"+savedPaymentMethodId+"_Number").val());
			$("#savedPaymentMethodOwner").val($("#"+savedPaymentMethodId+"_Owner").val());
			$("#savedPaymentMethodType").val($("#"+savedPaymentMethodId+"_Type").val());

			$("#adyenPaymentDetailsForm").submit();
		});
		
		function payByHPP(brandCode,issuerId){
			if(brandCode){
				$("#adyenPaymentBrand").val(brandCode);
			}else{
				$("#adyenPaymentBrand").val('');
			}
			if(issuerId){
				$("#issuerId").val(issuerId);
			}else{
				$("#issuerId").val('');
			}
			$("#useHPP").val(true);
			$("#useBoleto").val(false);
			$("#adyenPaymentDetailsForm").submit();
		}
		</script>
	</jsp:attribute>
	<jsp:body>

	<div id="globalMessages">
		<common:globalMessages/>
	</div>

	<multi-checkout:checkoutProgressBar steps="${checkoutSteps}" progressBarId="${progressBarId}"/>

	<div class="span-14 append-1">
		<div id="checkoutContentPanel" class="clearfix">
			<form:form method="post" commandName="adyenPaymentDetailsForm" class="create_update_payment_form">
			<c:if test="${apiEnabled}">
				<input type="hidden" id="paymentDetailsForm-expiry-generationtime" value="${generationTime}" data-encrypted-name="generationtime" />
				<input type="hidden" id="adyen-encrypted-data" name="adyen-encrypted-data" value="">
				<form:hidden path="savedPaymentMethodId"/>
				<form:hidden path="savedPaymentMethodCVC"/>
				<form:hidden path="savePaymentMethodExpiryMonth"/>
				<form:hidden path="savePaymentMethodExpiryYear"/>
				<form:hidden path="savedPaymentMethodCardNumber"/>
				<form:hidden path="savedPaymentMethodOwner"/>
				<form:hidden path="savedPaymentMethodType"/>
				<form:hidden path="useBoleto"/>
				<form:hidden path="useSavedPayment"/>
				<form:hidden path="useCreditCard"/>
				
				<!-- ADY-18 provide saved card information -->
				<c:if test="${cmsSite.adyenUseSavedPayment && cmsSite.adyenUseAPI && not empty savedAdyenPaymentMethods }">				
					<div class="headline clear"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.savedAdyenPaymentMethods" text="Saved Cards"/></div>
					<div class="cardForm">
						<ul>
						<c:forEach items="${savedAdyenPaymentMethods}" var="savedAdyenPaymentMethod" varStatus="status">
							<li id="${savedAdyenPaymentMethod.recurringDetail.recurringDetailReference}">
							<div class="savedCardDetails">
								<dl>
									<dt><spring:theme code="payment.cardNumber"/></dt>
									<dd>${savedAdyenPaymentMethod.recurringDetail.additionalData.cardBin}****${savedAdyenPaymentMethod.recurringDetail.card.number}
									<input type="hidden" id="${savedAdyenPaymentMethod.recurringDetail.recurringDetailReference}_Number" value="${savedAdyenPaymentMethod.recurringDetail.additionalData.cardBin}****${savedAdyenPaymentMethod.recurringDetail.card.number}">
									<input type="hidden" id="${savedAdyenPaymentMethod.recurringDetail.recurringDetailReference}_Type" value="${savedAdyenPaymentMethod.recurringDetail.variant}">
									</dd>
									<dt><spring:theme code="payment.nameOnCard"/></dt>
									<dd>${savedAdyenPaymentMethod.recurringDetail.card.holderName}
									<input type="hidden" id="${savedAdyenPaymentMethod.recurringDetail.recurringDetailReference}_Owner" value="${savedAdyenPaymentMethod.recurringDetail.card.holderName}">
									</dd>
									<dt><spring:theme code="payment.expiryDate"/></dt>
									<dd>
									<select class="adyenPaymentDetailsFormElement" tabindex="6" class="savedCardExpiryMonth" id="${savedAdyenPaymentMethod.recurringDetail.recurringDetailReference}_ExpiryMonth">
									<c:forEach items="${months}" var="month">
									<option value="${month.code}" <c:if test="${month.code eq savedAdyenPaymentMethod.recurringDetail.card.expiryMonth || month.code eq '0' + savedAdyenPaymentMethod.recurringDetail.card.expiryMonth}">selected="selected"</c:if>>${month.name}</option>
									</c:forEach>
									</select>
									
									<select class="adyenPaymentDetailsFormElement" tabindex="6" class="savedCardExpiryYear" id="${savedAdyenPaymentMethod.recurringDetail.recurringDetailReference}_ExpiryYear">
									<c:forEach items="${expiryYears}" var="year">
									<option value="${year.code}" <c:if test="${year.code eq savedAdyenPaymentMethod.recurringDetail.card.expiryYear}">selected="selected"</c:if>>${year.name}</option>
									</c:forEach>
									</select>
									</dd>
									<dt><spring:theme code="checkout.summary.paymentMethod.securityCode"/>*</dt>
									<dd><input type="text" class="text security savedCardSecurity adyenPaymentDetailsFormElement" maxlength="4" id="${savedAdyenPaymentMethod.recurringDetail.recurringDetailReference}_cvc"/></dd>
								</dl>
							</div>
							<div class="savedCardAction">
								<button class="positive useThisCard" tabindex="20" saved-payment-method-id="${savedAdyenPaymentMethod.recurringDetail.recurringDetailReference}" type="button">
									<spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.useThisCardDetails" text="[TODO]Use This Card"/>
								</button>
							</div>
							</li>
						</c:forEach>
						</ul>
					</div>
				</c:if>
				<%--End Saved Payments --%>
				<div class="headline clear" id="testDIV"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.paymentCard"/></div>
				<div class="required right"><spring:theme code="form.required"/></div>
				<div class="description"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.enterYourCardDetails"/></div>
				<div class="cardForm">
					<form:hidden path="paymentId" class="create_update_payment_id"/>
					<%-- <formElement:formSelectBox idKey="cardType" labelKey="payment.cardType" path="cardTypeCode" mandatory="true" skipBlank="false" selectCSSClass="adyenPaymentDetailsFormElement" skipBlankMessageKey="payment.cardType.pleaseSelect" items="${cardTypes}" tabindex="1"/> --%>
					<formElement:formInputBox idKey="nameOnCard" labelKey="payment.nameOnCard" path="nameOnCard" inputCSS="text adyenPaymentDetailsFormElement" mandatory="true" tabindex="2"/>
					<formElement:formInputBox idKey="cardNumber" labelKey="payment.cardNumber" path="cardNumber" inputCSS="text adyenPaymentDetailsFormElement" mandatory="true" tabindex="3"/>
					<fieldset id="startDate" class="cardDate">
						<legend><spring:theme code="payment.startDate"/></legend>
						<formElement:formSelectBox idKey="StartMonth" labelKey="payment.month" path="startMonth" mandatory="true" selectCSSClass="adyenPaymentDetailsFormElement" skipBlank="false" skipBlankMessageKey="" items="${months}" tabindex="4"/>
						<formElement:formSelectBox idKey="StartYear" labelKey="payment.year" path="startYear" mandatory="true" selectCSSClass="adyenPaymentDetailsFormElement" skipBlank="false" skipBlankMessageKey="" items="${startYears}" tabindex="5"/>
					</fieldset>
					<fieldset class="cardDate">
						<legend><spring:theme code="payment.expiryDate"/></legend>
						<formElement:formSelectBox idKey="ExpiryMonth" labelKey="payment.month" path="expiryMonth" selectCSSClass="adyenPaymentDetailsFormElement" mandatory="true" skipBlank="false" skipBlankMessageKey="" items="${months}" tabindex="6"/>
						<formElement:formSelectBox idKey="ExpiryYear" labelKey="payment.year" path="expiryYear" selectCSSClass="adyenPaymentDetailsFormElement" mandatory="true" skipBlank="false" skipBlankMessageKey="" items="${expiryYears}" tabindex="7"/>
					</fieldset>
					<div id="issueNum">
						<formElement:formInputBox idKey="payment.issueNumber" labelKey="payment.issueNumber" path="issueNumber" inputCSS="text adyenPaymentDetailsFormElement" mandatory="false" tabindex="8"/>
					</div>
					<formElement:formInputBox idKey="securityCode" labelKey="checkout.summary.paymentMethod.securityCode" path="securityCode" inputCSS="text adyenPaymentDetailsFormElement" mandatory="true" tabindex="3"/>
					
				</div>
				<div class="clear">
					<c:if test="${cmsSite.adyenUseSavedPayment && cmsSite.adyenUseAPI}">
						<form:checkbox id="savePaymentMethodToAdyen" path="savePayment" value="true" tabindex="9"/>
						<label for="savePaymentMethodToAdyen"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.savePaymentMethodToAdyen" text="[TODO]Save this payment method to Adyen"/></label>
					</c:if>
				</div>
				<c:if test="${not empty installments}">
					<div class="headline clear"><spring:theme code="payment.installments" text="[TODO]Installments"/></div>
					<div class="installments">
						<formElement:formSelectBox idKey="installments" labelKey="payment.installments" path="installments" mandatory="false" skipBlank="false" skipBlankMessageKey="payment.installments.pleaseSelect" items="${installments}" tabindex="1"/>
					</div>
				</c:if>			
				
	
				<div class="headline clear"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.billingAddress"/></div>
				<div class="description"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.billingAddressDiffersFromDeliveryAddress"/></div>
	
	
				<div>
					<c:if test="${cartData.deliveryItemsQuantity > 0}">
						<form:checkbox id="differentAddress" path="newBillingAddress" tabindex="9"/>
						<label for="differentAddress"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.enterDifferentBillingAddress"/></label>
					</c:if>
				</div>
	
	
				<div id="newBillingAddressFields" class="cardForm">
					<form:hidden path="billingAddress.addressId" class="create_update_address_id"/>
					<formElement:formSelectBox idKey="address.title" labelKey="address.title" path="billingAddress.titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect" items="${titles}" tabindex="10"/>
					<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="billingAddress.firstName" inputCSS="text" mandatory="true" tabindex="11"/>
					<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="billingAddress.lastName" inputCSS="text" mandatory="true" tabindex="12"/>
					<formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="billingAddress.line1" inputCSS="text" mandatory="true" tabindex="14"/>
					<formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="billingAddress.line2" inputCSS="text" mandatory="false" tabindex="15"/>
					<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="billingAddress.townCity" inputCSS="text" mandatory="true" tabindex="16"/>
					<formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="billingAddress.postcode" inputCSS="text" mandatory="true" tabindex="17"/>
					<formElement:formSelectBox idKey="address.country" labelKey="address.country" path="billingAddress.countryIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectCountry" items="${billingCountries}" itemValue="isocode" tabindex="18"/>
					<form:hidden path="billingAddress.shippingAddress"/>
					<form:hidden path="billingAddress.billingAddress"/>
				</div>
	
				<div class="save_payment_details">
					<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
						<form:checkbox id="SaveDetails" path="saveInAccount" tabindex="19"/>
						<label for="SaveDetails"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.savePaymentDetailsInAccount"/></label>
					</sec:authorize>
				</div>
				<div>
					<c:if test="${not hasNoPaymentInfo}">
						<a class="button" href="${choosePaymentMethodUrl}"><spring:theme code="checkout.multi.cancel" text="Cancel"/></a>
					</c:if>
					<ycommerce:testId code="editPaymentMethod_savePaymentMethod_button">
						<button class="positive" tabindex="20" id="submitPaymentDetailsBtn" type="button">
							<spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.useThesePaymentDetails"/>
						</button>
					</ycommerce:testId>
				</div>
				<c:if test="${boletoEnabled}">
					<div class="headline clear"><spring:theme code="payment.boleto" text="[TODO]Boleto"/></div>
					<div class="cardForm">
						<formElement:formSelectBox idKey="boletoBrands" labelKey="payment.boleto.brands" path="selectedBrand" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.boleto.brands.pleaseSelect" items="${boletoBrands}" tabindex="1"/>
						<formElement:formInputBox idKey="socialSecurityNumber" labelKey="payment.boleto.ssn" path="socialSecurityNumber" inputCSS="text adyenPaymentDetailsFormElement" mandatory="true"/>
						<formElement:formInputBox idKey="firstName" labelKey="payment.boleto.firstName" path="firstName" inputCSS="text adyenPaymentDetailsFormElement" mandatory="true"/>
						<formElement:formInputBox idKey="lastName" labelKey="payment.boleto.lastName" path="lastName" inputCSS="text adyenPaymentDetailsFormElement" mandatory="true"/>
						<formElement:formTextArea idKey="shopperStatement" labelKey="payment.boleto.shopperStatement" path="shopperStatement" areaCSS="text adyenPaymentDetailsFormElement"/>
						
					</div>
					<div class="useBoletoAction clear">
						<button class="positive useBoletoPayment" id="submitBoletoDetailsBtn" tabindex="20" type="button">
							<spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.useBoletoPayment" text="[TODO]Use Boleto Payment"/>
						</button>
					</div>
				</c:if>
			</c:if><!-- END API -->

			<c:if test="${hppEnabled}">
				<form:hidden path="useHPP"/>
				<form:hidden path="adyenPaymentBrand"/>
				<!--  HPP Payment Methods start -->
				<div class="headline clear" id="addPaymentDetailsHPPPaymentMethods"><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.hpp.payment.methods"/></div>
				<div class="savedCardDetails" style="display:inline-block;width:480px">
					<c:choose>
					    <c:when test="${not empty hppPaymentMethods}">
						    <c:forEach items="${hppPaymentMethods}" var="hppPaymentMethod" varStatus="status">
								<button style="width:150px;" tabindex="20"  type="button" onclick="payByHPP('${hppPaymentMethod.brandCode}')">
									${hppPaymentMethod.name}
								</button>
								<c:if test="${not empty hppPaymentMethod.issuers}">
								    <c:forEach items="${hppPaymentMethod.issuers}" var="issuer" varStatus="status">
											<button class="positive" style="width:150px;" tabindex="20"  type="button" onclick="payByHPP('${hppPaymentMethod.brandCode}','${issuer.issuerId}')">
												${issuer.name}
											</button>
									</c:forEach>
						   		 </c:if>
							</c:forEach>
					    </c:when>
					   	<c:otherwise>  
							<button style="width:150px;" tabindex="20"  type="button" onclick="payByHPP('','')">
								<spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.hpp.default.payment"/>   
							</button>
					   	</c:otherwise>
					  </c:choose>
				</div>
			</c:if><%--  HPP Payment Methods end --%>
			</form:form>
		</div>
	</div>
	<multi-checkout:checkoutOrderDetails cartData="${cartData}" showShipDeliveryEntries="true" showPickupDeliveryEntries="true" showTax="true"/>

	<cms:pageSlot position="SideContent" var="feature" element="div" class="span-24 side-content-slot cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	</jsp:body>
</template:page>
