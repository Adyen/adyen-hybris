<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="idKey" required="true" type="java.lang.String"%>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean"%>
<%@ attribute name="labelCSS" required="false" type="java.lang.String"%>
<%@ attribute name="inputCSS" required="false" type="java.lang.String"%>
<%@ attribute name="errorPath" required="false" type="java.lang.String"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>



<template:errorSpanField path="${path}" errorPath="${errorPath}">
	<ycommerce:testId code="LoginPage_Item_${idKey}">
	
			<label class="control-label ${labelCSS}" for="${idKey}"> <spring:theme
					code="${labelKey}" /> <c:if
					test="${mandatory != null && mandatory == false}">
					<spring:theme code="login.optional" />
				</c:if>
			</label>
			<form:password cssClass="${inputCSS}" id="${idKey}" path="${path}" />
	</ycommerce:testId>
</template:errorSpanField>
