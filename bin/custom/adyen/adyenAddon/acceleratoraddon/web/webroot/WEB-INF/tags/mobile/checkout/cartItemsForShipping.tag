<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="showPotentialPromotions" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showAllItems" type="java.lang.Boolean" %>

<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/mobile/checkout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ul class="mFormList itemsList productItemListDetailsHolder " numberOfItems="1" liFilterClass='cartLi'>
	<c:forEach items="${cartData.entries}" var="entry">
		<c:if test="${entry.deliveryPointOfService == null}">
			<checkout:cartItem cartData="${cartData}" entry="${entry}" />
		</c:if>
	</c:forEach>
</ul>
