<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="steps" required="true" type="java.util.List"%>
<%@ attribute name="progressBarId" required="true" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div data-role="navbar" class="checkoutStep">
	<ul data-theme="b">
		<c:forEach items="${steps}" var="checkoutStep" varStatus="status">
			<c:url value="${checkoutStep.url}" var="stepUrl"/>
			<c:choose>
				<c:when test="${progressBarId eq checkoutStep.progressBarId}">
					<c:set scope="page"  var="currentStepActive"  value="${checkoutStep.stepNumber}"/>
					<li class="checkoutStep40">
						<a href="${stepUrl}" data-theme="c" data-corners="false" data-role="button" aria-disabled="true">
							<spring:theme code="mobile.checkout.${checkoutStep.progressBarId}" />
						</a>
					</li>
				</c:when>
				<c:when test="${checkoutStep.stepNumber > currentStepActive }">
					<li class="checkoutStep20">
						<span data-theme="d" data-corners="false" data-role="button" class="ui-disabled">
							<spring:theme code="mobile.checkout.${checkoutStep.progressBarId}" />
						</span>
					</li>
				</c:when>
				<c:otherwise>
					<li class="checkoutStep20">
						<a href="${stepUrl}" data-theme="c" data-corners="false" data-role="button">
							<spring:theme code="mobile.checkout.${checkoutStep.progressBarId}" />
						</a>
					</li>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</ul>
</div>
