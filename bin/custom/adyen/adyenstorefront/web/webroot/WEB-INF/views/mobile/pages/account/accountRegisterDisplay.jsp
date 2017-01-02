<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/mobile/user"%>

<div data-role="content">
	<c:url value="/register/newcustomer" var="submitAction" />
	<user:register actionNameKey="register.submit" action="${submitAction}" />
</div>

