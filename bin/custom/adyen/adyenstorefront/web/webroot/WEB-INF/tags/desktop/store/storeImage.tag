<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="store" required="true" type="de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData" %>
<%@ attribute name="format" required="true" type="java.lang.String" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:set value="${ycommerce:storeImage(store, format)}" var="storeImage"/>
<div class="storeImage">
<c:choose>
	<c:when test="${not empty storeImage}">
		<img src="${storeImage.url}" alt="${store.name}" title="${store.name}"/>
	</c:when>
	<c:otherwise>
		<theme:image code="img.missingStoreImage.${format}" alt="${store.name}" title="${store.name}"/>
	</c:otherwise>
</c:choose>
</div>