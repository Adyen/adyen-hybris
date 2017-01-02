<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="headline">
	<spring:theme code="text.account.paymentDetails" text="Payment Details"/>
</div>
<div class="description">
	<spring:theme code="text.account.paymentDetails.managePaymentDetails" text="Manage your saved payment details."/>
</div>
<c:choose>
	<c:when test="${not empty paymentInfoData}">
		<c:forEach items="${paymentInfoData}" var="paymentInfo">
			<div class="paymentItem">
				<ul>
					<li>${fn:escapeXml(paymentInfo.cardNumber)}</li>
					<li>${fn:escapeXml(paymentInfo.cardType)}</li>
					<li>
						<spring:theme code="text.expires" text="Expires"/>
						${fn:escapeXml(paymentInfo.expiryMonth)} / ${fn:escapeXml(paymentInfo.expiryYear)}
					</li>
				</ul>
				<ul>
					<li>
						<c:out value="${fn:escapeXml(paymentInfo.billingAddress.title)} ${fn:escapeXml(paymentInfo.billingAddress.firstName)} ${fn:escapeXml(paymentInfo.billingAddress.lastName)}"/>
					</li>
					<li>${fn:escapeXml(paymentInfo.billingAddress.line1)}</li>
					<li>${fn:escapeXml(paymentInfo.billingAddress.line2)}</li>
					<li>${fn:escapeXml(paymentInfo.billingAddress.town)}</li>
					<li>${fn:escapeXml(paymentInfo.billingAddress.postalCode)}</li>
					<li>${fn:escapeXml(paymentInfo.billingAddress.country.name)}</li>
				</ul>
				<div class="buttons">
					<c:if test="${not paymentInfo.defaultPaymentInfo}">
						<c:url value="/my-account/set-default-payment-details" var="setDefaultPaymentActionUrl"/>
						<form:form id="setDefaultPaymentDetails${paymentInfo.id}" action="${setDefaultPaymentActionUrl}" method="post">
							<input type="hidden" name="paymentInfoId" value="${paymentInfo.id}"/>
							<button type="submit" class="submitSetDefault" id="${paymentInfo.id}" href="#">
								<spring:theme code="text.setDefault" text="Set as default"/>
							</button>
						</form:form>
					</c:if>
					<c:url value="/my-account/remove-payment-method" var="removePaymentActionUrl"/>
					<form:form id="removePaymentDetails${paymentInfo.id}" action="${removePaymentActionUrl}" method="post">
						<input type="hidden" name="paymentInfoId" value="${paymentInfo.id}"/>
						<button type="submit" class="submitRemove" id="${paymentInfo.id}" href="#">
							<spring:theme code="text.remove" text="Remove"/>
						</button>
					</form:form>
					<script type="text/javascript">
						var id = '${paymentInfo.id}';
					</script>
					<c:if test="${paymentInfo.defaultPaymentInfo}">
						<spring:theme code="text.default" text="Default"/>
					</c:if>
				</div>
			</div>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<p class="emptyMessage"><spring:theme code="text.account.paymentDetails.noPaymentInformation" text="No Saved Payment Details"/></p>
	</c:otherwise>
</c:choose>