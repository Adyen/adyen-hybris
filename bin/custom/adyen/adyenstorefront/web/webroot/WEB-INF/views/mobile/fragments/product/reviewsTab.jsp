<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div id="read_reviews" data-theme="b">
	<ul class="review_actions" data-theme="b">
		<li>
			<a href="#" id="write_review_action">
				<c:choose>
					<c:when test="${not empty reviews}">
						<spring:theme code="review.write.title"/>
					</c:when>
					<c:otherwise>
						<spring:theme code="review.no.reviews"/>
					</c:otherwise>
				</c:choose>
			</a>
		</li>
		<li>${fn:length(reviews)}&nbsp;
			<spring:theme code="review.number.of"/>&nbsp;${reviewsTotal}&nbsp;<spring:theme code="review.number.reviews"/>
		</li>
		<c:if test="${fn:length(reviews) ne reviewsTotal}">
			<li><a href="#" id="show_all_reviews_top_action">
				<spring:theme code="review.show.all"/>
			</a></li>
		</c:if>
	</ul>
	<c:if test="${not empty reviews}">
		<c:forEach items="${reviews}" var="review" varStatus="status">
			<c:choose>
				<c:when test="${status.last}">
					<c:set var="reviewDetailStyle" scope="page" value="border:none"/>
				</c:when>
				<c:otherwise>
					<c:set var="reviewDetailStyle" scope="page" value=""/>
				</c:otherwise>
			</c:choose>
			<div class="review_detail" style="${reviewDetailStyle}">
				<h3>${review.headline}</h3>
				<span class="stars large right" style="display: inherit;">
					<span style="width: <fmt:formatNumber maxFractionDigits="0" value="${review.rating * 17}" />px;"></span>
				</span>
				<p>${review.comment}</p>
				<p class="review_origins">
					<spring:theme code="review.submitted.by"/>
					<c:out value=" "/>
					<c:choose>
						<c:when test="${not empty review.alias}">
							${review.alias}
						</c:when>
						<c:otherwise>
							<spring:theme code="review.submitted.anonymous"/>
						</c:otherwise>
					</c:choose>
					<c:set var="reviewDate" value="${review.date}"/>
					(
					<fmt:formatDate value="${reviewDate}" pattern="dd/MM/yyyy"/>
					)
				</p>
			</div>
		</c:forEach>

		<c:if test="${fn:length(reviews) ne reviewsTotal}">
			<a href="#" id="show_all_reviews_bottom_action"><spring:theme code="review.show.all"/></a>
		</c:if>
	</c:if>
</div>
