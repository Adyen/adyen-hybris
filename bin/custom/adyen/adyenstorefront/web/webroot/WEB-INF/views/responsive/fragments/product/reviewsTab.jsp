<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>

<c:set value="${fn:length(reviews) eq reviewsTotal}" var="showingAllReviews"/>
<div id="showingAllReviews" data-showingAllReviews="${showingAllReviews}" ></div>

<c:if test="${not empty reviews}">
	<c:forEach items="${reviews}" var="review" varStatus="status">
		<li class="review-entry">
			<div class="title">${review.headline}</div>
			<div class="rating js-ratingCalc"
				data-rating='{"rating":${review.rating},"total":5}'>
				<div class="rating-stars">
					<span class="js-ratingIcon glyphicon glyphicon-star"></span>
				</div>
			</div>
			<div class="content">${review.comment}</div>
			<div class="autor">
				<c:choose>
					<c:when test="${not empty review.alias}">
							${review.alias}
						</c:when>
					<c:otherwise>
						<spring:theme code="review.submitted.anonymous" />
					</c:otherwise>
				</c:choose>
				<c:set var="reviewDate" value="${review.date}" />
				<span class="date"> (<fmt:formatDate value="${reviewDate}" pattern="dd/MM/yyyy" />)</span>
			</div>

		</li>
	</c:forEach>
</c:if>
