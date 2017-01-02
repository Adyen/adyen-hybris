<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/responsive/checkout/multi"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

	<div class="checkout-headline"><spring:theme code="checkout.multi.secure.checkout" text="Secure Checkout"></spring:theme></div>
	<div class="row">
		<multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
			<jsp:body>
				<ycommerce:testId code="checkoutStepOne">
					<div class="checkout-shipping">
							<multi-checkout:shipmentItems cartData="${cartData}" showDeliveryAddress="false" />

							<div class="checkout-indent">
								<div class="headline"><spring:theme code="checkout.summary.shippingAddress" text="Shipping Address"></spring:theme></div>


									<address:addressFormSelector supportedCountries="${countries}"
										regions="${regions}" cancelUrl="${currentStepUrl}"
										country="${country}" />

										<div id="addressbook">

											<c:forEach items="${deliveryAddresses}" var="deliveryAddress" varStatus="status">
												<div class="addressEntry">
													<form action="${request.contextPath}/checkout/multi/delivery-address/select" method="GET">
														<input type="hidden" name="selectedAddressCode" value="${deliveryAddress.id}" />
														<ul>
															<li>
																<strong>${fn:escapeXml(deliveryAddress.title)}&nbsp;
																${fn:escapeXml(deliveryAddress.firstName)}&nbsp;
																${fn:escapeXml(deliveryAddress.lastName)}</strong>
																<br>
																${fn:escapeXml(deliveryAddress.line1)}&nbsp;
																${fn:escapeXml(deliveryAddress.line2)}
																<br>
																${fn:escapeXml(deliveryAddress.town)}
																<c:if test="${not empty deliveryAddress.region.name}">
																	&nbsp;${fn:escapeXml(deliveryAddress.region.name)}
																</c:if>
																<br>
																${fn:escapeXml(deliveryAddress.country.name)}&nbsp;
																${fn:escapeXml(deliveryAddress.postalCode)}
															</li>
														</ul>
														<button type="submit" class="btn btn-primary btn-block">
															<spring:theme
																code="checkout.multi.deliveryAddress.useThisAddress"
																text="Use this Address" />
														</button>
													</form>
												</div>
											</c:forEach>
										</div>

										<address:suggestedAddresses selectedAddressUrl="/checkout/multi/delivery-address/select" />
							</div>

								<multi-checkout:pickupGroups cartData="${cartData}" />
					</div>


					<button id="addressSubmit" type="button"
						class="btn btn-primary btn-block checkout-next"><spring:theme code="checkout.multi.deliveryAddress.continue" text="Next"/></button>
				</ycommerce:testId>
			</jsp:body>


			
		</multi-checkout:checkoutSteps>
		
		<multi-checkout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="false" showPaymentInfo="false" showTaxEstimate="false" showTax="true" />

		<div class="col-sm-12 col-lg-9">
			<cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</div>
	</div>

</template:page>
