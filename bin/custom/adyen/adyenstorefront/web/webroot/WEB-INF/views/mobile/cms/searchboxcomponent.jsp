<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/search" var="searchUrl"/>
<c:url value="/search/autocomplete/${component.uid}" var="autocompleteUrl"/>

<div class="searchcomponent">
	<div class="accmob-search">
		<form name="search_form" method="get" action="${searchUrl}">
			<spring:theme code="text.search" var="searchText"/>
			<spring:theme code="search.placeholder" var="searchPlaceholder"/>
			<ycommerce:testId code="header_search_input">
				<label class="skip" for="search">${searchPlaceholder}</label>
				<input id="search" class="text accmob-mainSearch-input" type="search" name="text" value="" maxlength="100" placeholder="${searchPlaceholder}" data-options='{"autocompleteUrl" : "${autocompleteUrl}","minCharactersBeforeRequest" : "${component.minCharactersBeforeRequest}","waitTimeBeforeRequest" : "${component.waitTimeBeforeRequest}","displayProductImages" : ${component.displayProductImages}}' autocomplete="off"/>
			</ycommerce:testId>
			<div class="accmob-mainSearch-button">
				<ycommerce:testId code="header_search_button">
					<spring:theme code="img.searchButton" text="/" var="searchButtonPath"/>
					<input class="button" data-iconpos="notext" data-icon="custom-search" data-theme="d" type="submit" value="${searchText}"/>
				</ycommerce:testId>
			</div>
		</form>
	</div>
</div>
