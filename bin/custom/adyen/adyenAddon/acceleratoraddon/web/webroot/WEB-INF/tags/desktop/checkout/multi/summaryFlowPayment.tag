<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="paymentInfo" required="true" type="de.hybris.platform.commercefacades.order.data.CCPaymentInfoData" %>
<%@ attribute name="requestSecurityCode" required="true" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set value="${not empty paymentInfo and not empty paymentInfo.billingAddress}" var="billingAddressOk"/>
<spring:theme code="checkout.summary.paymentMethod.securityCode.whatIsThis.description" var="securityWhatText"/>

<div class="summaryPayment clearfix"  data-security-what-text="${securityWhatText}">
    <ycommerce:testId code="checkout_paymentDetails_text">
            <div class="column append-1">
				<strong><spring:theme code="checkout.summary.paymentMethod.header" htmlEscape="false"/></strong>
				<c:choose>
				   <c:when test="${paymentInfo.useHPP}">
					   <c:if test="${not empty paymentInfo.adyenPaymentBrand}"> 
			                <ul>
			                    <li><spring:theme code="checkout.multi.paymentMethod.summary.hpp.selected.payment.method" htmlEscape="false"/>${cartData.paymentInfo.adyenPaymentBrand}</li>
			                </ul>
		                 </c:if>
		                 <c:if test="${empty paymentInfo.adyenPaymentBrand}">
			                <ul>
			                    <li><spring:theme code="checkout.multi.paymentMethod.summary.pay.by.hpp" htmlEscape="false"/></li>
			                </ul>
                 		</c:if>
				   </c:when>
				   <c:otherwise>  
				      <c:if test="${empty boletoUrl}">
		                <ul>
		                    <li>${fn:escapeXml(paymentInfo.accountHolderName)}</li>
		                    <li>${fn:escapeXml(paymentInfo.cardNumber)}</li>
							<c:if test="${not empty paymentInfo.adyenPaymentBrand}">
								<li>${fn:escapeXml(paymentInfo.adyenPaymentBrand)}</li>
							</c:if>
		                    <li><spring:theme code="checkout.summary.paymentMethod.paymentDetails.expires" arguments="${paymentInfo.expiryMonth},${paymentInfo.expiryYear}"/></li>
		                </ul>
		                </c:if>
		                <c:if test="${not empty boletoUrl}">
		                <ul>
		                    <li>Boleto</li>
		                    <%-- <li><a href="${boletoUrl}" title="<spring:theme code="checkout.multi.paymentMethod.summary.download.boleto.pdf" htmlEscape="false"/>" class="button positive" style="height:auto;" target="_blank"><spring:theme code="checkout.multi.paymentMethod.summary.download.boleto.pdf.button" htmlEscape="false"/></a></li>    --%>
		                </ul>
		                </c:if>
		                <%-- <c:if test="${requestSecurityCode}">
						 <form>
							 <div class="control-group security">
								 <label for="SecurityCode"><spring:theme code="checkout.summary.paymentMethod.securityCode"/>*</label>
								 <div class="controls">
									<input type="text" class="text security" id="SecurityCode"/>
									<a href="#" class="security_code_what"><spring:theme code="checkout.summary.paymentMethod.securityCode.whatIsThis"/></a>
								 </div>
							 </div>
						 </form>
		                </c:if> --%>
				   </c:otherwise>
				 </c:choose>
            </div>
		
            <div class="column">
                <ul>
                    <c:if test="${billingAddressOk}">
                        <li><strong><spring:theme code="checkout.summary.paymentMethod.billingAddress.header"/></strong></li>
                        <li>
                            <c:if test="${not empty paymentInfo.billingAddress.title}">${fn:escapeXml(paymentInfo.billingAddress.title)}&nbsp;</c:if>
                                ${fn:escapeXml(paymentInfo.billingAddress.firstName)}&nbsp;${fn:escapeXml(paymentInfo.billingAddress.lastName)}
                        </li>
                        <li>${fn:escapeXml(paymentInfo.billingAddress.line1)}</li>
                        <li>${fn:escapeXml(paymentInfo.billingAddress.line2)}</li>
                        <li>${fn:escapeXml(paymentInfo.billingAddress.region.name)}&nbsp;${fn:escapeXml(paymentInfo.billingAddress.town)}</li>
                        <li>${fn:escapeXml(paymentInfo.billingAddress.postalCode)}</li>
                        <li>${fn:escapeXml(paymentInfo.billingAddress.country.name)}</li>
                    </c:if>
                </ul>
            </div>
    </ycommerce:testId>
    <ycommerce:testId code="checkout_changePayment_element">
	    <c:url value="/checkout/multi/add-payment-method" var="addPaymentMethodUrl"/>
        <a href="${addPaymentMethodUrl}" class="button positive editButton"><spring:theme code="checkout.summary.edit"/></a>
    </ycommerce:testId>
</div>
