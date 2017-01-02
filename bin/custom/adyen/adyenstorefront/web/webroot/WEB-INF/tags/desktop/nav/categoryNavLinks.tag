<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="categories" required="true" type="java.util.List" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<div class="nav_column">
	<div class="title_holder">
		<h2><spring:theme code="search.nav.refinements"/></h2>
	</div>
	
	<div class="facet">
		<div class="facetHead">

			<a href="#" class="refinementToggle">
				<spring:theme code="search.nav.categoryNav"/>
			</a>
		</div>

		<ycommerce:testId code="categoryNav_category_links">
			<div class="facetValues">
				<div class="allFacetValues">
					<ul class="facet_block indent">
						<c:forEach items="${categories}" var="category">
							<li>
								<c:url value="${category.url}" var="categoryUrl"/>
								<a href="${categoryUrl}">${category.name}</a>
							</li>
						</c:forEach>
					</ul>
				</div>
			</div>
		</ycommerce:testId>
	</div>
</div>
