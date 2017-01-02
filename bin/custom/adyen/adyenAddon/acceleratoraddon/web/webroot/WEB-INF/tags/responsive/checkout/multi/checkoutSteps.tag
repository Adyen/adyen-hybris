<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ attribute name="checkoutSteps" required="true" type="java.util.List" %>
<%@ attribute name="progressBarId" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<div class="col-sm-6 col-md-7 col-lg-5">
	<ycommerce:testId code="checkoutSteps">
		<div class="checkout-steps">
			<c:forEach items="${checkoutSteps}" var="checkoutStep" varStatus="status">
				<c:url value="${checkoutStep.url}" var="stepUrl"/>
				<c:choose>
					<c:when test="${progressBarId eq checkoutStep.progressBarId}">
						<c:set scope="page"  var="activeCheckoutStepNumber"  value="${checkoutStep.stepNumber}"/>
						<a href="${stepUrl}" class="step-head js-checkout-step active">
							<div class="title"><spring:theme code="checkout.multi.${checkoutStep.progressBarId}"/></div>
						</a>
						<div class="step-body"><jsp:doBody/></div>
					</c:when>
					<c:when test="${checkoutStep.stepNumber > activeCheckoutStepNumber}">
						<a href="${stepUrl}" class="step-head js-checkout-step ">
							<div class="title"><spring:theme code="checkout.multi.${checkoutStep.progressBarId}"/></div>
						</a>
					</c:when>
					<c:otherwise>
						<a href="${stepUrl}" class="step-head js-checkout-step ">
							<div class="title"><spring:theme code="checkout.multi.${checkoutStep.progressBarId}"/></div>
							<div class="edit">
								<span class="glyphicon glyphicon-pencil"></span>
							</div>
						</a>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</div>
	</ycommerce:testId>
</div>





					
					
					
