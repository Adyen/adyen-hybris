<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="languages" required="true" type="java.util.Collection" %>
<%@ attribute name="currentLanguage" required="true" type="de.hybris.platform.commercefacades.storesession.data.LanguageData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:if test="${fn:length(languages) > 1}">
	<c:url value="/_s/language" var="setLanguageActionUrl"/>
	<form:form action="${setLanguageActionUrl}" method="post" id="lang_form_${component.uid}" class="lang-form">
		<spring:theme code="text.language" var="languageText"/>
		<div class="control-group">
		<label class="control-label skip" for="lang_selector_${component.uid}">${languageText}</label>
		
		<div class="controls">
		<ycommerce:testId code="header_language_select">
			<select name="code" id="lang_selector_${component.uid}" class="lang-selector">
				<c:forEach items="${languages}" var="lang">
					<c:choose>
						<c:when test="${lang.isocode == currentLanguage.isocode}">
							<option value="${lang.isocode}" selected="selected" lang="${lang.isocode}">
								${lang.nativeName}
							</option>
						</c:when>
						<c:otherwise>
							<option value="${lang.isocode}" lang="${lang.isocode}">
								${lang.nativeName} <%-- (${lang.name}) --%>
							</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</ycommerce:testId>
		</div>
	</div>
	</form:form>
</c:if>