<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="showTaxEstimate" required="true" type="java.lang.Boolean" %>
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

<c:if test="${cartData.net && showTaxEstimate}">

	<tr id="countryWrapperDiv">
		<td>
			<spring:theme code="basket.page.totals.deliverycountry"/>
		</td>
		<td id="estimatedCountry">
			<select id="countryIso">
				<option value="" disabled="disabled" selected="selected">
					<spring:theme code='address.selectCountry'/>
				</option>
				<c:forEach var="country" items="${supportedCountries}">
					<option value="${country.isocode}">${country.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>

	<tr id="zipCodewrapperDiv">
		<td>
			<spring:theme code="basket.page.totals.estimatedZip"/>
		</td>
		<td>
			<div class="control-group">
				<div class="controls">
					<input id="zipCode" value="" class="zipInput"/>
				</div>
			</div>
			<button id="estimateTaxesButton" class="right" type="button">
				<spring:theme code="basket.page.totals.estimatetaxesbutton"/>
			</button>
		</td>
	</tr>


	<tr class="hidden estimatedTotals">
		<td>
			<spring:theme code="basket.page.totals.estimatedtotaltax"/>
		</td>
		<td id="estimatedTotalTax" class="hidden estimatedTotals">
			<format:price priceData="${cartData.totalTax}"/>
		</td>
	</tr>

	<tr class="total hidden estimatedTotals">
		<td>
			<spring:theme code="basket.page.totals.estimatedtotal"/>
		</td>
		<td id="estimatedTotalPrice" class="total hidden estimatedTotals">
			<format:price priceData="${cartData.totalPrice}"/>
		</td>
	</tr>
</c:if>

