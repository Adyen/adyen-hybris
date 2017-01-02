<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="showTaxEstimate" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showTax" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>

<table id="orderTotals">
	<thead>
	<tr>
		<td>
			<spring:theme code="order.order.totals"/>
		</td>
		<td></td>
	</tr>
	</thead>
	<tfoot>
	<tr>
		<td>
			<spring:theme code="basket.page.totals.total"/>
		</td>
		<td>
			<ycommerce:testId code="cart_totalPrice_label">
				<c:choose>
					<c:when test="${showTax}">
						<format:price priceData="${cartData.totalPriceWithTax}"/>
					</c:when>
					<c:otherwise>
						<format:price priceData="${cartData.totalPrice}"/>
					</c:otherwise>
				</c:choose>

			</ycommerce:testId>
		</td>
	</tr>
	</tfoot>
	<tbody>
	<tr>
		<td>
			<spring:theme code="basket.page.totals.subtotal"/>
		</td>
		<td>
			<ycommerce:testId code="Order_Totals_Subtotal">
				<format:price priceData="${cartData.subTotal}"/>
			</ycommerce:testId>
		</td>
	</tr>

	<c:if test="${cartData.totalDiscounts.value > 0}">
		<tr class="savings">
			<td>
				<spring:theme code="basket.page.totals.savings"/>
			</td>
			<td>
				<ycommerce:testId code="Order_Totals_Savings">
					<format:price priceData="${cartData.totalDiscounts}"/>
				</ycommerce:testId>
			</td>
		</tr>
	</c:if>

	<c:if test="${not empty cartData.deliveryCost}">
		<tr>
			<td>
				<spring:theme code="basket.page.totals.delivery"/>
			</td>
			<td>
				<format:price priceData="${cartData.deliveryCost}" displayFreeForZero="TRUE"/>
			</td>
		</tr>
	</c:if>

	<c:if test="${cartData.net && cartData.totalTax.value > 0 && showTax}">
		<tr>
			<td class="total">
				<spring:theme code="basket.page.totals.netTax"/>
			</td>
			<td class="total">
				<format:price priceData="${cartData.totalTax}"/>
			</td>
		</tr>
	</c:if>
	<cart:taxExtimate cartData="${cartData}" showTaxEstimate="${showTaxEstimate}"/>
	</tbody>
</table>


<c:if test="${not cartData.net}">
	<div class="realTotals">
		<ycommerce:testId code="cart_taxes_label">
			<p>
				<spring:theme code="basket.page.totals.grossTax" arguments="${cartData.totalTax.formattedValue}" argumentSeparator="!!!!"/>
			</p>
		</ycommerce:testId>
	</div>
</c:if>
<c:if test="${cartData.net && not showTax }">
	<div class="realTotals">
		<ycommerce:testId code="cart_taxes_label">
			<p>
				<spring:theme code="basket.page.totals.noNetTax"/>
			</p>
		</ycommerce:testId>
	</div>
</c:if>
