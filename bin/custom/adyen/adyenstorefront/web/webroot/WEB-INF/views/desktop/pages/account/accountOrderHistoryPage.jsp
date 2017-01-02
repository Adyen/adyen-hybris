<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/desktop/action" %>

<div class="headline">
	<spring:theme code="text.account.orderHistory" text="Order History"/>
</div>
<c:if test="${not empty searchPageData.results}">
	<div class="description">
		<spring:theme code="text.account.orderHistory.viewOrders" text="View your orders"/>
	</div>
	<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="/my-account/orders?sort=${searchPageData.pagination.sort}" msgKey="text.account.orderHistory.page" numberPagesShown="${numberPagesShown}"/>
	<div class="orderList">
		<table class="orderListTable">
			<thead>
			<tr>
				<th id="header1">
					<spring:theme code="text.account.orderHistory.orderNumber" text="Order Number"/>
				</th>
				<th id="header2">
					<spring:theme code="text.account.orderHistory.orderStatus" text="Order Status"/>
				</th>
				<th id="header3">
					<spring:theme code="text.account.orderHistory.datePlaced" text="Date Placed"/>
				</th>
				<th id="header4">
					<spring:theme code="text.account.orderHistory.total" text="Total"/>
				</th>
				<th id="header5">
					<spring:theme code="text.account.orderHistory.actions" text="Actions"/>
				</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${searchPageData.results}" var="order">
				<tr>
					<td headers="header1">
						<ycommerce:testId code="orderHistory_orderNumber_link">
							${order.code}
						</ycommerce:testId>
					</td>
					<td headers="header2">
						<ycommerce:testId code="orderHistory_orderStatus_label">
							<p>
								<spring:theme code="text.account.order.status.display.${order.statusDisplay}"/>
							</p>
						</ycommerce:testId>
					</td>
					<td headers="header3">
						<ycommerce:testId code="orderHistory_orderDate_label">
							<p>
								<fmt:formatDate value="${order.placed}" dateStyle="long" timeStyle="short" type="both"/>
							</p>
						</ycommerce:testId>
					</td>
					<td headers="header4">
						<ycommerce:testId code="orderHistory_Total_links">
							<p>${order.total.formattedValue}</p>
						</ycommerce:testId>
					</td>
					<td headers="header5">
						<c:set var="orderCode" value="${order.code}" scope="request"/>
						<action:actions element="div" parentComponent="${component}"/>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
	<nav:pagination top="false" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="/my-account/orders?sort=${searchPageData.pagination.sort}" msgKey="text.account.orderHistory.page" numberPagesShown="${numberPagesShown}"/>
</c:if>
<c:if test="${empty searchPageData.results}">
	<p><spring:theme code="text.account.orderHistory.noOrders" text="You have no orders"/></p>
</c:if>