<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div>
	
		<fmt:formatNumber maxFractionDigits="0" value="${(product.averageRating > 0 ? product.averageRating: 0) * 17}" var="starWidth"/>
		<span class="stars large"
			style="display: inherit;"
			tabindex='4'
			alt='<spring:theme code="product.average.review.rating" arguments="${(product.averageRating > 0 ? product.averageRating: 0)}"/>'>
			<span style="width: ${starWidth}px;"></span>
		</span>
		<c:if test="${product.numberOfReviews gt 0}"><a href="#" id="averageRatingTopOfPage">
			<div class="numberOfReviews">( ${product.numberOfReviews} )</div>
		</a>
		<a href="#" id='seeReviewsLink'><spring:theme code="review.see.reviews"/> | </a>
	</c:if>
	<c:url value="/p" var="encodedUrl"/>
	<a id='writeReviewLink' href="${encodedUrl}/${product.code}/writeReview"><spring:theme code="review.write.review"/></a>
</div>
<div style="clear:both"></div>
