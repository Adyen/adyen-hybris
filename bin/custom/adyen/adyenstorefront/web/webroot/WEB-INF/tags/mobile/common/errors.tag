<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<spring:bind path="*">
	<c:if test="${status.errors.errorCount > 0}">
		<input type="hidden" id="form-errors" data-message='<form:errors element="div" path="*" cssClass="error" />'
			 data-headertext='<spring:theme code="text.headertext.error" />' />
	</c:if>
	<c:if test="${status.errors.errorCount == 0}">
		<%-- Error messages (includes spring validation messages) --%>
		<c:if test="${not empty accErrorMsgs}">
			<div id="accErrorMsgs" style="display: none;" data-headertext='<spring:theme code="text.headertext.error"/>'>
				<ul class="error mFormList">
					<c:forEach items="${accErrorMsgs}" var="errorMsg">
						<li><spring:theme code="${errorMsg}" /></li>
					</c:forEach>
				</ul>
			</div>
		</c:if>
	</c:if>
</spring:bind>
