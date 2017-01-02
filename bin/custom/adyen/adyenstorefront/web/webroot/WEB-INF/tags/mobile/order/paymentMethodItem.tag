<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div data-theme="b" data-role="content">
	<div data-theme="b">
		<h4 class="subItemHeader">
			<spring:theme code="text.paymentMethod" text="Payment Method" />
		</h4>
	</div>
	<div data-theme="b">
		<ul class="mFormList">
			<li>${order.paymentInfo.cardNumber}</li>
			<li>${order.paymentInfo.cardTypeData.name}</li>
			<li>
				<spring:theme code="paymentMethod.paymentDetails.expires" arguments="${order.paymentInfo.expiryMonth},${order.paymentInfo.expiryYear}" />
			</li>
		</ul>
	</div>
</div>
