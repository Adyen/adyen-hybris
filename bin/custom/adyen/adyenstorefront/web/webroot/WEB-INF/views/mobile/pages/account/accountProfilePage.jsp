<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<template:page pageTitle="${pageTitle}">
	<jsp:body>
		<nav:myaccountNav />
		<div class="item_container_holder">
			<div id="globalMessages" data-theme="e">
				<common:globalMessages />
			</div>
			<cms:pageSlot position="TopContent" var="feature" element="div">
				<cms:component component="${feature}" />
			</cms:pageSlot>
			<h3>
				<spring:theme code="text.account.profile" text="Profile" />
			</h3>
			<div class="item_container">
				<div class="ui-grid-a longtext" data-theme="d">
					<div class="ui-block-a" style="width: 85%">
						<ul class="mContentList">
							<li class="mContentListImportant">${title.name}&nbsp;${customerData.firstName}&nbsp;${customerData.lastName}</li>
							<li>${customerData.displayUid}</li>
						</ul>
					</div>
				</div>
				<div class="fakeHRSmall"></div>
				<fieldset>
					<ycommerce:testId code="profile_update">
						<c:url value="/my-account/update-profile" var="updateProfileUrl" />
						<a href="${updateProfileUrl}" data-role="button" data-ajax="false" data-theme="c">
							<spring:theme code="text.account.profile.updateProfile.mobile" text="Update Profile" />
						</a>
					</ycommerce:testId>
					<ycommerce:testId code="password_update">
						<c:url value="/my-account/update-password" var="updatePasswordUrl" />
						<a href="${updatePasswordUrl}" data-role="button" data-ajax="false" data-theme="c">
							<spring:theme code="text.account.profile.changePassword.mobile" text="Change password" />
						</a>
					</ycommerce:testId>
					<ycommerce:testId code="edit_email">
						<c:url value="/my-account/update-email" var="updateEmailUrl" />
						<a href="${updateEmailUrl}" data-role="button" data-ajax="false" data-theme="c">
							<spring:theme code="text.account.profile.updateEmail.mobile" text="Update email" />
						</a>
					</ycommerce:testId>
				</fieldset>
			</div>
			<cms:pageSlot position="BottomContent" var="feature" element="div">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
	</jsp:body>
</template:page>
