<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="spellingSuggestion" required="true" type="de.hybris.platform.commerceservices.search.facetdata.SpellingSuggestionData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>

<c:if test="${not empty spellingSuggestion}">
	<div class="searchSpellingSuggestionPrompt">
		<c:url value="${spellingSuggestion.query.url}" var="spellingSuggestionQueryUrl" />
		<spring:theme code="search.spellingSuggestion.prompt" />
		&nbsp;<a href="${spellingSuggestionQueryUrl}">${spellingSuggestion.suggestion}</a>?
	</div>
</c:if>
