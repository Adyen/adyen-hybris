<%@ attribute name="paymentMethod" required="true" type="java.lang.String" %>
<%@ attribute name="label" required="true" type="java.lang.String" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:url value="/checkout/multi/adyen/summary/placeOrder" var="placeOrderUrl"/>
<spring:url value="/checkout/multi/termsAndConditions" var="getTermsAndConditionsUrl"/>

<%-- Components --%>
<c:if test="${selectedPaymentMethod eq 'mbway' || selectedPaymentMethod eq 'paypal' || selectedPaymentMethod eq 'applepay'  || selectedPaymentMethod eq 'pix'}">
    <%-- Render Paypal or Apple Pay button --%>
    <c:if test="${selectedPaymentMethod eq 'paypal' || selectedPaymentMethod eq 'applepay' || selectedPaymentMethod eq 'pix'}">
        <div class="checkbox">
            <label>
                <input type="checkbox" id="terms-conditions-check-${label}" />
                <spring:theme var="readTermsAndConditions" code="checkout.summary.placeOrder.readTermsAndConditions" arguments="${fn:escapeXml(getTermsAndConditionsUrl)}" htmlEscape="false"/>
                    ${ycommerce:sanitizeHTML(readTermsAndConditions)}
            </label>
        </div>

        <c:choose>
            <c:when test="${selectedPaymentMethod eq 'pix'}">
                <button id="generateqr-${label}" type="submit" class="btn btn-primary btn-block">
                    <spring:theme code="checkout.summary.component.pix.generateqr" text="Generate QR Code" />
                </button>
                <div id="pix-container-${label}"></div>
            </c:when>
            <c:otherwise>
                <div id="adyen-component-button-container-${label}"></div>
            </c:otherwise>
        </c:choose>
    </c:if>

    <c:if test="${selectedPaymentMethod eq 'mbway'}">
        <div class="chckt-pm__header js-chckt-pm__header">
            <spring:theme code="checkout.summary.component.mbway.payment"/>
        </div>
        <div id="adyen-component-container-${label}"></div>
    </c:if>
</c:if>

<%-- Paypal and Apple Pay has it's own button --%>
<c:if test="${selectedPaymentMethod ne 'paypal' && selectedPaymentMethod ne 'applepay' && selectedPaymentMethod ne 'pix'}">
    <form:form action="${placeOrderUrl}" id="placeOrderForm-${label}" modelAttribute="placeOrderForm">
        <div class="checkbox">
            <label> <form:checkbox id="terms-conditions-check-${label}" path="termsCheck" />
                <spring:theme var="readTermsAndConditions" code="checkout.summary.placeOrder.readTermsAndConditions" arguments="${fn:escapeXml(getTermsAndConditionsUrl)}" htmlEscape="false"/>
                    ${ycommerce:sanitizeHTML(readTermsAndConditions)}
            </label>
        </div>
    </form:form>

    <button id="placeOrder-${label}" type="submit" class="btn btn-primary btn-place-order btn-block">
        <spring:theme code="checkout.summary.placeOrder" text="Place Order" />
    </button>
</c:if>

