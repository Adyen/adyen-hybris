<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:url value="/cart" var="basketUrl"/>
<h6 class="descriptionHeadline">Click here to get to your cart</h6>	

<%-- this code should get rendered correctly by jquery mobil after a ajax call --%>
<%--<a href="${basketUrl}" id="top-nav-bar-cart-2" class="ui-btn-right" data-role="button" data-theme="b" data-iconpos="left" data-icon="cart" title="<spring:theme code="text.cart"/>">
	<ycommerce:testId code="miniCart_items_label">
		&nbsp;<spring:theme text="items" code="cart.count" arguments="${totalItems}" />
	</ycommerce:testId>
</a>
--%>
<%-- this is only the rendered result, it looks correct, but should be replaced with the above code --%>
<a title="Cart" class="ui-btn-right ui-btn ui-shadow ui-btn-corner-all ui-btn-icon-left ui-btn-up-b" id="top-nav-bar-cart" href="${basketUrl}">
	<span class="ui-btn-inner ui-btn-corner-all">
		<span class="ui-btn-text">
			&nbsp;<spring:theme text="items" code="cart.count" arguments="${totalItems}" />
		</span>
		<span class="ui-icon ui-icon-cart ui-icon-shadow">&nbsp;</span>
	</span>
</a>