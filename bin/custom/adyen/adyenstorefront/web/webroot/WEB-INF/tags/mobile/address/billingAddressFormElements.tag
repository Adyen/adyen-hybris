<%@ attribute name="regions" required="true" type="java.util.List"%>
<%@ attribute name="country" required="false" type="java.lang.String"%>
<%@ attribute name="tabindex" required="false" type="java.lang.Integer"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:choose>
	<c:when test="${country == 'US'}">
		<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="billTo_firstName" inputCSS="text" mandatory="true" tabindex="${tabindex + 1}"/>
		<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="billTo_lastName" inputCSS="text" mandatory="true" tabindex="${tabindex + 2}"/>
		<formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="billTo_street1" inputCSS="text" mandatory="true" tabindex="${tabindex + 3}"/>
		<formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="billTo_street2" inputCSS="text" mandatory="false" tabindex="${tabindex + 4}"/>
		<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="billTo_city" inputCSS="text" mandatory="true" tabindex="${tabindex + 5}"/>
		<formElement:formSelectBox idKey="address.region" labelKey="address.state" path="billTo_state" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectState" items="${regions}" itemValue="isocodeShort" tabindex="${tabindex + 6}" />
		<formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="billTo_postalCode" inputCSS="text" mandatory="true" tabindex="${tabindex + 7}"/>
		</c:when>
	<c:when test="${country == 'CA'}">
		<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="billTo_firstName" inputCSS="text" mandatory="true" tabindex="${tabindex + 1}"/>
		<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="billTo_lastName" inputCSS="text" mandatory="true" tabindex="${tabindex + 2}"/>
		<formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="billTo_street1" inputCSS="text" mandatory="true" tabindex="${tabindex + 3}"/>
		<formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="billTo_street2" inputCSS="text" mandatory="false" tabindex="${tabindex + 4}"/>
		<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="billTo_city" inputCSS="text" mandatory="true" tabindex="${tabindex + 5}"/>
		<formElement:formSelectBox idKey="address.region" labelKey="address.province" path="billTo_state" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectProvince" items="${regions}" itemValue="isocodeShort" tabindex="${tabindex + 6}" />
		<formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="billTo_postalCode" inputCSS="text" mandatory="true" tabindex="${tabindex + 7}"/>
	</c:when>
	<c:when test="${country == 'CN'}">
		<formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="billTo_postalCode" inputCSS="text" mandatory="true" tabindex="${tabindex + 1}"/>
		<formElement:formSelectBox idKey="address.region" labelKey="address.province" path="billTo_state" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectProvince" items="${regions}" itemValue="isocodeShort" tabindex="${tabindex + 2}" />
		<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="billTo_city" inputCSS="text" mandatory="true" tabindex="${tabindex + 3}"/>
		<formElement:formInputBox idKey="address.line2" labelKey="address.district_and_street" path="billTo_street2" inputCSS="text" mandatory="true" tabindex="${tabindex + 4}"/>
		<formElement:formInputBox idKey="address.line1" labelKey="address.building_and_room" path="billTo_street1" inputCSS="text" mandatory="false" tabindex="${tabindex + 5}"/>
		<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="billTo_lastName" inputCSS="text" mandatory="true" tabindex="${tabindex + 6}"/>
		<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="billTo_firstName" inputCSS="text" mandatory="true" tabindex="${tabindex + 7}"/>
		</c:when>
	<c:when test="${country == 'JP'}">
		<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="billTo_lastName" inputCSS="text" mandatory="true" tabindex="${tabindex + 1}"/>
		<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="billTo_firstName" inputCSS="text" mandatory="true" tabindex="${tabindex + 2}"/>
		<formElement:formInputBox idKey="address.postcode" labelKey="address.postcodeJP" path="billTo_postalCode" inputCSS="text" mandatory="true" tabindex="${tabindex + 3}"/>
		<formElement:formSelectBox idKey="address.region" labelKey="address.prefecture" path="billTo_state" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectPrefecture" items="${regions}" itemValue="isocodeShort" tabindex="${tabindex + 4}" />
		<formElement:formInputBox idKey="address.townCity" labelKey="address.townJP" path="billTo_city" inputCSS="text" mandatory="true" tabindex="${tabindex + 5}"/>
		<formElement:formInputBox idKey="address.line2" labelKey="address.subarea" path="billTo_street2" inputCSS="text" mandatory="true" tabindex="${tabindex + 6}"/>
		<formElement:formInputBox idKey="address.line1" labelKey="address.furtherSubarea" path="billTo_street1" inputCSS="text" mandatory="true" tabindex="${tabindex + 7}"/>
		</c:when>
	<c:otherwise>
		<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="billTo_firstName" inputCSS="text" mandatory="true" tabindex="${tabindex + 1}"/>
		<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="billTo_lastName" inputCSS="text" mandatory="true" tabindex="${tabindex + 2}"/>
		<formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="billTo_street1" inputCSS="text" mandatory="true" tabindex="${tabindex + 3}"/>
		<formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="billTo_street2" inputCSS="text" mandatory="false" tabindex="${tabindex + 4}"/>
		<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="billTo_city" inputCSS="text" mandatory="true" tabindex="${tabindex + 5}"/>
		<formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="billTo_postalCode" inputCSS="text" mandatory="true" tabindex="${tabindex + 6}"/>
	</c:otherwise>
</c:choose>

