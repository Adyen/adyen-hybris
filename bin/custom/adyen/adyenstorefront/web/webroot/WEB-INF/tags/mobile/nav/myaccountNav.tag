<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="link" required="false" type="java.lang.String" %>
<%@ attribute name="textcode" required="false" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="accmob-navigationHolder">
	<div class="accmob-navigationContent">
		<div class="accmobBackLink accmobBackLinkSingle">
			<c:url value="${link == null ? '/my-account' : link }" var="navLink" />
			<c:set value="${textcode == null ? 'text.myaccount' : textcode }" var="navLinkText" />
			<a href="${navLink}">
				<spring:theme code="${navLinkText}" />
			</a>
		</div>
	</div>
</div>
