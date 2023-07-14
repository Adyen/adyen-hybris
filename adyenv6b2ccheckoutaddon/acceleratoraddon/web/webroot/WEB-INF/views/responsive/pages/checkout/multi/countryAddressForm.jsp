<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/addons/adyenv6b2ccheckoutaddon/responsive" %>

<c:if test="${not empty country}">
	<form:form modelAttribute="addressForm">
		<address:addressFormElements regions="${regions}"
		                             country="${country}"/>
	</form:form>
</c:if>

