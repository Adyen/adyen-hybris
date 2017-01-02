<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ attribute name="format" required="true" type="java.lang.String"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:set value="${ycommerce:productImage(product, format)}" var="primaryImage"/>
<div class="prod_image_main" data-role="content">
	<c:choose>
		<c:when test="${not empty primaryImage}">
			<img class="productCartImage" src="${primaryImage.url}" alt="${product.name}" producturl="${product.url}"
				title="${product.name}" id="primaryImage" galleryposition="0" mainimageurl="${primaryImage.url}"/>
		</c:when>
		<c:otherwise>
			<theme:image code="img.missingProductImage.${format}" alt="${product.name}" title="${product.name}"/>
		</c:otherwise>
	</c:choose>
</div>
