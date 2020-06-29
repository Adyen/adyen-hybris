<%@ taglib prefix="address" tagdir="/WEB-INF/tags/addons/adyenv6b2ccheckoutaddon/responsive"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form:form modelAttribute="adyenPaymentForm">
	<address:billingAddressFormElements regions="${regions}"
										country="${country}" tabindex="12"/>
</form:form>
