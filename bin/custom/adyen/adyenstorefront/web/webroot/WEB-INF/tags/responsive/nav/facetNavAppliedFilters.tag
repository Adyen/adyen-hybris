<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>


<c:if test="${not empty pageData.breadcrumbs}">

	<div class="facet js-facet">
		<div class="facet-name js-facet-name">Applied Facets</div>
		<div class="facet-values js-facet-values">
			<ul class="facet-list">
				<c:forEach items="${pageData.breadcrumbs}" var="breadcrumb">
					<li>
						<c:url value="${breadcrumb.removeQuery.url}" var="removeQueryUrl"/>
						${breadcrumb.facetValueName}&nbsp;<a href="${removeQueryUrl}" ><span class="glyphicon glyphicon-remove"></span></a>
					</li>
				</c:forEach>
			</ul>
		</div>
	</div>

</c:if>
