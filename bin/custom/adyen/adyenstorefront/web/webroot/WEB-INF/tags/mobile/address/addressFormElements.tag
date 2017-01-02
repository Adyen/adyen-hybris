<%@ attribute name="regions" required="true" type="java.util.List"%>
<%@ attribute name="country" required="false" type="java.lang.String"%>
<%@ attribute name="tabIndex" required="false" type="java.lang.Integer"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:choose>
	<c:when test="${country == 'US'}">
		<formElement:formSelectBox idKey="address.title" labelKey="address.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect" items="${titles}" selectedValue="${addressForm.titleCode}"/>
		<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="line1" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="line2" inputCSS="text" mandatory="false"/>
		<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="townCity" inputCSS="text" mandatory="true"/>
		<formElement:formSelectBox idKey="address.region" labelKey="address.state" path="regionIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectState" items="${regions}" itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}" selectedValue="${addressForm.regionIso}"/>
		<formElement:formInputBox idKey="address.postcode" labelKey="address.zipcode" path="postcode" inputCSS="text" mandatory="true"/>
	</c:when>
	<c:when test="${country == 'CA'}">
		<formElement:formSelectBox idKey="address.title" labelKey="address.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect" items="${titles}" selectedValue="${addressForm.titleCode}"/>
		<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="line1" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="line2" inputCSS="text" mandatory="false"/>
		<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="townCity" inputCSS="text" mandatory="true"/>
		<formElement:formSelectBox idKey="address.region" labelKey="address.province" path="regionIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectProvince" items="${regions}" itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}" selectedValue="${addressForm.regionIso}"/>
		<formElement:formInputBox idKey="address.postcode" labelKey="address.postalcode" path="postcode" inputCSS="text" mandatory="true"/>
	</c:when>
	<c:when test="${country == 'CN'}">
		<formElement:formInputBox idKey="address.postcode" labelKey="address.postalcode" path="postcode" inputCSS="text" mandatory="true"/>
		<formElement:formSelectBox idKey="address.region" labelKey="address.province" path="regionIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectProvince" items="${regions}" itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}" selectedValue="${addressForm.regionIso}"/>
		<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="townCity" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.line1" labelKey="address.street" path="line1" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.line2" labelKey="address.building" path="line2" inputCSS="text" mandatory="false"/>
		<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName" inputCSS="text" mandatory="true"/>
		<formElement:formSelectBox idKey="address.title" labelKey="address.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect" items="${titles}" selectedValue="${addressForm.titleCode}"/>
	</c:when>
	<c:when test="${country == 'JP'}">
		<formElement:formSelectBox idKey="address.title" labelKey="address.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect" items="${titles}" selectedValue="${addressForm.titleCode}"/>
		<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.line1" labelKey="address.furtherSubarea" path="line1" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.line2" labelKey="address.subarea" path="line2" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.townCity" labelKey="address.townJP" path="townCity" inputCSS="text" mandatory="true"/>
		<formElement:formSelectBox idKey="address.region" labelKey="address.prefecture" path="regionIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectPrefecture" items="${regions}" itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}" selectedValue="${addressForm.regionIso}"/>
		<formElement:formInputBox idKey="address.postalcode" labelKey="address.postcode" path="postcode" inputCSS="text" mandatory="true"/>
	</c:when>
	<c:otherwise>
		<formElement:formSelectBox idKey="address.title" labelKey="address.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect" items="${titles}" selectedValue="${addressForm.titleCode}"/>
		<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="line1" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="line2" inputCSS="text" mandatory="false"/>
		<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="townCity" inputCSS="text" mandatory="true"/>
		<formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="postcode" inputCSS="text" mandatory="true"/>
	</c:otherwise>
</c:choose>
