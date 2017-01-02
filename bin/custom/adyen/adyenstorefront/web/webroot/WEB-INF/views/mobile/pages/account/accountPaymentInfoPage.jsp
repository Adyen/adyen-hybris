<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<h2>
	<spring:theme code="text.account.paymentDetails" text="Payment Details"/>
</h2>
<c:if test="${empty paymentInfoData}">
	<p class="emptyMessage">
		<spring:theme code="text.account.paymentDetails.noPaymentInformation" text="You have no payment information"/>
	</p>
</c:if>
<c:if test="${not empty paymentInfoData}">
	<p>
		<spring:theme code="text.account.paymentDetails.managePaymentDetails" text="Manage your saved payment details."/>
	</p>
	<c:forEach items="${paymentInfoData}" var="paymentInfo">
		<div class="paymentInfoDataElement">
			<div class="ui-grid-a" data-theme="b" data-role="content">
				<div class="ui-block-a" style="width: 100%">
					<ul class="mFormList">
						<li>
							<h3>
								<spring:theme code="text.account.paymentDetails.paymentCard" text="Payment Card"/>
							</h3>
						</li>
						<c:if test="${paymentInfo.defaultPaymentInfo}">
							<li>
								<spring:theme code="text.account.paymentDetails.paymentCard.default" text="My Default Payment Card"/>
							</li>
						</c:if>
						<div class="ui-grid-a paymentCardInfoBlock" data-theme="b">
							<div class="ui-block-a">
								<spring:theme code="payment.cardType" text="Card Type"/>
								:
							</div>
							<div class="ui-block-b">${paymentInfo.cardType}</div>

							<div class="ui-block-a">
								<spring:theme code="payment.nameOnCard" text="Name on Card"/>
								:
							</div>
							<div class="ui-block-b">${paymentInfo.accountHolderName}</div>

							<div class="ui-block-a">
								<spring:theme code="payment.cardNumber" text="Card Number"/>
								:
							</div>
							<div class="ui-block-b">${paymentInfo.cardNumber}</div>

							<c:if test="${not empty paymentInfo.startMonth}">
								<div class="ui-block-a">
									<spring:theme code="text.start" text="Start Date: "/>
									:
								</div>
								<div class="ui-block-b">${paymentInfo.startMonth} / ${paymentInfo.startYear}</div>
							</c:if>

							<div class="ui-block-a">
								<spring:theme code="text.expires " text="Expiry Date"/>
								:
							</div>
							<div class="ui-block-b">${paymentInfo.expiryMonth} / ${paymentInfo.expiryYear}</div>

							<div class="ui-block-a">
								<spring:theme code="payment.issueNumber"/>
								:
							</div>
							<div class="ui-block-b">${paymentInfo.issueNumber}</div>
						</div>
					</ul>
				</div>
			</div>
			<div class="ui-grid-a" data-theme="b" data-role="content">
				<div class="ui-block-a" style="width: 100%">
					<ul class="mFormList">
						<h3>
							<spring:theme code="text.account.paymentDetails.billingAddress" text="Billing Address"/>
						</h3>
						<li>
							<b>
								<c:out value="${paymentInfo.billingAddress.title} ${paymentInfo.billingAddress.firstName} ${paymentInfo.billingAddress.lastName}"/>
							</b>
						</li>
						<li>${paymentInfo.billingAddress.line1}</li>
						<c:if test="${not empty paymentInfo.billingAddress.line2}">
							<li>${paymentInfo.billingAddress.line2}</li>
						</c:if>
						<li>${paymentInfo.billingAddress.town}</li>
						<li>${paymentInfo.billingAddress.postalCode}</li>
						<li>${paymentInfo.billingAddress.country.name}</li>
					</ul>
				</div>
			</div>
			<ul class="mFormList">
				<li>
					<fieldset class="ui-grid-a doubleButton">
						<div class="ui-block-a">
							<c:url value="/my-account/remove-payment-method" var="removePaymentCardFormAction"/>
							<form:form id="removePaymentCardForm${paymentInfo.id}" action="${removePaymentCardFormAction}" method="post" class="removePaymentCardForm">
								<input type="hidden" name="paymentInfoId" value="${paymentInfo.id}"/>
								<a href="#" data-role="button" data-theme="d" data-icon="delete" class="removePaymentCardButton" pid="${paymentInfo.id}"
								   data-message='<spring:theme code="text.paymentcard.remove.confirm"/>' data-headerText='<spring:theme code="text.headertext"/>'>
									<spring:theme code="text.remove" text="Remove"/>
								</a>
							</form:form>
						</div>
						<div class="ui-block-b">
							<c:if test="${not paymentInfo.defaultPaymentInfo}">
								<c:url value="/my-account/set-default-payment-details" var="setDefaultPaymentDetailsFormAction"/>
								<form:form id="setDefaultPaymentDetails${paymentInfo.id}" action="${setDefaultPaymentDetailsFormAction}" method="post">
									<input type="hidden" name="paymentInfoId" value="${paymentInfo.id}"/>
									<c:choose>
										<c:when test="${not paymentInfo.defaultPaymentInfo}">
											<a href="" data-role="button" data-theme="c" pid="${paymentInfo.id}" class="setDefaultPayment">
												<spring:theme code="text.setDefault" text="Set as default"/>
											</a>
										</c:when>
										<c:otherwise>
											<a data-role="text" data-theme="c">
												<spring:theme code="text.default" text="Default"/>
											</a>
										</c:otherwise>
									</c:choose>
								</form:form>
							</c:if>
						</div>
					</fieldset>
				</li>
			</ul>
		</div>
	</c:forEach>
</c:if>

