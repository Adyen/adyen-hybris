<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:url value="/cart/checkout" var="checkoutUrl" scope="session"/>
        <div class="row">
            <div class="col-md-6 col-lg-8"></div>
            <div class="col-md-6 col-lg-4">
                <div class="express-checkout">
                    <div class="headline"><spring:theme code="text.expresscheckout.header"/></div>
                    <strong><spring:theme code="text.expresscheckout.title"/></strong>
                    <ul>
                        <li><spring:theme code="text.expresscheckout.line1"/></li>
                        <li><spring:theme code="text.expresscheckout.line2"/></li>
                        <li><spring:theme code="text.expresscheckout.line3"/></li>
                    </ul>
                    <sec:authorize access="isFullyAuthenticated()">
                        <c:if test="${expressCheckoutAllowed}">
                            <div class="checkbox">
                                <label> 
                                    <c:url value="/checkout/multi/express" var="expressCheckoutUrl" scope="session"/>
                                    <input type="checkbox" class="express-checkout-checkbox" data-express-checkout-url="${expressCheckoutUrl}">
                                    <spring:theme text="I would like to Express checkout" code="cart.expresscheckout.checkbox"/>
                                </label>
                             </div>
                        </c:if>
                   </sec:authorize>

                </div>
                <div class="row">
                    <div class="col-sm-6">
                        <button class="btn btn-default btn-block continueShoppingButton" data-continue-shopping-url="${continueShoppingUrl}"><spring:theme text="Continue Shopping" code="cart.page.continue"/></button>
                    </div>
                    <div class="col-sm-6">
                        <button class="btn btn-primary btn-block checkoutButton"  data-checkout-url="${checkoutUrl}"><spring:theme code="checkout.checkout"/></button>
                    </div>
                </div>
            </div>
        </div>

    
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
                    <option value="default"><spring:theme code="checkout.checkout.multi.pci-ws"/></option>
                    <option value="hop"><spring:theme code="checkout.checkout.multi.pci-hop"/></option>
                </c:if>
                <option value="sop"><spring:theme code="checkout.checkout.multi.pci-sop" text="PCI-SOP" /></option>
            </select>
        </div>
    </div>
</c:if>

