<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="store" required="false" type="de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData"%>

<div class="store_map"
	id="maps_canvas"
	style="width: 100%;"
	data-latitude="${store.geoPoint.latitude}"
	data-longitude="${store.geoPoint.longitude}"
	data-storename="${store.name}"
	data-line1="${store.address.line1}"
	data-line2="${store.address.line2}"
	data-town="${store.address.town}"
	data-postalCode="${store.address.postalCode}"
	data-country="${store.address.country.name}">
</div>
