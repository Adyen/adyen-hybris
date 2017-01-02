<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>

<sec:authorize access="isFullyAuthenticated()">
<c:if test="${expressCheckoutAllowed}">
	<div class="span-8 expressCheckoutBox">
		<div class="headline"><spring:theme text="Express Checkout" code="text.expresscheckout.header"/></div>
		<div class="description"><spring:theme text="Benefit from a faster checkout by:" code="text.expresscheckout.title"/></div>
		<ul>
			<li><spring:theme text="setting a default Delivery Address in your account" code="text.expresscheckout.line1"/></li>
			<li><spring:theme text="setting a default Payment Details in your account" code="text.expresscheckout.line2"/></li>
			<li><spring:theme text="a default shipping method is used" code="text.expresscheckout.line3"/></li>
		</ul>
		<cart:cartExpressCheckoutEnabled/>
	</div>
</c:if>
</sec:authorize>