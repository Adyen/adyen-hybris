<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<template:page pageTitle="${pageTitle}">

		<div class="checkout-login">
			<div class="row">
				<div class="col-sm-6">
					<cms:pageSlot position="LeftContentSlot" var="feature">
						<cms:component component="${feature}" />
					</cms:pageSlot>
				</div>
				<div class="col-sm-6">
					<cms:pageSlot position="RightContentSlot" var="feature">
						<cms:component component="${feature}" />
					</cms:pageSlot>

				</div>

			</div>

		</div>


</template:page>