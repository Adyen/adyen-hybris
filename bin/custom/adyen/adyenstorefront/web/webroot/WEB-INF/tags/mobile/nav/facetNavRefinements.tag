<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="pageData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>

<ul></ul>
<script id="refinementsListTemplate" type="text/x-jquery-tmpl">
	<ul data-role="listview" data-inset="true" data-content-theme="d">
		{{each facets}}
		<li data-corners="false" data-shadow="false" data-iconshadow="true" data-inline="false" data-wrapperels="div"
			data-icon="arrow-r" data-iconpos="right" data-theme="d">
			<a href="\#{{= name}}-page" id="{{= name}}-button" class="refinementFacetPageLink"><span class="refinementSetName">{{= name}}</span></a>
		</li>
		{{/each}}
	</ul>
</script>
