<%@ attribute name="paymentMethod" required="true" type="java.lang.String" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/checkout/multi/adyen/summary/component-result" var="handleComponentResult"/>

<%-- Paypal button --%>
<c:if test="${selectedPaymentMethod eq 'paypal'}">
    <div class="checkbox">
        <label>
            <input type="checkbox" id="terms-conditions-check" />
            <spring:theme code="checkout.summary.placeOrder.readTermsAndConditions" arguments="${getTermsAndConditionsUrl}" text="Terms and Conditions"/>
        </label>
    </div>
    <div id="adyen-paypal-container"></div>
</c:if>

<c:if test="${selectedPaymentMethod eq 'mbway'}">
    <div class="chckt-pm__header js-chckt-pm__header">
        <spring:theme code="checkout.summary.component.mbway.payment"/>
    </div>
    <div id="adyen-component-container"></div>
</c:if>

<form:form id="handleComponentResultForm"
           class="create_update_payment_form"
           action="${handleComponentResult}"
           method="post">
    <input type="hidden" id="resultData" name="resultData"/>
    <input type="hidden" id="isResultError" name="isResultError" value="false"/>
</form:form>

