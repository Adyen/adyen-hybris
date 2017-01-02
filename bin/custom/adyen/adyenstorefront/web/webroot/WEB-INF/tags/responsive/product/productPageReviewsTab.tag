<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:url value="${product.url}/reviewhtml/3" var="getPageOfReviewsUrl"/>
<c:url value="${product.url}/reviewhtml/all" var="getAllReviewsUrl"/>
<c:url value="${product.url}/review" var="productReviewActionUrl"/>

<div class="tab-review">
	<div class="review-pagination-bar">
		<button class="btn btn-default js-review-write-toggle "><spring:theme code="review.write.title"/></button>

		<div class="right">
			<button class="btn btn-default all-reviews-btn"><spring:theme code="review.show.all" /></button>
			<button class="btn btn-default less-reviews-btn"><spring:theme code="review.show.less" /></button>
		</div>
	</div>

	<div class="write-review js-review-write">
		<form:form method="post" action="${productReviewActionUrl}" commandName="reviewForm">
			<div class="form-group">
				<formElement:formInputBox idKey="review.headline" labelKey="review.headline" path="headline" inputCSS="form-control" mandatory="true"/>
			</div>
			<div class="form-group">
				<formElement:formTextArea idKey="review.comment" labelKey="review.comment" path="comment" areaCSS="form-control" mandatory="true"/>
			</div>
			
			<div class="form-group">
			
				<label><spring:theme code="review.rating"/></label>


				<div class="rating rating-set js-ratingCalcSet"
					data-rating='{"total":5}'>
					<div class="rating-stars">
						<span class="js-ratingIcon js-rationIconSet glyphicon glyphicon-star"></span>
					</div>
				</div>

				<formElement:formInputBox idKey="review.rating" labelKey="review.rating" path="rating" inputCSS="sr-only js-ratingSetInput" labelCSS="sr-only" mandatory="true"/>
	
				<formElement:formInputBox idKey="alias" labelKey="review.alias" path="alias" inputCSS="form-control" mandatory="false"/>
			</div>

			<button type="submit" class="btn btn-primary" value="<spring:theme code="review.submit"/>"><spring:theme code="review.submit"/></button>
		</form:form>

	</div>

	<ul id="reviews" class="review-list" data-reviews="${getPageOfReviewsUrl}"  data-allreviews="${getAllReviewsUrl}"></ul>

	<div class="review-pagination-bar">

		<div class="right">
			<button class="btn btn-default all-reviews-btn"><spring:theme code="review.show.all" /></button>
			<button class="btn btn-default less-reviews-btn"><spring:theme code="review.show.less" /></button>
		</div>
	</div>
</div>
