<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="store" required="true" type="de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData"%>
<%@ attribute name="format" required="true" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:set value="${ycommerce:storeImage(store, format)}" var="storeImage"/>
<c:choose>
	<c:when test="${not empty storeImage}">
		<div class="store_image">
			<img src="${storeImage.url}" alt="${store.name}" title="${store.name}"/>
		</div>
	</c:when>
	<c:otherwise>
		<div class="store_image">
			<theme:image code="img.missingStoreImage.${format}" alt="${store.name}" title="${store.name}"/>
		</div>
	</c:otherwise>
</c:choose>
