<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="currencies" required="true" type="java.util.Collection"%>
<%@ attribute name="currentCurrency" required="true" type="de.hybris.platform.commercefacades.storesession.data.CurrencyData"%>
<%@ attribute name="languages" required="true" type="java.util.Collection"%>
<%@ attribute name="currentLanguage" required="true" type="de.hybris.platform.commercefacades.storesession.data.LanguageData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:url value="/_s/language" var="languageUrl"/>
<c:url value="/_s/currency" var="currencyUrl"/>


<ul data-role="mFormList" data-inset="true" data-theme="e" data-content-theme="e">
	<c:if test="${fn:length(languages) > 1}">
		<li>
			<form:form action="${languageUrl}" method="post" id="lang-form">
				<ycommerce:testId code="header_language_select">
					<select name="code" id="lang-selector" data-theme="f">
						<c:forEach items="${languages}" var="lang">
							<option value="${lang.isocode}" ${lang.isocode == currentLanguage.isocode ? 'selected="selected"' : ''} lang="${lang.isocode}">${lang.nativeName}</option>
						</c:forEach>
					</select>
				</ycommerce:testId>
			</form:form>
		</li>
	</c:if>
	<c:if test="${fn:length(currencies) > 1}">
		<li>
			<form:form action="${currencyUrl}" method="post" id="currency-form">
				<ycommerce:testId code="header_currency_select">
					<select name="code" id="currency-selector" data-theme="f">
						<c:forEach items="${currencies}" var="curr">
							<option value="${curr.isocode}" ${curr.isocode == currentCurrency.isocode ? 'selected="selected"' : ''}>${curr.symbol} ${curr.isocode}</option>
						</c:forEach>
					</select>
				</ycommerce:testId>
			</form:form>
		</li>
	</c:if>
</ul>
