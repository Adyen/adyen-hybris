<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>

<div class="prodReview clearfix">
			<product:productStars rating="${product.averageRating}" />
			
			<c:if test="${not empty product.reviews}">
			
					<c:choose>
						<c:when test="${fn:length(product.reviews) > 1}">
							<a href="#" id="based_on_reviews" class="count" ><spring:theme code="review.based.on" arguments="${fn:length(product.reviews)}"/></a>
						</c:when>
						<c:otherwise>
							<a href="#" id="based_on_reviews" class="count" ><spring:theme code="review.based.on.one" arguments="${fn:length(product.reviews)}"/></a>
						</c:otherwise>
					</c:choose>
			
			</c:if>
			
			
				<a href="#" id="write_review_action_main" class="write" ><spring:theme code="review.write.title"  /></a>
		
			
		
		</div>