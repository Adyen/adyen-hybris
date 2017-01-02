<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%-- Information (confirmation) messages --%>
<c:if test="${not empty accConfMsgs}">
	<div id="accConfMsgs" style="display: none;" data-headertext='<spring:theme code="text.headertext.conf"/>'>
		<ul class='success mFormList'>
			<c:forEach items="${accConfMsgs}" var="msg">
				<li><spring:theme code="${msg.code}" arguments="${msg.attributes}"/></li>
			</c:forEach>
		</ul>
	</div>
</c:if>
<%-- Warning messages --%>
<c:if test="${not empty accInfoMsgs}">
	<div id="accInfoMsgs" style="display: none;" data-headertext='<spring:theme code="text.headertext.info"/>'>
		<ul class="info mFormList">
			<c:forEach items="${accInfoMsgs}" var="msg">
				<li><spring:theme code="${msg.code}" arguments="${msg.attributes}"/></li>
			</c:forEach>
		</ul>
	</div>
</c:if>
<%-- Error messages --%>
<c:if test="${not empty accErrorMsgs}">
	<div id="accErrorMsgs" style="display: none;" data-headertext='<spring:theme code="text.headertext.error"/>'>
		<ul class="error mFormList">
			<c:forEach items="${accErrorMsgs}" var="msg">
				<li><spring:theme code="${msg.code}" arguments="${msg.attributes}"/></li>
			</c:forEach>
		</ul>
	</div>
</c:if>
