<%@ attribute name="paymentMethod" required="true" type="java.lang.String" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:url value="/checkout/multi/adyen/summary/component-result" var="handleComponentResult"/>

<c:choose>
    <%-- Paypal button --%>
    <c:when test="${selectedPaymentMethod eq 'paypal'}">
        <div class="checkbox">
            <label>
                <input type="checkbox" id="Terms1" />
                <spring:theme code="checkout.summary.placeOrder.readTermsAndConditions" arguments="${getTermsAndConditionsUrl}" text="Terms and Conditions"/>
            </label>
        </div>
        <div id="adyen-paypal-container"></div>
    </c:when>

    <c:otherwise>
        <div class="chckt-pm__header js-chckt-pm__header">
            <span class="chckt-cp__image">
                <img width="40" src="https://checkoutshopper-live.adyen.com/checkoutshopper/img/pm/${paymentMethod}@2x.png" alt="">
                <span class="chckt-cp__image-border"></span>
            </span>
            <spring:theme code="checkout.summary.component.payment"/>
        </div>
        <div id="adyen-component-container"></div>
    </c:otherwise>
</c:choose>

<form:form id="handleComponentResultForm"
           class="create_update_payment_form"
           action="${handleComponentResult}"
           method="post">
    <input type="hidden" id="resultData" name="resultData"/>
    <input type="hidden" id="isResultError" name="isResultError" value="false"/>
</form:form>

