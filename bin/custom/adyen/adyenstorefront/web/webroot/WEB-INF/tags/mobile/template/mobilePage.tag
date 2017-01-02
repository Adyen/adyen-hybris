<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/mobile/common/header"%>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/mobile/common/footer"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="pageId" required="true"%>
<%@ attribute name="dataSearchQuery" required="false"%>
<%@ attribute name="header" required="false" fragment="true"%>
<%@ attribute name="footer" required="false" fragment="true"%>

<div id="${pageId}" data-searchquery="${dataSearchQuery}" data-role="page" data-theme="d" data-url="<c:url value="/"/>">
	<div data-role="header" data-position="fixed" class="ui-bar" data-position="inline">
		<jsp:invoke fragment="header"/>
	</div>
	<div data-role="content" data-inset="true" data-theme="d">
		<jsp:doBody/>
	</div>
	<div data-role="footer" data-position="fixed" class="ui-bar">
		<jsp:invoke fragment="footer"/>
	</div>
</div>
