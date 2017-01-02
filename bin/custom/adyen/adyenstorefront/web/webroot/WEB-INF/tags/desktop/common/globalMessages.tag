<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags"%>

<%-- Information (confirmation) messages --%>
<c:if test="${not empty accConfMsgs}">
		<c:forEach items="${accConfMsgs}" var="msg">
			<div class="alert positive">
				<spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
			</div>
		</c:forEach>
</c:if>

<%-- Warning messages --%>
<c:if test="${not empty accInfoMsgs}">
		<c:forEach items="${accInfoMsgs}" var="msg">
			<div class="alert neutral">
				<spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
			</div>
		</c:forEach>
</c:if>

<%-- Error messages (includes spring validation messages)--%>
<c:if test="${not empty accErrorMsgs}">
		<c:forEach items="${accErrorMsgs}" var="msg">
			<div class="alert negative">
				<spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
			</div>
		</c:forEach>
</c:if>