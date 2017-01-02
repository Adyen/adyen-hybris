<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div style="display:none;"
	id="popupMenu"
	data-options='{"mode":"blank","headerText":"<spring:theme code="text.header.menu" text="Menu"/>","headerClose":true,"blankContent":true,"themeDialog":"f"}'>

	<cms:pageSlot position="NavigationBar" var="component" element="ul" data-role="listview" data-inset="true" class="menulist" data-theme="f">
		<cms:component component="${component}" />
	</cms:pageSlot>
</div>
