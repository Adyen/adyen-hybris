<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/mobile/address"%>

<template:page pageTitle="${pageTitle}">
	<jsp:body>
		<nav:myaccountNav/>
		<div class="item_container_holder" data-content-theme="d" data-theme="e">
			<div id="globalMessages" data-theme="e">
				<common:globalMessages/>
			</div>
			<cms:pageSlot position="TopContent" var="feature" element="div">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
			<div data-content-theme="d" data-theme="e">
				<h3>
					<spring:theme code="text.account.addressBook.addressDetails" text="Address Details"/>
				</h3>
				<p>

				<div class="item_container">
					<p class="continuous-text">
						<spring:theme code="text.account.addressBook.addEditform" text="Please use this form to add/edit an address."/>
					</p>
					<p class="continuous-text">
						<spring:theme code="form.required" text="Fields marked * are required"/>
					</p>

					<address:addressFormSelector supportedCountries="${countries}" regions="${regions}"
					                             cancelUrl="/my-account/address-book"/>

					<address:suggestedAddresses selectedAddressUrl="/my-account/select-suggested-address"/>
				</div>
			</div>
			<cms:pageSlot position="BottomContent" var="feature" element="div" id="bottom-disp-img" class="home-disp-img">
				<cms:component component="${feature}" element="div" class="span-24 section1 cms_disp-img_slot"/>
			</cms:pageSlot>
		</div>
	</jsp:body>
</template:page>
