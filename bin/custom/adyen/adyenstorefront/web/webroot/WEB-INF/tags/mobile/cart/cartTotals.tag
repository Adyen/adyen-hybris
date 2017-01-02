<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div id='cartTotals'>
	<div data-theme="b" data-role="content">
		<table cellpadding="0" cellspacing="0" class="order_totals">
			<tr>
				<td><span><spring:theme code="basket.page.totals.subtotal" /></span></td>
				<td class="order_totals_Sum"><span><format:price priceData="${cartData.subTotal}" /></span></td>
			</tr>
			<c:if test="${cartData.totalDiscounts.value > 0}">
				<tr>
					<td class="savings"><span><spring:theme code="basket.page.totals.savings" /></span></td>
					<td class="savings order_totals_Sum"><span><format:price priceData="${cartData.totalDiscounts}" /></span></td>
				</tr>
			</c:if>
			<c:if test="${not empty cartData.deliveryCost}">
				<tr>
					<td><span><spring:theme code="basket.page.totals.delivery" /></span></td>
					<td class="order_totals_Sum"><span><format:price priceData="${cartData.deliveryCost}" displayFreeForZero="TRUE" /></span></td>
				</tr>
			</c:if>
			<c:if test="${cartData.net && cartData.totalTax.value > 0}">
				<tr>
					<td><span><spring:theme code="basket.page.totals.netTax" /></span></td>
					<td class="order_totals_Sum"><span><format:price priceData="${cartData.totalTax}" /></span></td>
				</tr>
			</c:if>
			<tr>
				<td class="completePrice"><span><spring:theme code="basket.page.totals.total" /></span></td>
				<td class="completePrice order_totals_Sum">
					<span>
						<ycommerce:testId code="cart_totalPrice_label">
							<c:choose>
								<c:when test="${cartData.net}">
									<format:price priceData="${cartData.totalPriceWithTax}" />
								</c:when>
								<c:otherwise>
									<format:price priceData="${cartData.totalPrice}" />
								</c:otherwise>
							</c:choose>
						</ycommerce:testId>
					</span>
				</td>
			</tr>
		</table>
	</div>
	<div data-theme="b" data-role="content">
		<c:if test="${not cartData.net}">
			<ycommerce:testId code="cart_taxes_label">
				<p><spring:theme code="basket.page.totals.grossTax" arguments="${cartData.totalTax.formattedValue}" argumentSeparator="!!!!" /></p>
			</ycommerce:testId>
		</c:if>
		<c:if test="${cartData.net && cartData.totalTax.value <= 0}">
			<ycommerce:testId code="cart_taxes_label">
				<p><spring:theme code="basket.page.totals.noNetTax" /></p>
			</ycommerce:testId>
		</c:if>
	</div>
</div>
