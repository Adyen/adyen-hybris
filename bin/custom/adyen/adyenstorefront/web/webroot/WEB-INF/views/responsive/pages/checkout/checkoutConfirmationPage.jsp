<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>
<%@ taglib prefix="multi" tagdir="/WEB-INF/tags/addons/b2ccheckoutaddon/responsive/checkout/multi" %>


<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

    <c:url value="${continueUrl}" var="continueShoppingUrl" scope="session"/>
    
    <cms:pageSlot position="TopContent" var="feature" element="div">
            <cms:component component="${feature}"/>
    </cms:pageSlot>


	<div class="container">
		<div class="checkout-success">
			<div class="checkout-success-headline">
				<span class="glyphicon glyphicon-lock"></span> <spring:theme code="checkout.orderConfirmation.checkoutSuccessful" />
			</div>

			<div class="checkout-success-body">
				<div class="checkout-success-body-headline"><spring:theme code="checkout.orderConfirmation.thankYouForOrder" /></div>
				<p><spring:theme code="text.account.order.orderNumber" text="Order number is {0}" arguments="${orderData.code}"/></p>
				<p><spring:theme code="checkout.orderConfirmation.copySentTo" arguments="${email}"/></p>
                
                <p><multi:pickupGroups2 orderData="${orderData}"/></p>
                
			    <button class="btn btn-default continueShoppingButton" data-continue-shopping-url="${continueShoppingUrl}"><spring:theme code="checkout.orderConfirmation.continueShopping" /></button>
            
			</div>

		</div>
	</div>
	
	            
	
    <cms:pageSlot position="SideContent" var="feature" element="div">
        <cms:component component="${feature}"/>
    </cms:pageSlot>
</template:page>

