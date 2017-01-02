<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div data-role="collapsible" data-theme="e" data-content-theme="c" data-collapsed="true" data-icon="arrow-u"
	class="review_detail" id='review_detail'>
	<h3><spring:theme code="text.productreviews"/></h3>
	<p>
	<div id="read_reviews">
		<c:if test="${not empty product.reviews}">
			<ul class="productReviews" data-role="listview" data-theme="e">
				<c:forEach items="${product.reviews}" var="review" varStatus="status">
					<li data-theme="e" ${status.count >3?"style='display: none'":"" } ${status.count >3?"class='hiddenReview'":"" } >
						<div class='review'><c:choose>
							<c:when test="${status.last}">
								<c:set var="reviewDetailStyle" scope="page" value="border:none"/>
							</c:when>
							<c:otherwise>
								<c:set var="reviewDetailStyle" scope="page" value=""/>
							</c:otherwise>
						</c:choose>
							<p>
							<span class="stars large right">
								<span style="width: <fmt:formatNumber maxFractionDigits="0" value="${(review.rating) * 17}" />px;"></span>
							</span>
							</p>
							<div class="review_detail" style="${reviewDetailStyle}">
								<ycommerce:testId code="review_headline"><h3>${review.headline}</h3></ycommerce:testId>
								<p>
									<c:set var="reviewDate" value="${review.date}"/>
									<fmt:formatDate value="${reviewDate}" pattern="dd MMMM yyyy"/>
								</p>
								<p style="white-space:normal">${review.comment}</p>
							</div>
							<p>
								<ycommerce:testId code="review_was_writtern_by">
									<strong>
										<spring:theme code="review.submitted.by"/><c:out value=" "/>
										<c:choose>
											<c:when test="${not empty review.alias}">
												${review.alias}
											</c:when>
											<c:otherwise>
												<spring:theme code="review.submitted.anonymous"/>
											</c:otherwise>
										</c:choose>
									</strong>
								</ycommerce:testId>
							</p>
						</div>
					</li>
				</c:forEach>
				<li data-theme="e">
					<div class="ui-grid-a">
                        <c:if test="${fn:length(product.reviews) gt 3}">
                            <div class="ui-block-a">
                                <ycommerce:testId code="show_more_reviews_button">
                                    <a href="javascript:void('#')" class="showReviews" data-theme="d" data-role="button">
                                        <spring:theme code="review.show.more"/>
                                    </a>
                                </ycommerce:testId>
                            </div>
                        </c:if>
						<div class="ui-block-b" ${fn:length(product.reviews) le 3 ? "style=width:100%":""}>
							<c:url value="/p" var="encodedUrl"/>
							<a href="${encodedUrl}/${product.code}/writeReview" data-role="button" data-theme="c">
								<c:choose>
									<c:when test="${not empty product.reviews}">
										<spring:theme code="review.write.title"/>
									</c:when>
									<c:otherwise>
										<spring:theme code="review.no.reviews"/>
									</c:otherwise>
								</c:choose>
							</a>
						</div>
					</div>
				</li>
			</ul>
		</c:if>
		<c:if test="${empty product.reviews}">
			<c:url value="/p" var="encodedUrl"/>
			<a href="${encodedUrl}/${product.code}/writeReview" data-role="button" data-theme="c">
				<spring:theme code="review.no.reviews"/>
			</a>
		</c:if>
	</div>
	</p>
</div>
