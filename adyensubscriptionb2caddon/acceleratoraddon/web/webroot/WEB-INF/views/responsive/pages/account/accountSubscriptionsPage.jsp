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

<%-- Point billing at another card the shopper has already saved.

     Rendered on the SCOPE the connector declared, never on which billing platform this is. The note under
     the control is keyed by that scope, because "all your subscriptions" and "this subscription only" are
     different promises and only one of them is true on any given platform.

     Only cards already in the Adyen vault are offered. Adding a new one here would need zero-auth, which
     this integration cannot carry through 3DS, so a card requiring authentication could not be stored. --%>
<c:if test="${not empty paymentMethodSubscriptionCode and not empty storedCards}">
    <div class="account-section-content subscription-payment-method">
        <div class="subscription-payment-method-title">
            <spring:theme code="text.account.subscriptions.paymentMethod"/>
        </div>
        <form:form action="${request.contextPath}/my-account/subscriptions/payment-method" method="post">
            <input type="hidden" name="${CSRFToken.parameterName}" value="${CSRFToken.token}"/>
            <%-- Chosen by the facade: it has to be a row whose connector offers the change, which is in a
                 state where the change means something, and which carries a public identifier. "First on
                 screen" satisfies none of those reliably. --%>
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
                <spring:theme code="text.account.subscriptions.paymentMethod.note.${paymentMethodChangeScope}"/>
            </div>
        </form:form>
    </div>
</c:if>

<%-- The honest third state. A shopper with subscriptions on a platform that cannot do this reads one
     sentence instead of looking at a blank space and wondering, and is never shown a control that would
     fail. Not rendered when there is nothing on the page to change. --%>
<c:if test="${paymentMethodChangeScope eq 'NOT_SUPPORTED' and not empty subscriptions}">
    <div class="account-section-content subscription-payment-method-unavailable">
        <spring:theme code="text.account.subscriptions.paymentMethod.unsupported"/>
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

                                <%-- Two facts, two sentences. They used to be one: the order CODE was fed
                                     into "Ordered on {0}", which rendered "Ordered on 00012345" while the
                                     date the sentence wanted sat unused on the DTO. The date is what the
                                     shopper reads; the number is what they quote to support, and it is
                                     shown even when the date is missing. --%>
                                <c:if test="${not empty subscription.orderDate}">
                                    <div class="subscription-order">
                                        <fmt:formatDate value="${subscription.orderDate}" dateStyle="long"
                                                        var="orderedOn"/>
                                        <spring:theme code="text.account.subscriptions.order"
                                                      arguments="${orderedOn}"/>
                                    </div>
                                </c:if>
                                <c:if test="${not empty subscription.orderCode}">
                                    <div class="subscription-order-number">
                                        <spring:theme code="text.account.subscriptions.orderNumber"
                                                      arguments="${fn:escapeXml(subscription.orderCode)}"/>
                                    </div>
                                </c:if>

                                <%-- Recognition only, and it names the card the order was paid with rather
                                     than the one billing uses now: the change is offered once above the
                                     list, not per row, so nothing here invites the shopper to act on it. --%>
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
