<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


	<div class="headline"><spring:theme code="text.paymentDetails" text="Payment Details"/></div>
	<c:choose>
	   <c:when test="${order.paymentInfo.useHPP}">
		   <c:if test="${not empty order.paymentInfo.adyenPaymentBrand}"> 
                <ul>
                    <li><spring:theme code="checkout.multi.paymentMethod.summary.hpp.selected.payment.method" htmlEscape="false"/>${order.paymentInfo.adyenPaymentBrand}</li>
                </ul>
           </c:if>
           <c:if test="${empty order.paymentInfo.adyenPaymentBrand}">
                <ul>
                    <li><spring:theme code="checkout.multi.paymentMethod.summary.pay.by.hpp" htmlEscape="false"/></li>
                </ul>
           </c:if>
	   </c:when>
	   <c:otherwise>  
			<c:if test="${empty order.paymentInfo.boletoPdfUrl}">
			    <ul>
			        <li>${fn:escapeXml(order.paymentInfo.cardNumber)}</li>
			        <li>${fn:escapeXml(order.paymentInfo.cardTypeData.name)}</li>
			        <li><spring:theme code="paymentMethod.paymentDetails.expires" arguments="${fn:escapeXml(order.paymentInfo.expiryMonth)},${fn:escapeXml(order.paymentInfo.expiryYear)}"/></li>
			    </ul>
				</c:if>
				<c:if test="${not empty order.paymentInfo.boletoPdfUrl}">
				<ul>
			        <li>Boleto</li>
			        <li><a href="${order.paymentInfo.boletoPdfUrl}" title="<spring:theme code="checkout.multi.paymentMethod.summary.download.boleto.pdf" htmlEscape="false"/>" class="button positive" style="height:auto;" target="_blank"><spring:theme code="checkout.multi.paymentMethod.summary.download.boleto.pdf.button" htmlEscape="false"/></a></li>
			    </ul>
			</c:if>						   
	   </c:otherwise>
	 </c:choose>
