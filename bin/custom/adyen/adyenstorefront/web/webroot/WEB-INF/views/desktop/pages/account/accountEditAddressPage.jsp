<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/desktop/address"%>

<template:page pageTitle="${pageTitle}">

	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>

	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<nav:accountNav selected="address-book" />
	
	
	<div class="column accountContentPane clearfix">
		<div class="headline"><spring:theme code="text.account.addressBook.addressDetails" text="Address Details"/></div>
		<div class="required right"><spring:theme code="form.required" text="Fields marked * are required"/></div>
		<div class="description"><spring:theme code="text.account.addressBook.addEditform" text="Please use this form to add/edit an address."/></div>
		
		
		<address:addressFormSelector supportedCountries="${countries}" regions="${regions}"
									 cancelUrl="/my-account/address-book"/>
		
		<address:suggestedAddresses selectedAddressUrl="/my-account/select-suggested-address"/>
	
	
	</div>
	

</template:page>

