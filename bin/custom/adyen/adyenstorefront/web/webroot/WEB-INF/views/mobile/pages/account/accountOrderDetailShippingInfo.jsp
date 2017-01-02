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

<div data-theme="d">
	<h3 class="summaryHeadline">
		<spring:theme code="text.account.order.summary" text="A summary of your order is below:"/>
	</h3>
</div>

<c:if test="${fn:length(orderData.deliveryOrderGroups) gt 0}">
	<order:addressItem address="${orderData.deliveryAddress}" type="delivery"/>
</c:if>
<order:deliveryMethodItem order="${orderData}"/>
<order:paymentMethodItem order="${orderData}"/>
