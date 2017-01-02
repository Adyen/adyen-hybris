<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>  



<div class="productImage">
	<div class="productImageGallery">
		<c:if test="${fn:length(galleryImages) gt 0}">
			<div class="scroller">
			
					<ul class="jcarousel-skin">
						<c:forEach items="${galleryImages}" var="container">
							<li>
								<span class="thumb">
										<img src="${container.thumbnail.url}" data-zoomurl="${container.zoom.url}" alt="${product.name}" title="${product.name}">
								</span>
							</li>
						</c:forEach>
					</ul>
			
			</div>
		</c:if>
	</div>
	
	<c:if test="${empty zoomImageUrl}">
		<c:set value="${ycommerce:productImage(product, 'zoom').url}" var="zoomImageUrl"/>
	</c:if>
	<c:if test="${not empty requestParams['mediaUrl'][0]}">
		<c:forEach items="${galleryImages}" var="container">
			<c:if test="${container.product.url eq requestParams['mediaUrl'][0]}">
				<c:set var="zoomImageUrl">${container.zoom.url}</c:set>
			</c:if>
		</c:forEach>
	</c:if>
	
	<div class="productImagePrimary">
		<c:if test="${not empty zoomImageUrl}">
			<img src="${zoomImageUrl}"  alt="${product.name}" title="${product.name}"/>
		</c:if>
	</div>

</div>


