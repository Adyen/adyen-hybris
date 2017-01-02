<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="registerNewCustomerLinkHolder registerNewCustomerLinkHolderBack" data-theme="e" data-content-theme="e">
	<c:choose>
		<c:when test="${not empty accErrorMsgs}">
			<c:url value="${savedHeader}" var="loginUrl"/>
		</c:when>
		<c:otherwise>
			<c:url value="${header['referer']}" var="loginUrl"/>
				<c:set var="savedHeader" scope="session" value="${header['referer']}"/>
		</c:otherwise>
	</c:choose>
	<a href="${loginUrl}" data-role="link" data-theme="d">
		&laquo; <spring:theme code="register.back.login"/> </a>
</div>

