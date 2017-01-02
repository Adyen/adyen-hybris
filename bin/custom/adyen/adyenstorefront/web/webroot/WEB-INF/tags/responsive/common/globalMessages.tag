<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags"%>

<c:if test="${(not empty accConfMsgs) || (not empty accInfoMsgs) || (not empty accErrorMsgs)}">
	<div class="global-alerts">
		
		<%-- Information (confirmation) messages --%>
		<c:if test="${not empty accConfMsgs}">
			<c:forEach items="${accConfMsgs}" var="msg">
				<div class="alert alert-info alert-dismissable">
					<button class="close" aria-hidden="true" data-dismiss="alert" type="button">&times;</button>
					<spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
				</div>
			</c:forEach>
		</c:if>

		<%-- Warning messages --%>
		<c:if test="${not empty accInfoMsgs}">
			<c:forEach items="${accInfoMsgs}" var="msg">
				<div class="alert alert-warning alert-dismissable">
					<button class="close" aria-hidden="true" data-dismiss="alert" type="button">&times;</button>
					<spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
				</div>
			</c:forEach>
		</c:if>

		<%-- Error messages (includes spring validation messages)--%>
		<c:if test="${not empty accErrorMsgs}">
			<c:forEach items="${accErrorMsgs}" var="msg">
				<div class="alert alert-danger alert-dismissable">
					<button class="close" aria-hidden="true" data-dismiss="alert" type="button">&times;</button>
					<spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
				</div>
			</c:forEach>
		</c:if>

	</div>
</c:if>