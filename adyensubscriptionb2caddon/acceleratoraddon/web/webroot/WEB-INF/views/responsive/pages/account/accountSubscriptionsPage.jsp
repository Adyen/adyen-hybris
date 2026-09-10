<%--
    The "My subscriptions" page body.

    A fragment, not a page: it is rendered inside a ContentSlot by a JspIncludeComponent, so there is no
    <template:page> here. Wrapping it in one produces a page inside a page, or nothing at all.

    Every state the shopper can be in has its own wording, and only some of them carry a date - a
    subscription has no known period until the first platform read, so "renews on" has to have an answer for
    when nobody knows yet.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<div class="account-section-header">
    <spring:theme code="text.account.subscriptions"/>
</div>

<%-- Orders that were paid for and never became a subscription. Shown above the list and never instead of
     it: this shopper may also have subscriptions that worked. Without this they would be told they have
     nothing, which is the one answer that is certainly wrong. --%>
<c:if test="${not empty ordersAwaitingSetup}">
    <div class="account-section-content">
        <c:forEach items="${ordersAwaitingSetup}" var="orderCode">
            <div class="alert alert-warning">
                <spring:theme code="text.account.subscriptions.awaitingSetup"
                              arguments="${fn:escapeXml(orderCode)}"/>
            </div>
        </c:forEach>
    </div>
</c:if>

<%-- Proof of concept: point billing at another card the shopper has already saved.

     Placed once, above the list, and not on each row - on Chargebee a payment source belongs to the
     customer, so this moves every subscription they have. Rendering it per row would say otherwise.

     Only cards already in the Adyen vault are offered. Adding a new one here would need zero-auth, which
     in this integration carries no 3DS, so a card requiring authentication could not be stored at all. --%>
<c:if test="${not empty paymentMethodSubscriptionCode and not empty storedCards}">
    <div class="account-section-content subscription-payment-method">
        <div class="subscription-payment-method-title">
            <spring:theme code="text.account.subscriptions.paymentMethod"/>
        </div>
        <form:form action="${request.contextPath}/my-account/subscriptions/payment-method" method="post">
            <input type="hidden" name="${CSRFToken.parameterName}" value="${CSRFToken.token}"/>
            <%-- Any of the shopper's own subscriptions identifies the customer and the store, but it must
                 be one that has a public identifier - the facade picks it, because "first on screen" can be
                 a row created before that column existed, whose empty code can only be refused. --%>
            <input type="hidden" name="code" value="${fn:escapeXml(paymentMethodSubscriptionCode)}"/>
            <select name="storedPaymentMethodId" class="form-control">
                <c:forEach items="${storedCards}" var="storedCard">
                    <option value="${fn:escapeXml(storedCard.id)}">
                        ${fn:escapeXml(storedCard.brand)}&nbsp;&bull;&bull;&bull;&bull;&nbsp;${fn:escapeXml(storedCard.lastFour)}
                    </option>
                </c:forEach>
            </select>
            <button type="submit" class="btn btn-default">
                <spring:theme code="text.account.subscriptions.paymentMethod.submit"/>
            </button>
            <div class="subscription-payment-method-note">
                <spring:theme code="text.account.subscriptions.paymentMethod.note"/>
            </div>
        </form:form>
    </div>
</c:if>

<c:choose>
    <c:when test="${not empty subscriptions}">
        <div class="account-section-content account-list">
            <div class="account-orders-history">
                <c:forEach items="${subscriptions}" var="subscription">
                    <div class="account-cards card-select subscription-entry">
                        <div class="row">
                            <div class="col-xs-12 col-sm-7">
                                <div class="subscription-name">
                                    <c:choose>
                                        <c:when test="${not empty subscription.productName}">
                                            ${fn:escapeXml(subscription.productName)}
                                        </c:when>
                                        <c:otherwise>
                                            <spring:theme code="text.account.subscriptions.unnamed"/>
                                        </c:otherwise>
                                    </c:choose>
                                    <c:if test="${subscription.quantity gt 1}">
                                        <span class="subscription-quantity">
                                            &times;&nbsp;${fn:escapeXml(subscription.quantity)}
                                        </span>
                                    </c:if>
                                </div>

                                <%-- The state's own sentence. The key carries ".dated" only when a date is
                                     actually known, so nothing ever renders an empty "until". --%>
                                <div class="subscription-status">
                                    <c:set var="stateKey"
                                           value="text.account.subscriptions.state.${fn:toLowerCase(subscription.state)}"/>
                                    <c:choose>
                                        <c:when test="${not empty subscription.effectiveDate}">
                                            <fmt:formatDate value="${subscription.effectiveDate}"
                                                            dateStyle="long" var="effectiveDate"/>
                                            <spring:theme code="${stateKey}.dated" arguments="${effectiveDate}"/>
                                        </c:when>
                                        <c:otherwise>
                                            <spring:theme code="${stateKey}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <c:if test="${not empty subscription.orderCode}">
                                    <div class="subscription-order">
                                        <spring:theme code="text.account.subscriptions.order"
                                                      arguments="${fn:escapeXml(subscription.orderCode)}"/>
                                    </div>
                                </c:if>

                                <%-- Recognition only. Changing the card is not offered on either platform,
                                     so nothing here invites the shopper to try. --%>
                                <c:if test="${not empty subscription.paymentMethodSummary}">
                                    <div class="subscription-payment">
                                        <spring:theme code="text.account.subscriptions.paidWith"
                                                      arguments="${fn:escapeXml(subscription.paymentMethodSummary)}"/>
                                    </div>
                                </c:if>
                            </div>

                            <div class="col-xs-12 col-sm-5 subscription-actions">
                                <c:if test="${subscription.cancellable}">
                                    <ycommerce:testId code="subscription_cancel_button">
                                        <form:form action="${request.contextPath}/my-account/subscriptions/cancel"
                                                   method="post">
                                            <%-- Added by hand. This storefront configures its own CSRF
                                                 parameter name, so Spring's form tag does not supply it,
                                                 and POST is the only method the matcher checks - omitting
                                                 this makes the button 403 rather than leaving it
                                                 unprotected. --%>
                                            <input type="hidden" name="${CSRFToken.parameterName}"
                                                   value="${CSRFToken.token}"/>
                                            <input type="hidden" name="code"
                                                   value="${fn:escapeXml(subscription.code)}"/>
                                            <button type="submit" class="btn btn-default btn-block">
                                                <spring:theme code="text.account.subscriptions.cancel"/>
                                            </button>
                                            <div class="subscription-cancel-note">
                                                <spring:theme code="text.account.subscriptions.cancel.note"/>
                                            </div>
                                        </form:form>
                                    </ycommerce:testId>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </c:when>
    <c:when test="${empty ordersAwaitingSetup}">
        <div class="account-section-content content-empty">
            <spring:theme code="text.account.subscriptions.empty"/>
        </div>
    </c:when>
</c:choose>
