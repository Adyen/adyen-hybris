<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="store" required="false" type="de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<div class="storeMap">	
	<c:if test="${store ne null and store.geoPoint.latitude ne null and store.geoPoint.longitude ne null}">
		<div class="store_map_details" id="map_canvas"
			data-latitude = '${store.geoPoint.latitude}'
			data-longitude = '${store.geoPoint.longitude}'
			data-stores= '{"id":"0","latitude":"${store.geoPoint.latitude}","longitude":"${store.geoPoint.longitude}","name":"<div class=strong>${store.name}</div><div>${store.address.line1}</div><div>${store.address.line2}</div><div>${store.address.town}</div><div>${store.address.postalCode}</div><div>${store.address.country.name}</div>"}'
			></div>
	</c:if>
</div>
