<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<sec:authorize access="isFullyAuthenticated()">
	<cart:cartExpressCheckoutEnabled/>
</sec:authorize>

<div class="ui-grid-a">
	<div class="ui-block-a">
		<a href="${continueShoppingUrl}" data-theme="d" data-role="button" data-icon="arrow-l">
			<spring:theme text="Continue Shopping" code="cart.page.shop" />
		</a>
	</div>
	<div class="ui-block-b">
		<a href="${checkoutUrl}" id="checkoutButton" data-role="button" data-theme="b" data-icon="arrow-r" data-iconpos="right" class="continueCheckout">
			<spring:theme code="checkout.checkout" />
		</a>
	</div>
</div>
