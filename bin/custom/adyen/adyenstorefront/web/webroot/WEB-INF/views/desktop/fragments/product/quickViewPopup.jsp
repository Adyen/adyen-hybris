<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>

<c:url value="${product.url}" var="productUrl"/>



<div id="quickviewLightbox" class="productDetailsPanel clearfix">
	<div class="column productImage">
		<div class="productImagePrimary">
			<a class="productImagePrimaryLink"  href="${productUrl}">
				<product:productPrimaryImage product="${product}" format="product"/>
			</a>
			<c:url value="${product.url}/zoomImages" var="productZoomImagesUrl"/>
			<a  class="productImageZoomLink" href="${productZoomImagesUrl}"></a>
		</div>
	</div>
	<div class="column productDescription last">

		<div class="big-price right"><format:fromPrice priceData="${product.price}"/></div>
			<a href="${productUrl}">
				<h1>${product.name}</h1>
			</a>
			
			<div class="prodReview clearfix">
				<c:if test="${not empty product.reviews}">
					<product:productStars rating="${product.averageRating}" />
				</c:if>
				
				<c:if test="${not empty product.reviews}">
					<c:url value="${product.url}#tab-reviews" var="productReadReviewsUrl"/>
					<a class="count" href="${productReadReviewsUrl}"><spring:theme code="review.based.on" arguments="${fn:length(product.reviews)}"/></a>
				</c:if>
				
				<c:url value="${product.url}#tab-reviews" var="productWriteReviewsUrl"/>
				<a class="write" href="${productWriteReviewsUrl}"><spring:theme code="review.write.title" /></a>
			</div>
			
			<div class="summary">${product.summary}</div>
			
			<c:if test="${not empty product.potentialPromotions}">
				<div class="bundle">
					<c:choose>
						<c:when test="${not empty product.potentialPromotions[0].couldFireMessages}">
							<p>${product.potentialPromotions[0].couldFireMessages[0]}</p>
						</c:when>
						<c:otherwise>
							<p>${product.potentialPromotions[0].description}</p>
						</c:otherwise>
					</c:choose>
				</div>
			</c:if>
			
			<div class="quickview_lightbox-goto-product">
				<a href="${productUrl}"><spring:theme code="product.product.details.more"/></a>
			</div>
			
			<product:productAddToCartPanel product="${product}" allowAddToCart="${true}" isMain="false" />
	</div>
</div>