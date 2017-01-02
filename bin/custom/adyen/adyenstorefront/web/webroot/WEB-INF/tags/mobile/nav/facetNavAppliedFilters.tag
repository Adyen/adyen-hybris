<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="pageData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>

<a href="#facetRefinements-page" data-role="button" data-icon="checkbox-off" data-rel="page" id="addFilters" data-theme="c" data-iconpos="right">
	<spring:theme code="mobile.search.add.refinements"/>
</a>