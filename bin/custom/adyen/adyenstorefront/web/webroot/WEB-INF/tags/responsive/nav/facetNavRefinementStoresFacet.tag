<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="facetData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.FacetData" %>
<%@ attribute name="userLocation" required="true" type="de.hybris.platform.acceleratorservices.store.data.UserLocationData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<c:url value="/store-finder" var="searchUserLocationUrl"/>
<c:url value="/store-finder/position" var="autoUserLocationUrl"/>



<c:if test="${not empty facetData.values}">
<ycommerce:testId code="facetNav_title_${facetData.name}">
	<div class="facet js-facet js-shop-stores-facet">
		<div class="facet-name js-facet-name">
				<spring:theme code="search.nav.facetTitle" arguments="${facetData.name}"/>
		</div>

		<div class="facet-values js-facet-values">

			<div class="facet-form js-facet-form <c:if test="${not empty userLocation}">facet-form-hidden</c:if>" data-url="${searchUserLocationUrl}">
				<form name="userLocationForm" action="${searchUserLocationUrl}" method="GET" id="user_location_form" class="storesFacetSearchForm">
					<div class="input-group">
						<label for="user_location_query" class="sr-only"><spring:theme code="storeFinder.search"/></label>
						<input type="text" class="form-control js-shop-store-search-input" name="q" id="user_location_query" placeholder="">
						<span class="input-group-btn">
							<button type="submit" id="user_location_query_button" class="btn btn-primary ">
								<span class="glyphicon glyphicon-search"></span>
							</button>
						</span>
					</div>
				</form>

				<div class="line-text"><span><spring:theme code="storeFinder.line.text"/></span></div>

				
				<button type="button" id="findStoresNearMeAjax" class="btn btn-primary btn-block"><spring:theme code="storeFinder.findStoresNearMe"/></button>
			
			</div>

			<div class="facet-container js-facet-container <c:if test="${empty userLocation}">facet-container-hidden</c:if>">
				<c:if test="${not empty userLocation}">
					<div class="facet-search">
						<a href="#" class="js-facet-change-link">
							<spring:theme code="search.nav.changeLocation"/>
						</a>
						<c:if test="${not empty userLocation.searchTerm}">
							<div class="facet-search-results">
								<spring:theme code="search.nav.resultsForStore" arguments="${userLocation.searchTerm}"/>
							</div>
						</c:if>
					</div>


					<c:if test="${not empty facetData.values}">
							<ul class="facet-list js-facet-list ${facetData.multiSelect ? '' : 'indent'}">
								<c:forEach items="${facetData.values}" var="facetValue" varStatus="status">
									<li class="${(status.index < 5 or facetValue.selected) ? '' : 'hidden'}">
										<c:if test="${facetData.multiSelect}">
											<form action="#" method="get">
												<input type="hidden" name="q" value="${facetValue.query.query.value}"/>
												<input type="hidden" name="text" value="${searchPageData.freeTextSearch}"/>
												<label>
													<input class="facet-checkbox js-facet-checkbox sr-only" type="checkbox"  ${facetValue.selected ? 'checked="checked"' : ''} >
													<span class="facet-label">
														<span class="facet-mark"></span>
														<span class="facet-text">
															${facetValue.name}
															<ycommerce:testId code="facetNav_count">
																<span class="facet-value-count"> <spring:theme code="search.nav.facetValueCount" arguments="${facetValue.count}"/></span>
															</ycommerce:testId>
														</span>
													</span>
												</label>
											</form>
										</c:if>
										<c:if test="${not facetData.multiSelect}">
											<c:url value="${facetValue.query.url}" var="facetValueQueryUrl"/>
											<span class="facet-text">
												<a href="${facetValueQueryUrl}">${facetValue.name}</a>
												<ycommerce:testId code="facetNav_count">
													<span class="facet-value-count"> <spring:theme code="search.nav.facetValueCount" arguments="${facetValue.count}"/></span>
												</ycommerce:testId>
											</span>
										</c:if>
									</li>
								</c:forEach>
							</ul>
							<c:if test="${fn:length(facetData.values) > 5}">
								<span class="more">
									<a href="#" class="js-more-stores-facet-values"><spring:theme code="search.nav.facetShowMore_${facetData.code}"/></a>
								</span>
							</c:if>
					</c:if>
				</c:if>
			</div>
		</div>
	</div>
</ycommerce:testId>
</c:if>
