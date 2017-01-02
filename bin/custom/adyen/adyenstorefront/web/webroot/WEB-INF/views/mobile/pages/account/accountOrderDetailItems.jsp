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

<div class="checkoutOverviewItems accountOrderItems">
	<c:set var="headingWasShown" value="false"/>
	<c:forEach items="${orderData.consignments}" var="consignment">
		<c:if test="${consignment.status.code eq 'WAITING' or consignment.status.code eq 'PICKPACK' or consignment.status.code eq 'READY'}">
			<c:if test="${not headingWasShown}">
				<c:set var="headingWasShown" value="true"/>
				<h1>
					<spring:theme code="text.account.order.title.inProgressItems"/>
				</h1>
			</c:if>
			<div class="ui-grid-a productItemListHolder">
				<order:accountOrderDetailsItem order="${orderData}" consignment="${consignment}" inProgress="true"/>
			</div>
		</c:if>
	</c:forEach>

	<c:if test="${not empty orderData.unconsignedEntries}">
		<div class="productItemListHolder productItemListHolder-ne">
			<c:forEach items="${orderData.unconsignedEntries}" var="entry">
				<order:accountOrderEntry entry="${entry}"/>
			</c:forEach>
		</div>
	</c:if>

	<c:forEach items="${orderData.consignments}" var="consignment">
		<c:if test="${consignment.status.code ne 'WAITING' and consignment.status.code ne 'PICKPACK' and consignment.status.code ne 'READY'}">
			<div class="productItemListHolder productItemListHolder-ne">
				<order:accountOrderDetailsItem order="${orderData}" consignment="${consignment}"/>
			</div>
		</c:if>
	</c:forEach>
</div>

