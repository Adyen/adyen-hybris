<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="showTax" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="cart-totals">
    <div class="row">
        <div class="col-xs-8 col-sm-9 col-md-10 col-lg-11"><spring:theme code="basket.page.totals.subtotal"/></div>
        <div class="col-xs-4 col-sm-3 col-md-2 col-lg-1"><ycommerce:testId code="Order_Totals_Subtotal"><format:price priceData="${cartData.subTotal}"/></ycommerce:testId></div>
                
                
        <c:if test="${not empty cartData.deliveryCost}">       
        	<div class="col-xs-8 col-sm-9 col-md-10 col-lg-11"><spring:theme code="basket.page.totals.delivery"/></div>
        	<div class="col-xs-4 col-sm-3 col-md-2 col-lg-1"><format:price priceData="${cartData.deliveryCost}" displayFreeForZero="TRUE"/></div>
         </c:if>    
        
        
        <c:if test="${cartData.net && cartData.totalTax.value > 0 && showTax}">
            <div class="col-xs-8 col-sm-9 col-md-10 col-lg-11"><spring:theme code="basket.page.totals.netTax"/></div>
            <div class="col-xs-4 col-sm-3 col-md-2 col-lg-1"><format:price priceData="${cartData.totalTax}"/></div>
        </c:if>



        <c:if test="${cartData.totalDiscounts.value > 0}">
            <div class="col-xs-8 col-sm-9 col-md-10 col-lg-11 discount"><spring:theme code="basket.page.totals.savings"/></div>
            <div class="col-xs-4 col-sm-3 col-md-2 col-lg-1 discount">
                -<ycommerce:testId code="Order_Totals_Savings"><format:price priceData="${cartData.totalDiscounts}"/></ycommerce:testId>
            </div>         
        </c:if> 
        
        <div class="col-xs-8 col-sm-9 col-md-10 col-lg-11 grand-total"><spring:theme code="basket.page.totals.total"/></div>
        <div class="col-xs-4 col-sm-3 col-md-2 col-lg-1 grand-total">
            <ycommerce:testId code="cart_totalPrice_label">
                <c:choose>
                    <c:when test="${showTax}">
                        <format:price priceData="${cartData.totalPriceWithTax}"/>
                    </c:when>
                    <c:otherwise>
                        <format:price priceData="${cartData.totalPrice}"/>
                    </c:otherwise>
                </c:choose>
            </ycommerce:testId>
        </div>

        
        <c:if test="${not cartData.net}">
            <div class=""> 
                <ycommerce:testId code="cart_taxes_label"><spring:theme code="basket.page.totals.grossTax" arguments="${cartData.totalTax.formattedValue}" argumentSeparator="!!!!"/></ycommerce:testId>
             </div>
        </c:if>
        

        <c:if test="${cartData.net && not showTax }">
            <div class="">
                <ycommerce:testId code="cart_taxes_label"><spring:theme code="basket.page.totals.noNetTax"/></ycommerce:testId>
            </div>
        </c:if>
        
    </div>
</div>
