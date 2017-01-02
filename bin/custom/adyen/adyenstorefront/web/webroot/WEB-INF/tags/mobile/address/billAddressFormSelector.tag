<%@ attribute name="supportedCountries" required="false" type="java.util.List"%>
<%@ attribute name="regions" required="false" type="java.util.List"%>
<%@ attribute name="country" required="false" type="java.lang.String"%>
<%@ attribute name="cancelUrl" required="false" type="java.lang.String"%>
<%@ attribute name="tabindex" required="false" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/mobile/address"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<div id="billingCountrySelector" data-address-code="${cartData.deliveryAddress.id}" data-country-iso-code="${cartData.deliveryAddress.country.isocode}" data-display-title="false">
	<input type="hidden" value="${silentOrderPageData.parameters['billTo_email']}" class="text" name="billTo_email" id="billTo_email">
	<formElement:formSelectBox idKey="address.country"
	                           labelKey="address.country"
	                           path="billTo_country"
	                           mandatory="true"
	                           skipBlank="false"
	                           skipBlankMessageKey="address.selectCountry"
	                           items="${supportedCountries}"
	                           itemValue="isocode"
	                           tabindex="${tabindex}"/>
</div>

<div id="billingAddressForm" class="billingAddressForm">
	<c:if test="${not empty country}">
		<address:billingAddressFormElements regions="${regions}"
		                                    country="${country}"
		                                    tabindex="${tabindex + 1}"/>
	</c:if>
</div>


