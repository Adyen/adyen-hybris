<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="galleryImages" required="true" type="java.util.List" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>


<div class="image-gallery js-gallery">
    <div class="carousel gallery-image js-gallery-image">
        <c:forEach items="${galleryImages}" var="container" varStatus="varStatus">
            <div class="item">
                <div class="thumb">
                    <img class="lazyOwl" data-src="${container.product.url}"
                         data-zoom-image="${container.superZoom.url}"
                         alt="${container.thumbnail.altText}" >
                </div>
            </div>
        </c:forEach>
    </div>
    <product:productGalleryThumbnail galleryImages="${galleryImages}" />
</div>
