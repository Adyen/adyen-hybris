<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags"%>

<c:if test="${not empty validationData}">
	<c:set var="productLinkValidationTextDecoration" value="style=\"text-decoration: underline\""/>
	<c:forEach items="${validationData}" var="modification">
		<ul>
			<c:url value="${modification.entry.product.url}" var="entryUrl"/>
			<li>
				<spring:theme code="basket.validation.${modification.statusCode}"
					arguments="${modification.entry.product.name}###${entryUrl}###${modification.quantity}###${modification.quantityAdded}###${productLinkValidationTextDecoration}"
					argumentSeparator="###"/>
			</li>
		</ul>
	</c:forEach>
</c:if>
