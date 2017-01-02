<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="currencies" required="true" type="java.util.Collection" %>
<%@ attribute name="currentCurrency" required="true" type="de.hybris.platform.commercefacades.storesession.data.CurrencyData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:if test="${fn:length(currencies) > 1}">
	<c:url value="/_s/currency" var="setCurrencyActionUrl"/>
	<form:form action="${setCurrencyActionUrl}" method="post" id="currency_form_${component.uid}" class="currency-form">
		<div class="control-group">
			<label class="control-label skip" for="currency_selector_${component.uid}"><spring:theme code="text.currency"/></label>
			<div class="controls">
				<ycommerce:testId code="header_currency_select">
					<select name="code" id="currency_selector_${component.uid}" class="currency-selector">
						<c:forEach items="${currencies}" var="curr">
							<option value="${curr.isocode}" ${curr.isocode == currentCurrency.isocode ? 'selected="selected"' : ''}>
								<c:out value="${curr.symbol} ${curr.isocode}"/>
							</option>
						</c:forEach>
					</select>
				</ycommerce:testId>
			</div>
		</div>
	</form:form>
</c:if>
