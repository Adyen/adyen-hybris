<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ attribute name="containerCSS" required="false" type="java.lang.String"%>

<c:if test="${not empty order.appliedOrderPromotions}">
	<div class="ui-grid-a recievedPromoFullPusher" data-theme="b" data-role="content">
		<h4 class="ui-li-heading subItemHeader">
			<spring:theme code="text.account.order.receivedPromotions" text="Received Promotions" />
		</h4>
	</div>
	<div class="ui-grid-a" data-theme="b">
		<ul class="cart-promotions itemPromotionBox">
			<c:forEach items="${order.appliedOrderPromotions}" var="promotion">
				<li class="cart-promotions-applied">${promotion.description}</li>
			</c:forEach>
		</ul>
	</div>
</c:if>
