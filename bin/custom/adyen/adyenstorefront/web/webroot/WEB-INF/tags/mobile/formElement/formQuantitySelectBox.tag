<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="idKey" required="true" type="java.lang.String"%>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="items" required="true" type="java.util.Collection"%>
<%@ attribute name="itemValue" required="false" type="java.lang.String"%>
<%@ attribute name="itemLabel" required="false" type="java.lang.String"%>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean"%>
<%@ attribute name="labelCSS" required="false" type="java.lang.String"%>
<%@ attribute name="selectCSSClass" required="false" type="java.lang.String"%>
<%@ attribute name="skipBlank" required="false" type="java.lang.Boolean"%>
<%@ attribute name="skipBlankMessageKey" required="false" type="java.lang.String"%>
<%@ attribute name="selectedValue" required="false" type="java.lang.String"%>
<%@ attribute name="tabindex" required="false" rtexprvalue="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>

<template:errorSpanField path="${path}">
	<c:set var="idValue">
		<spring:theme code='${idKey}' />
	</c:set>
	<ycommerce:testId code="quantity_select_box_${idKey}">
		<form:select id="${idValue}" path="${path}" cssClass="${selectCSSClass}" tabindex="${tabindex}">
			<c:if test="${skipBlank == null || skipBlank == false}">
				<option value="" disabled="disabled" selected="${empty selectedValue ? 'selected' : ''}">
					<spring:theme code='${skipBlankMessageKey}' />
				</option>
			</c:if>
			<form:options items="${items}" itemValue="${not empty itemValue ? itemValue :'code'}" itemLabel="${not empty itemLabel ? itemLabel :'name'}" />
		</form:select>
	</ycommerce:testId>
</template:errorSpanField>
