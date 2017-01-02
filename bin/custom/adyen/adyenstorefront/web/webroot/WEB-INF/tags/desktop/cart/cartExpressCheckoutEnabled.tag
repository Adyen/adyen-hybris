<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authorize access="isFullyAuthenticated()">
	<c:if test="${expressCheckoutAllowed}">
		<div class="expressCheckoutCheckbox">
			<label for="expressCheckoutCheckbox">
				<input id="expressCheckoutCheckbox" type="checkbox" data-express-checkout-url="<c:url value='/checkout/multi/express'/>" class="form  doExpressCheckout"/>
				<spring:theme text="I would like to Express checkout" code="cart.expresscheckout.checkbox"/>
			</label>
		</div>
	</c:if>
</sec:authorize>