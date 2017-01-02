<%@ page trimDirectiveWhitespaces="true" %>
<div>
	<img title="${media.altText}" alt="${media.altText}" src="${media.url}" usemap="#map">
	<map name="map">
		${imageMapHTML}
	</map>
</div>