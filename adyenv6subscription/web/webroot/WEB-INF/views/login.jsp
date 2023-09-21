<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Login</title>
    <c:set var="cssUrl"><c:url value="/static/adyenv6subscription-webapp.css"/></c:set>
    <link rel="stylesheet" href="${fn:escapeXml(cssUrl)}" type="text/css" media="screen, projection"/>
</head>
<body>
<div class="container">
    <c:set var="actionUrl"><c:url value="/j_spring_security_check"/></c:set>
    <form action="${fn:escapeXml(actionUrl)}" method="POST">
        <div id="logincontrols" class="logincontrols">
            <div id="loginErrors">&nbsp;
                <c:if test="${not empty param.login_error}">
                    <c:out value="Login failed: ${SPRING_SECURITY_LAST_EXCEPTION.message}"/>
                </c:if>
            </div>

            <fieldset class="login-form">
                <p>
                    <input type="text" name="j_username" placeholder="Username" value="admin"/>
                </p>
                <p>
                    <input type="password" name="j_password" placeholder="Password" value=""/>
                </p>
                <p>
                    <label><input type="checkbox" name="_spring_security_remember_me" class="checkbox"
                                  id="_spring_security_remember_me"/> Remember Login</label>
                </p>
                <p>
                    <button type="submit" class="button" autofocus>login</button>
                    <sec:csrfInput/>
                </p>
            </fieldset>
        </div>
    </form>
</div>
</body>
</html>
