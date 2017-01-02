<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/mobile/order" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav" %>
<div class="item_container_holder" data-content-theme="d" data-theme="e">
	<ul class="mFormList" data-theme="c" data-content-theme="c">
		<li>
			<div class="ui-grid-a right">
				<c:url value="/my-account/orders" var="ordersUrl"/>
				<a href="${ordersUrl}" data-role="button" data-theme="d" data-icon="arrow-l" class="ignoreIcon">
					<spring:theme code="text.account.orderHistory" text="Order History"/>
				</a>
			</div>
		</li>
	</ul>
</div>

