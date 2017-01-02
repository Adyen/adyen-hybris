<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ attribute name="format" required="true" type="java.lang.String"%>
<%@ attribute name="zoomable" required="true" type="java.lang.Boolean"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:set value="${ycommerce:productImage(product, format)}" var="primaryImage"/>
<c:set value="${ycommerce:productImage(product, 'cartIcon')}" var="thumbnailImage"/>

<div class="prod_image_main">
	<c:choose>
		<c:when test="${not empty primaryImage}">
			<c:if test="${zoomable}">
				<img class="primaryImage hasThumbnail" src="${primaryImage.url}" data-producturl="${product.url}" data-mainimageurl="${primaryImage.url}"
					data-thumbnail="${thumbnailImage.url}" data-galleryposition="0" title="${product.name}" alt="${product.name}" />
				<div id="clickToZoomOverlay" class="zoomImageButtonOverlay">
					<div id="clickToZoomButton" class="zoomButton">Tap To Zoom</div>
				</div>
			</c:if>
			<c:if test="${not zoomable}">
				<img class="primaryImage hasThumbnail" src="${primaryImage.url}" title="${product.name}" alt="${product.name}" data-thumbnail="${thumbnailImage.url}" />
			</c:if>
		</c:when>
		<c:otherwise>
			<theme:image code="img.missingProductImage.${format}" alt="${product.name}" title="${product.name}"/>
		</c:otherwise>
	</c:choose>
</div>
