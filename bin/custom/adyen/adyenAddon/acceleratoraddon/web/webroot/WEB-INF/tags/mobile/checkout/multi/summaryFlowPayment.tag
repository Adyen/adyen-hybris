<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="paymentInfo" required="true" type="de.hybris.platform.commercefacades.order.data.CCPaymentInfoData"%>
<%@ attribute name="requestSecurityCode" required="true" type="java.lang.Boolean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:set value="${not empty paymentInfo}" var="paymentInfoOk" />
<c:set value="${not empty paymentInfo and not empty paymentInfo.billingAddress}" var="billingAddressOk" />
<div data-theme="b" data-role="content">
	<h6 class="descriptionHeadline">
		<spring:theme code="text.headline.orderinfo" text="All information about delivery address, delivery method and payment method" />
	</h6>
	<div class="checkout_summary_flow_c ${paymentInfoOk ? 'complete' : ''}" id="checkout_summary_payment_div">
		<ycommerce:testId code="checkout_paymentDetails_text">
			<div class="ui-grid-a" data-theme="b">
				<h4 class="subItemHeader">
					<spring:theme code="checkout.summary.paymentMethod.header" htmlEscape="false" />
					<span></span>
				</h4>
			</div>
			<div class="ui-grid-a" data-theme="b">
				<div class="ui-block-a" style="width: 85%">
					<ul class="mFormList">
						<c:choose>
							<c:when test="${paymentInfoOk}">
								<li>${paymentInfo.accountHolderName}</li>
								<li>${paymentInfo.cardNumber}</li>
								<li>${paymentInfo.cardType}</li>
								<li><spring:theme code="checkout.summary.paymentMethod.paymentDetails.expires" arguments="${paymentInfo.expiryMonth},${paymentInfo.expiryYear}" /></li>
							</c:when>
							<c:otherwise>
								<li><spring:theme code="checkout.summary.paymentMethod.paymentDetails.noneSelected" /></li>
							</c:otherwise>
						</c:choose>
						<br />
						<c:if test="${requestSecurityCode and paymentInfoOk}">
							<li>
								<form>
									<div class="ui-grid-a" data-theme="b">
										<div class="ui-block-a" style="width: 60%">
											<spring:theme code="checkout.summary.paymentMethod.securityCode" />
											<a href="#" id="cvv2Description" data-cvv2description="<spring:theme code='checkout.summary.paymentMethod.securityCode.whatIsThis.description'/>">
												<br />
												<spring:theme code="checkout.summary.paymentMethod.securityCode.whatIsThis" />
											</a>
										</div>
										<div class="ui-block-b" style="width: 40%">
											<input type="text" class="text security" id="SecurityCode" maxlength="5" style="width: 50px;" /><br />
										</div>
									</div>
								</form>
							</li>
						</c:if>
					</ul>
				</div>
				<div class="ui-block-b" style="width: 15%">
					<ycommerce:testId code="checkout_changePayment_element">
						<c:if test="${paymentInfoOk}">
							<c:url value="${currentStepUrl}" var="addPaymentMethodUrl" />
							<a href="${addPaymentMethodUrl}" class="edit_complete change_payment_method_button" data-theme="c">
								<spring:theme code="mobile.checkout.edit.link" />
							</a>
						</c:if>
					</ycommerce:testId>
				</div>
			</div>
		</ycommerce:testId>
	</div>
</div>
