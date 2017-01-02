<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<template:page pageTitle="${pageTitle}">
	<div class="row">
		<div class="col-md-5">
			<cms:pageSlot position="LeftContentSlot" var="feature">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
		<div class="col-md-7">
			<cms:pageSlot position="RightContentSlot" var="feature">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
	</div>
</template:page>