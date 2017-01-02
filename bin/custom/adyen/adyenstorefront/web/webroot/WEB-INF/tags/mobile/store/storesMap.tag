<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="store" required="false" type="de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData"%>
<%@ attribute name="storeSearchPageData" required="false" type="de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${storeSearchPageData ne null and !empty storeSearchPageData.results}">
	<c:set var="markers" value=""/>
	<c:forEach var="location" items="${storeSearchPageData.results}" varStatus="status">
		<c:set var="markers" value="${markers}{'latitude':'${location.geoPoint.latitude}','longitude':'${location.geoPoint.longitude}','title': '${location.name}'}${ ! status.last ? ',' : ''}"/>
	</c:forEach>
	<div class="stores_map" id="maps_canvas" style="width: 100%;" data-latitude="${storeSearchPageData.sourceLatitude}"
		data-longitude="${storeSearchPageData.sourceLongitude}" data-markers="[${markers}]">
	</div>
</c:if>
