<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="storeSearchPageData" required="false" type="de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData" %>
<%@ attribute name="locationQuery" required="false" type="java.lang.String" %>
<%@ attribute name="geoPoint" required="false" type="de.hybris.platform.commerceservices.store.data.GeoPoint" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/mobile/store" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav" %>

<c:set value="/store-finder?q=${param.q}" var="searchUrl" />
<c:if test="${not empty geoPoint}">
	<c:set value="/store-finder?latitude=${geoPoint.latitude}&longitude=${geoPoint.longitude}&q=${param.q}" var="searchUrl" />
</c:if>

<c:if test="${storeSearchPageData ne null and not empty storeSearchPageData.results}">
	<ul data-role="listview" data-split-theme="b" data-content-theme="c">
		<li data-role="list-divider">
			<div class="ui-grid-a">
				<div class="ui-block-a" style="width: 100%">
					<h3>
						<c:choose>
							<c:when test="${empty locationQuery}">
								<spring:theme code="search.page.nearbyStores" />
							</c:when>
							<c:otherwise>
								<spring:theme code="search.page.searchText" arguments="${locationQuery}" />
							</c:otherwise>
						</c:choose>
				   		<span class="resultsItems">
				   			(<spring:theme code="text.storefinder.mobile.page.totalResults" arguments="${storeSearchPageData.pagination.totalNumberOfResults}" />)
				   		</span>
				   	</h3>
				</div>
			</div>
		</li>
		<li>
			<div class="ui-grid-a">
				<nav:paginationPageCounter searchPageData="${storeSearchPageData}" msgKey="text.storefinder.mobile.page"/>
			</div>
		</li>
		<c:forEach items="${storeSearchPageData.results}" var="pos" varStatus="row">
			<c:url value="${pos.url}" var="posUrl"/>
			<li>
				<div class="ui-grid-b">
					<div class="ui-block-a" style="width: 10%">
						<img class="mapMarker" data-index="${row.count}"/>
					</div>
					<div class="ui-block-b" style="width: 70%">
						<a href="${posUrl}" data-transition="slide"><span class="storeName">${pos.name}</span></a>
					</div>
					<div class="ui-block-c" style="width: 20%">
						<span>${pos.formattedDistance}</span>
					</div>
				</div>
				<div class="ui-grid-a">
					<div class="ui-block-a" style="width: 10%"></div>
					<div class="ui-block-b" style="width: 90%">
						<ycommerce:testId code="storeFinder_result_address_label">
							<c:if test="${not empty pos.address}">
								<div class="ui-grid-a">
									<span>${pos.address.line1}</span><span>${pos.address.line2}</span>
								</div>
								<div class="ui-grid-a">
									<span>${pos.address.town}</span><span>${pos.address.postalCode}</span>
								</div>
							</c:if>
						</ycommerce:testId>
					</div>
				</div>
				<div class="ui-grid-a">
					<div class="ui-block-a" style="width: 10%"></div>
					<div class="ui-block-b">
						<span class="stores-tel"><a href="tel:${ycommerce:encodeUrl(pos.address.phone)}">${pos.address.phone}</a></span>
					</div>
				</div>
			</li>
		</c:forEach>
	</ul>
	<nav:pagination searchPageData="${storeSearchPageData}"
					searchUrl="${searchUrl}"
					msgKey="text.storefinder.mobile.page" />
</c:if>
