<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<template:page pageTitle="${pageTitle}">
	<div class="item_container_holder">
		<div class="title_holder">
			<h2>
				<spring:theme code="forgottenPwd.title"/>
			</h2>
		</div>
		<div class="item_container">
			<div id="globalMessages">
				<common:globalMessages/>
			</div>
			<spring:theme code="account.confirmation.forgotten.password.link.sent" text="You have been sent an email with a link to change your password." />
		</div>
	</div>
</template:page>
