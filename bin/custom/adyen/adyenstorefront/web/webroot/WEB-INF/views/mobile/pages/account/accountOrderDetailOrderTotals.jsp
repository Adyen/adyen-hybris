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

<h4>
	<spring:theme code="text.account.order.orderNumberShort" text="Order #: {0}" arguments="${orderData.code}"/>
</h4>
<ul class="mFormList">
	<li>
		<spring:theme code="text.account.order.status.display.${orderData.statusDisplay}" var="orderStatus"/>
		<spring:theme code="text.account.order.orderStatus" arguments="${orderStatus}"/>
	</li>
	<li>
		<spring:theme code="text.account.order.orderPlaced" text="Placed on {0}" arguments="${orderData.created}"/>
	</li>
</ul>

<div data-theme="b">
	<order:receivedPromotions order="${orderData}"/>
</div>

<div data-theme="b">
	<order:orderTotalsItem order="${orderData}" hideHeading="true"/>
</div>
