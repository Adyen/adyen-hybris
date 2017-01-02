<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<h3>
	<spring:theme code="text.account.orderHistory" text="Order History"/>
</h3>
<p>

<div data-theme="c">
	<h6 class="descriptionHeadline">
		<spring:theme code="text.headline.orders" text="View your orders"/>
	</h6>
	<c:if test="${not empty searchPageData.results}">
		<p>
			<spring:theme code="text.account.orderHistory.viewOrders" text="View your orders"/>
		</p>
		<nav:pagination searchPageData="${searchPageData}" searchUrl="/my-account/orders?sort=${searchPageData.pagination.sort}" msgKey="text.account.orderHistory.mobile.page"/>
		<ul data-role="listview" data-inset="true" data-theme="d" data-dividertheme="d">
			<c:forEach items="${searchPageData.results}" var="order">
				<li>
					<c:url value="/my-account/order/${order.code}" var="myAccountOrderDetailsUrl"/>
					<a href="${myAccountOrderDetailsUrl}">
						<div class='ui-grid-a'>
							<div class='ui-block-a'>
								<H3>${order.code}</H3>

								<p>
									<fmt:formatDate pattern="dd/MM/yy" value="${order.placed}"/>
								</p>
							</div>
							<div class='ui-block-b'>
								<h3 class="continuous-text">
									<spring:theme code="text.account.order.status.display.${order.statusDisplay}"/>
								</h3>
							</div>
						</div>
					</a></li>
			</c:forEach>
		</ul>
		<nav:pagination searchPageData="${searchPageData}" searchUrl="/my-account/orders?sort=${searchPageData.pagination.sort}" msgKey="text.account.orderHistory.mobile.page"/>
	</c:if>
	<c:if test="${empty searchPageData.results}">
		<p class="emptyMessage">
			<spring:theme code="text.account.orderHistory.noOrders" text="You have no orders"/>
		</p>
	</c:if>
</div>
