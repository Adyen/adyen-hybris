<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>

<c:url value="/login/register" var="registerActionUrl" />
<user:register actionNameKey="register.submit" action="${registerActionUrl}"/>