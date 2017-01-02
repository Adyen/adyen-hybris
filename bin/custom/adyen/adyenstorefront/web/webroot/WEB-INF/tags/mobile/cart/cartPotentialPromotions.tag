<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:if test="${not empty cartData.potentialOrderPromotions}">
	<div class="ui-grid-a specialPromotionBox" data-theme="b" data-role="content">
		<div class="ui-grid-a" data-theme="b">
			<span class="iconHolder"></span>
			<h2>
				<spring:theme code="basket.potential.promotions" />
			</h2>
		</div>
		<div class="ui-grid-a" data-theme="b">
			<ycommerce:testId code="potentialPromotions_promotions_labels">
				<ul>
					<c:forEach items="${cartData.potentialOrderPromotions}" var="promotion">
						<li class="cart-promotions-potential">${promotion.description}</li>
					</c:forEach>
				</ul>
			</ycommerce:testId>
		</div>
	</div>
</c:if>
