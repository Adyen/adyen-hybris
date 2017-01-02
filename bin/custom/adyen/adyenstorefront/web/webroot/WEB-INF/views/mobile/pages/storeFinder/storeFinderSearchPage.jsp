<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/mobile/store" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<template:page pageTitle="${pageTitle}">

	<jsp:attribute name="pageScriptsAfterJspBody">
		<%-- Google maps API --%>
		<c:if test="${not empty googleApiVersion}">
			<script type="text/javascript"
					src="https://maps.googleapis.com/maps/api/js?v=${googleApiVersion}&key=${googleApiKey}&sensor=false"></script>
		</c:if>
		<script type="text/javascript" src="${commonResourcePath}/js/accmob.storefinder.js"></script>
	</jsp:attribute>

	<jsp:body>
		<common:globalMessages/>
		<cms:pageSlot position="TopContent" var="feature" element="div">
			<cms:component component="${feature}" />
		</cms:pageSlot>

		<store:storesMap storeSearchPageData="${searchPageData}"/>
		<div class="accmob-StoreList" data-theme="e" data-content-theme="c">

			<cms:pageSlot position="MiddleContentSlot" var="feature">
				<cms:component component="${feature}" element="div"/>
			</cms:pageSlot>

		</div>
		<div data-theme="e" data-content-theme="c">
			<store:storeSearch/>
		</div>

		<cms:pageSlot position="BottomContent" var="feature" element="div" id="bottom-disp-img" class="home-disp-img">
			<cms:component component="${feature}"/>
		</cms:pageSlot>

	</jsp:body>

</template:page>
