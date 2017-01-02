<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>


<c:if test="${not empty cartData.entries}">

    <c:url value="/cart/checkout" var="checkoutUrl" scope="session"/>
    <c:url value="${continueUrl}" var="continueShoppingUrl" scope="session"/>
        
    <c:set var="showTax" value="false"/>

   
                <div class="cart-top-totals">
                <c:choose>
                	<c:when test="${fn:length(cartData.entries) > 1}">
                		<spring:theme code="basket.page.totals.total.items" text="Total: (${fn:length(cartData.entries)} items)" arguments="${fn:length(cartData.entries)}"/> 
                	</c:when>
                	<c:otherwise>
                		<spring:theme code="basket.page.totals.total.items.one" text="Total: (${fn:length(cartData.entries)} item)" arguments="${fn:length(cartData.entries)}"/>
                	</c:otherwise> 
                </c:choose> 
            <ycommerce:testId code="cart_totalPrice_label">
                <c:choose>
                    <c:when test="${showTax}">
                        <format:price priceData="${cartData.totalPriceWithTax}"/>
                    </c:when>
                    <c:otherwise>
                        <format:price priceData="${cartData.totalPrice}"/>
                    </c:otherwise>
                </c:choose>
            </ycommerce:testId></div>
                <div class="row">
                    <div class="col-sm-6 col-md-3 col-md-push-6">
                        <button class="btn btn-default btn-block continueShoppingButton" data-continue-shopping-url="${continueShoppingUrl}"><spring:theme text="Continue Shopping" code="cart.page.continue"/></button>
                    </div>
                    <div class="col-sm-6 col-md-3 col-md-push-6">
                        <ycommerce:testId code="checkoutButton">
                            <button class="btn btn-primary btn-block checkoutButton" data-checkout-url="${checkoutUrl}"><spring:theme code="checkout.checkout"/></button>
                        </ycommerce:testId>
                    </div>
                </div>
      

	   <cart:cartItems cartData="${cartData}"/>

</c:if>

