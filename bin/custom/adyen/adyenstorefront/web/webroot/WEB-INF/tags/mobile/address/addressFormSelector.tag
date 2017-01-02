<%@ attribute name="supportedCountries" required="true" type="java.util.List"%>
<%@ attribute name="regions" required="true" type="java.util.List"%>
<%@ attribute name="country" required="false" type="java.lang.String"%>
<%@ attribute name="cancelUrl" required="false" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/mobile/address"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<form:form method="post" commandName="addressForm">
	<form:hidden path="addressId" class="add_edit_delivery_address_id" status="${not empty suggestedAddresses ? 'hasSuggestedAddresses' : ''}"/>
	<input type="hidden" name="bill_state" id="address.billstate"/>

	<div id="countrySelector" data-address-code="${addressData.id}" data-country-iso-code="${addressData.country.isocode}">
		<formElement:formSelectBox idKey="address.country"
		                           labelKey="address.country"
		                           path="countryIso"
		                           mandatory="true"
		                           skipBlank="false"
		                           skipBlankMessageKey="address.selectCountry"
		                           items="${supportedCountries}"
		                           itemValue="isocode"
		                           selectedValue="${addressForm.countryIso}"/>
	</div>
	<div id="i18nAddressForm" class="i18nAddressForm">
		<c:if test="${not empty country}">
			<address:addressFormElements regions="${regions}"
			                             country="${country}"/>
		</c:if>
	</div>
	<div id="addressform_button_panel" style="display: none">
		<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
			<c:choose>
				<c:when test="${showSaveToAddressBook}">
					<formElement:formCheckbox idKey="saveAddressInMyAddressBook" labelKey="checkout.summary.deliveryAddress.saveAddressInMyAddressBook" path="saveInAddressBook" inputCSS="add-address-left-input" labelCSS="add-address-left-label" mandatory="false"/>
				</c:when>
				<c:when test="${not addressBookEmpty && not isDefaultAddress}">
					<formElement:formCheckbox idKey="defaultAddress" labelKey="address.default" path="defaultAddress"
					                          inputCSS="add-address-left-input" labelCSS="add-address-left-label" mandatory="false"/>
				</c:when>
			</c:choose>
		</sec:authorize>
			<span style="display: block; clear: both;">
				<c:choose>
					<c:when test="${edit eq true}">
						<ycommerce:testId code="multicheckout_saveAddress_button">
							<button class="form change_address_button" >
								<spring:theme code="checkout.multi.saveAddress" text="Save address"/>
							</button>
						</ycommerce:testId>
					</c:when>
					<c:otherwise>
						<ycommerce:testId code="multicheckout_saveAddress_button">
							<button class="form change_address_button" type="submit" data-theme="b">
								<spring:theme code="checkout.checkout.multi.next" text="Next &raquo;"/>
							</button>
						</ycommerce:testId>
					</c:otherwise>
				</c:choose>

				<c:if test="${not noAddress}">
					<ycommerce:testId code="multicheckout_cancel_button">
						<c:url value="${cancelUrl}" var="cancel"/>
							<button class="form" type="button" onclick="window.location='${cancel}'" data-theme="b"><spring:theme code="checkout.multi.cancel" text="Cancel"/></button>
					</ycommerce:testId>
				</c:if>
			</span>
	</div>
</form:form>
