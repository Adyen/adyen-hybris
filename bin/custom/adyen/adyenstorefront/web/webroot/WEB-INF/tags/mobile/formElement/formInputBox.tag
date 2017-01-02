<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="idKey" required="true" type="java.lang.String"%>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="mandatory" required="false" type="java.lang.Boolean"%>
<%@ attribute name="labelCSS" required="false" type="java.lang.String"%>
<%@ attribute name="inputCSS" required="false" type="java.lang.String"%>
<%@ attribute name="tabindex" required="false" rtexprvalue="true"%>
<%@ attribute name="autocomplete" required="false" type="java.lang.String" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>

<template:errorSpanField path="${path}">
	<ycommerce:testId code="LoginPage_Item_${idKey}">
		<div data-role="fieldcontain" data-theme="b">
			<label class="${labelCSS}" for="${idKey}"> <spring:theme code="${labelKey}" />
				<c:if test="${mandatory != null && mandatory == true}">
					<span class="mandatory"> <spring:theme code="login.required" var="loginrequiredText" />
						<img width="5" height="6" alt="${loginrequiredText}" title="${loginrequiredText}" src="${commonResourcePath}/images/mandatory.gif"/>
					</span>
				</c:if>
			</label>
			<form:input cssClass="${inputCSS}" id="${idKey}" path="${path}" tabindex="${tabindex}" autocomplete="${autocomplete}" />
		</div>
	</ycommerce:testId>
</template:errorSpanField>