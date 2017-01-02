<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ attribute name="path" required="true" rtexprvalue="true"%>
<%@ attribute name="errorPath" required="false" rtexprvalue="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<spring:bind path="${not empty errorPath ? errorPath : path}">
	<c:choose>
		<c:when test="${not empty status.errorMessages}">
		<span class="form_field_error">
			<jsp:doBody/>
		</span>
		</c:when>
		<c:otherwise>
			<jsp:doBody/>
		</c:otherwise>
	</c:choose>
</spring:bind>
