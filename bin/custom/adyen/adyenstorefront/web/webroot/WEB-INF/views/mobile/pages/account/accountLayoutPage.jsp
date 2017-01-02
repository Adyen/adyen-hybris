<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages" data-theme="b">
		<common:globalMessages/>
	</div>
	<cms:pageSlot position="TopContent" var="feature" element="div">
		<cms:component component="${feature}"/>
	</cms:pageSlot>
	<div data-role="content">
		<div class="item_container_holder" data-content-theme="d" data-theme="e">
			<cms:pageSlot position="BodyContent" var="feature">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</div>
	</div>
	<cms:pageSlot position="BottomContent" var="feature" element="div" id="bottom-disp-img" class="home-disp-img">
		<cms:component component="${feature}" element="div" class="span-24 cms_disp-img_slot"/>
	</cms:pageSlot>
</template:page>
