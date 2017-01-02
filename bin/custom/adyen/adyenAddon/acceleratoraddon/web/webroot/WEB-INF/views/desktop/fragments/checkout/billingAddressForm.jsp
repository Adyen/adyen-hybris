<%@ taglib prefix="address" tagdir="/WEB-INF/tags/desktop/address"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:if test="${not empty country}">
	<form:form commandName="sopPaymentDetailsForm">
		<address:billingAddressFormElements regions="${regions}"
		                             country="${country}" tabindex="12"/>
	</form:form>
</c:if>
