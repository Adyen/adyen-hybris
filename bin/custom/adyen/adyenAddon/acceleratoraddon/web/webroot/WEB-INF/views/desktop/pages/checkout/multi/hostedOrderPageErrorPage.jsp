<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:url value="${redirectUrl}" var="continueUrl"/>
<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>

	<cms:pageSlot position="SideContent" var="feature" element="div" class="span-4 side-content-slot cms_disp-img_slot">
		<cms:component component="${feature}"/>
	</cms:pageSlot>

	<div class="span-20 last">
		<div class="span-20 last">
			<div class="item_container_holder">
				<div class="title_holder">
					<h2><spring:theme code="checkout.multi.hostedOrderPageError.header"/></h2>
				</div>
				<div class="item_container">
					<div id="errorDetailsSection">
						<span class=" form_field_error">
							<p id="errorDetailsText">
								<spring:theme code="checkout.multi.hostedOrderPageError.${decision}.${reasonCode}"/>
							</p>
						</span>
						<a id="continueButton" href="${continueUrl}" class="positive right"><spring:theme code="checkout.multi.hostedOrderPageError.continue"/></a>
					</div>
				</div>
			</div>
		</div>
	</div>
</template:page>
