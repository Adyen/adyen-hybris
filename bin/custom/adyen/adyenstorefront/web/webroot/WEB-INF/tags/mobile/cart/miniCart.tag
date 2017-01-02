<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:url value="/cart" var="basketUrl" />
<div id="minicart_data">
	<h6 class="descriptionHeadline">Click here to get to your cart</h6>
	<a href="${basketUrl}" id="top-nav-bar-cart" class="ui-btn-right" data-role="button" data-theme="b" data-iconpos="left" data-icon="cart" title="<spring:theme code="text.cart"/>">
		<ycommerce:testId code="miniCart_items_label">
			<spring:theme text="items" code="cart.count" arguments="${totalItems}" />
		</ycommerce:testId>
	</a>
</div>
