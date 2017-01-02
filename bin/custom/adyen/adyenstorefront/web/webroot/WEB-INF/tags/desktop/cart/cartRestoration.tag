<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:url value="/cart" var="cartUrl" />

<c:if test="${not empty restorationErrorMsg}">
	<div class="alert negative">
		<spring:theme code="basket.restoration.${restorationErrorMsg}" />
	</div>
</c:if>
<c:if
	test="${not empty restorationData and not empty restorationData.modifications}">
	<div class="alert neutral">

		<spring:theme code="basket.restoration" />
		<c:choose>
			<c:when test="${not showModifications}">
				<spring:theme code="basket.restoration.view.cart"
					arguments="${cartUrl}" />
			</c:when>
			<c:otherwise>
				<c:forEach items="${restorationData.modifications}"
					var="modification">
					<br />
					<c:url value="${modification.entry.product.url}" var="entryUrl" />
					<c:choose>
						<c:when
							test="${modification.deliveryModeChanged and not empty modification.entry}">
							<spring:theme code="basket.restoration.delivery.changed"
								arguments="${modification.entry.product.name},${entryUrl},${modification.entry.quantity},${modification.quantityAdded}" />
						</c:when>
						<c:when
							test="${modification.deliveryModeChanged and empty modification.entry}">
							<spring:theme code="basket.restoration.delivery.changed"
								arguments="${modification.entry.product.name},${entryUrl},${modification.quantity},${modification.quantityAdded}" />
						</c:when>
						<c:when test="${not modification.deliveryModeChanged and not empty modification.entry}">
							<spring:theme
								code="basket.restoration.${modification.statusCode}"
								arguments="${modification.entry.product.name},${entryUrl},${modification.entry.quantity},${modification.quantityAdded}" />
						</c:when>
						<c:when test="${not modification.deliveryModeChanged and empty modification.entry}">
							<spring:theme
								code="basket.restoration.${modification.statusCode}"
								arguments="${modification.entry.product.name},${entryUrl},${modification.quantity},${modification.quantityAdded}" />
						</c:when>
					</c:choose>
				</c:forEach>
			</c:otherwise>
		</c:choose>
	</div>
</c:if>
