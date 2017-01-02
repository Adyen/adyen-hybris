<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mobiletemplate" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<mobiletemplate:page pageTitle="${pageTitle}">
	<jsp:body>
		<nav:myaccountNav />
		<div class="item_container_holder" data-content-theme="d" data-theme="e">
			<div id="globalMessages" data-theme="e">
				<common:globalMessages />
			</div>
			<cms:pageSlot position="TopContent" var="feature" element="div">
				<cms:component component="${feature}" />
			</cms:pageSlot>
			<h3>
				<spring:theme code="text.account.profile" text="Profile" />
			</h3>
			<p></p>
			<p class="continuous-text"><spring:theme code="text.account.profile.updateForm" text="Please use this form to update your personal details" /></p>
			<p class="continuous-text"><spring:theme code="form.required" text="Fields marked * are required" /></p>
			<c:url value="/my-account/update-profile" var="updateProfileUrl" />
			<form:form action="${updateProfileUrl}" method="post" commandName="updateProfileForm" data-ajax="false">
				<common:errors />
				<ul class="mFormList" data-theme="c" data-content-theme="c">
					<li><formElement:formSelectBox idKey="profile.title" labelKey="profile.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="form.select.empty" items="${titleData}" /></li>
					<li><formElement:formInputBox idKey="profile.firstName" labelKey="profile.firstName" path="firstName" inputCSS="text" mandatory="true" /></li>
					<li><formElement:formInputBox idKey="profile.lastName" labelKey="profile.lastName" path="lastName" inputCSS="text" mandatory="true" /></li>
					<li>
						<fieldset class="ui-grid-a doubleButton">
							<div class="ui-block-a">
								<c:url value="/my-account/profile" var="profileUrl" />
								<ycommerce:testId code="profilePage_CancelButton">
									<a href="${profileUrl}" data-role="button" data-theme="d" data-icon="delete" class="ignoreIcon">
										<spring:theme code="text.button.cancel" />
									</a>
								</ycommerce:testId>
							</div>
							<div class="ui-block-b">
								<ycommerce:testId code="profilePage_SaveUpdatesButton">
									<button class="form" data-theme="c" data-icon="check">
										<spring:theme code="text.button.save" />
									</button>
								</ycommerce:testId>
							</div>
						</fieldset>
					</li>
				</ul>
			</form:form>
			<cms:pageSlot position="BottomContent" var="feature" element="div">
				<cms:component component="${feature}" />
			</cms:pageSlot>
		</div>
	</jsp:body>
</mobiletemplate:page>
