<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="groupData" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryGroupData" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="showPotentialPromotions" required="false" type="java.lang.Boolean" %>

<%@ taglib prefix="checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/mobile/checkout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:forEach items="${groupData.entries}" var="entry">
	<checkout:cartItem cartData="${cartData}" entry="${entry}" showPotentialPromotions="${showPotentialPromotions}" />
</c:forEach>
