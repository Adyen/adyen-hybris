<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>

<div id="read_reviews">
	<div class="actionBar top clearfix">
		<div class="left">
			<a href="#" id="write_review_action" class="write" >
				<c:choose>
					<c:when test="${not empty reviews}">
						<spring:theme code="review.write.title"/>
					</c:when>
					<c:otherwise>
						<spring:theme code="review.no.reviews"/>
					</c:otherwise>
				</c:choose>
			</a>
		</div>
		<div class="right">
			<c:if test="${fn:length(reviews) ne reviewsTotal}" >
				<a href="#" id="show_all_reviews_top_action"><spring:theme code="review.show.all"/></a>&nbsp;&nbsp;
			</c:if>
			${fn:length(reviews)}&nbsp;<spring:theme code="review.number.of"/>&nbsp;${reviewsTotal}&nbsp;<spring:theme code="review.number.reviews"/>
		</div>
	</div>

	<c:if test="${not empty reviews}">
	<div class="rewiewList">
		<c:forEach items="${reviews}" var="review" varStatus="status">
			<div class="reviewDetail">
				<product:productStars rating="${review.rating}"  addClass="right" />
				<div class="headline" >${review.headline}</div>
				
			
				<div class="body" >${review.comment}</div>
	
				<div class="autor">
					<spring:theme code="review.submitted.by"/><c:out value=" "/>
					
					<c:choose>
						<c:when test="${not empty review.alias}">
							${review.alias}
						</c:when>
						<c:otherwise>
							<spring:theme code="review.submitted.anonymous"/>
						</c:otherwise>
					</c:choose>
	
					<c:set var="reviewDate" value="${review.date}" />
					(<fmt:formatDate value="${reviewDate}" pattern="dd/MM/yyyy" />)
				</div>
			</div>
		</c:forEach>
</div>
		<div class="actionBar bottom clearfix">
			<div class="right">
				<c:if test="${fn:length(reviews) ne reviewsTotal}" >
					<a href="#" id="show_all_reviews_bottom_action"><spring:theme code="review.show.all"/></a>
				</c:if>
			</div>
		</div>
	</c:if>
</div>