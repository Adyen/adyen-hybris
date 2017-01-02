<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>

<cart:cartExpressCheckoutEnabled />
<a class="button" href="${continueShoppingUrl}">
	<spring:theme text="Continue Shopping" code="cart.page.continue"/>
</a>
<button id="checkoutButtonBottom" class="doCheckoutBut positive right continueCheckout" type="button" data-checkout-url="${checkoutUrl}">
	<spring:theme code="checkout.checkout" />
</button>
<c:if test="${showCheckoutStrategies && not empty cartData.entries}" >
	<div class="span-24">
		<div class="right">
			<input type="hidden" name="flow" id="flow"/>
			<input type="hidden" name="pci" id="pci"/>
			<select id="selectAltCheckoutFlow" class="doFlowSelectedChange">
				<option value="multistep"><spring:theme code="checkout.checkout.flow.select"/></option>
				<option value="multistep"><spring:theme code="checkout.checkout.multi"/></option>
				<option value="multistep-pci"><spring:theme code="checkout.checkout.multi.pci"/></option>
			</select>
			<select id="selectPciOption" style="margin-left: 10px; display: none;">
				<option value=""><spring:theme code="checkout.checkout.multi.pci.select"/></option>
				<c:if test="${!isOmsEnabled}">
					<option value="hop"><spring:theme code="checkout.checkout.multi.pci-hop"/></option>
				</c:if>
				<option value="sop"><spring:theme code="checkout.checkout.multi.pci-sop" text="PCI-SOP" /></option>
			</select>
		</div>
	</div>
</c:if>
