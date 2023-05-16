<%@ attribute name="regions" required="true" type="java.util.List"%>
<%@ attribute name="country" required="false" type="java.lang.String"%>
<%@ attribute name="tabIndex" required="false" type="java.lang.Integer"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:choose>
	<c:when test="${country == 'US'}">
		<formElement:formSelectBoxDefaultEnabled idKey="address.title" labelKey="address.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.none" items="${titles}" selectedValue="${addressForm.titleCode}" selectCSSClass="form-control"/>
		<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="line1" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="line2" inputCSS="form-control" mandatory="false"/>
		<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="townCity" inputCSS="form-control" mandatory="true" />
		<formElement:formSelectBox idKey="address.region" labelKey="address.state" path="regionIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.state" items="${regions}" itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}" selectedValue="${addressForm.regionIso}" selectCSSClass="form-control"/>
		<formElement:formInputBox idKey="address.postcode" labelKey="address.zipcode" path="postcode" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.phone" labelKey="address.phone" path="phone" inputCSS="form-control" mandatory="false" />
	</c:when>
	<c:when test="${country == 'CA'}">
		<formElement:formSelectBoxDefaultEnabled idKey="address.title" labelKey="address.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.none" items="${titles}" selectedValue="${addressForm.titleCode}" selectCSSClass="form-control"/>
		<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="line1" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="line2" inputCSS="form-control" mandatory="false"/>
		<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="townCity" inputCSS="form-control" mandatory="true" />
		<formElement:formSelectBox idKey="address.region" labelKey="address.province" path="regionIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.state" items="${regions}" itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}" selectedValue="${addressForm.regionIso}" selectCSSClass="form-control"/>
		<formElement:formInputBox idKey="address.postcode" labelKey="address.postalcode" path="postcode" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.phone" labelKey="address.phone" path="phone" inputCSS="form-control" mandatory="false" />
	</c:when>
	<c:when test="${country == 'CN'}">
		<formElement:formSelectBox idKey="address.region" labelKey="address.province" path="regionIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectProvince" items="${regions}" itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}" selectedValue="${addressForm.regionIso}" selectCSSClass="form-control"/>
		<formElement:formSelectBox idKey="address.townCity" labelKey="address.townCity" path="cityIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectCity" items="${cities}" itemValue="${'code'}" selectedValue="${addressForm.cityIso}" selectCSSClass="form-control"/>
		<formElement:formSelectBox idKey="address.district" labelKey="address.district" path="districtIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectDistrict" items="${districts}" itemValue="${'code'}" selectedValue="${addressForm.districtIso}" selectCSSClass="form-control"/>
		<formElement:formInputBox idKey="address.line1" labelKey="address.street" path="line1" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.line2" labelKey="address.building" path="line2" inputCSS="form-control" mandatory="false"/>
		<formElement:formInputBox idKey="address.fullname" labelKey="address.fullname" path="fullname" inputCSS="form-control" mandatory="true" />
		<formElement:formSelectBoxDefaultEnabled idKey="address.title" labelKey="address.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.none" items="${titles}" selectedValue="${addressForm.titleCode}" selectCSSClass="form-control"/>
		<formElement:formInputBox idKey="address.postcode" labelKey="address.postalcode" path="postcode" inputCSS="form-control" mandatory="false" />
		<formElement:formInputBox idKey="address.cellphone" labelKey="address.cellphone" path="cellphone" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.phone" labelKey="address.phone" path="phone" inputCSS="form-control" mandatory="false" />
	</c:when>
	<c:when test="${country == 'JP'}">
		<formElement:formSelectBoxDefaultEnabled idKey="address.title" labelKey="address.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.none" items="${titles}" selectedValue="${addressForm.titleCode}" selectCSSClass="form-control"/>
		<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.line1" labelKey="address.furtherSubarea" path="line1" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.line2" labelKey="address.subarea" path="line2" inputCSS="form-control" mandatory="true"/>
		<formElement:formInputBox idKey="address.townCity" labelKey="address.townJP" path="townCity" inputCSS="form-control" mandatory="true" />
		<formElement:formSelectBox idKey="address.region" labelKey="address.prefecture" path="regionIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectPrefecture" items="${regions}" itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}" selectedValue="${addressForm.regionIso}" selectCSSClass="form-control"/>
		<formElement:formInputBox idKey="address.postalcode" labelKey="address.postcode" path="postcode" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.phone" labelKey="address.phone" path="phone" inputCSS="form-control" mandatory="false" />
	</c:when>
	<c:when test="${country == 'BR'}">
		<formElement:formSelectBoxDefaultEnabled idKey="address.title" labelKey="address.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.none" items="${titles}" selectedValue="${addressForm.titleCode}" selectCSSClass="form-control"/>
		<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="line1" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.line2" labelKey="address.line2BR" path="line2" inputCSS="form-control" mandatory="true"/>
		<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="townCity" inputCSS="form-control" mandatory="true" />
		<formElement:formSelectBox idKey="address.region" labelKey="address.state" path="regionIso" mandatory="true" skipBlank="false" skipBlankMessageKey="address.state" items="${regions}" itemValue="${useShortRegionIso ? 'isocodeShort' : 'isocode'}" selectedValue="${addressForm.regionIso}" selectCSSClass="form-control"/>
		<formElement:formInputBox idKey="address.postcode" labelKey="address.zipcode" path="postcode" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.phone" labelKey="address.phone" path="phone" inputCSS="form-control" mandatory="false" />
	</c:when>
	<c:when test="${country == 'IN'}">
		<formElement:formSelectBoxDefaultEnabled idKey="address.title" labelKey="address.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.none" items="${titles}" selectedValue="${addressForm.titleCode}" selectCSSClass="form-control"/>
		<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="line1" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="line2" inputCSS="form-control" mandatory="false"/>
		<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="townCity" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="postcode" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.phone" labelKey="address.phone" path="phone" inputCSS="form-control" mandatory="true" />
	</c:when>
	<c:otherwise>
		<formElement:formSelectBoxDefaultEnabled idKey="address.title" labelKey="address.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.none" items="${titles}" selectedValue="${addressForm.titleCode}" selectCSSClass="form-control"/>
		<formElement:formInputBox idKey="address.firstName" labelKey="address.firstName" path="firstName" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="lastName" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="line1" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="line2" inputCSS="form-control" mandatory="false"/>
		<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="townCity" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="postcode" inputCSS="form-control" mandatory="true" />
		<formElement:formInputBox idKey="address.phone" labelKey="address.phone" path="phone" inputCSS="form-control" mandatory="true" />
	</c:otherwise>
</c:choose>

