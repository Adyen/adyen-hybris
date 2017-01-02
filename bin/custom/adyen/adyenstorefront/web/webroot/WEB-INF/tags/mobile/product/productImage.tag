<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ attribute name="format" required="true" type="java.lang.String"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:set value="${ycommerce:productImage(product, 'cartIcon')}" var="thumbnailImage"/>
<c:if test="${not empty galleryImages}">
	<div class="imageGallery swipeGallery" data-enabled='true' class="hasThumbnail" data-commonresourcepath="${commonResourcePath}" data-thumbnail="${thumbnailImage.url}">
		<c:forEach items="${galleryImages}" var="container" varStatus="varStatus">
			<div class='imageGallerySlide swipeGallerySlide'
				data-primaryimagesrc="${container.product.url}"
				data-producturl="${product.url}"
				data-zoomimagesrc="${container.zoom.url}"
				data-galleryposition="${varStatus.index}"
				data-title="${product.name}"
				data-alt="${product.name}"
				data-zoom='in'>
				<div class="imageContainer">
					<img src="${container.product.url}" title="${product.name}" alt="${product.name}"/>
				</div>
				<div class='zoomButtonOverlay'>
					<div class='zoomButton'>
						<div class='zoomInButton' alt='<spring:theme code="general.zoom"/>' tabindex='0'></div>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
	<div class="dots">
		<c:if test="${fn:length(galleryImages) gt 1}">
			<c:forEach items="${galleryImages}" var="container" varStatus="varStatus">
				<c:if test="${(varStatus.index == 0)}">
					<span><img src='${commonResourcePath}/images/closeddot.png'/></span>
				</c:if>
				<c:if test="${(varStatus.index != 0)}">
					<span><img src='${commonResourcePath}/images/opendot.png'/></span>
				</c:if>
			</c:forEach>
		</c:if>
	</div>
</c:if>
<c:if test="${empty galleryImages}">
	<div><theme:image code="img.missingProductImage.product" alt="Missing product image" title="Missing product image"/></div>
</c:if>
