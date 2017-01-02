<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
		<div class="item_container_holder" data-content-theme="d" data-theme="e">
			<div data-content-theme="e" data-theme="e">
				<h3>
					<spring:theme code="text.account.profile.updatePasswordForm"
								text="Update Password"/>
				</h3>
				<p>
				<div class="item_container">
					<p class="continuous-text">
						<spring:theme code="text.account.profile.updatePassword" text="Please use this form to update your account password"/>
					</p>
					<p class="continuous-text">
						<spring:theme code="form.required" text="Fields marked * are required"/>
					</p>
					<c:url value="/my-account/update-password" var="updatePasswordUrl"/>
					<form:form action="${updatePasswordUrl}" method="post" commandName="updatePasswordForm" autocomplete="off">
						<common:errors/>
						<ul class="mFormList" data-inset="true">
							<li>
								<formElement:formPasswordBox idKey="profile.currentPassword"
															labelKey="profile.currentPassword" path="currentPassword"
															inputCSS="text password" mandatory="true"/>
							</li>
							<li>
								<formElement:formPasswordBox idKey="profile-newPassword"
															labelKey="profile.newPassword" path="newPassword"
															inputCSS="text password strength" mandatory="true"/>
							</li>
							<li>
								<formElement:formPasswordBox
									idKey="profile.checkNewPassword"
									labelKey="profile.checkNewPassword" path="checkNewPassword"
									inputCSS="text password" mandatory="true"/>
							</li>
							<li>
							<li>
								<fieldset class="ui-grid-a doubleButton">
									<div class="ui-block-a">
										<c:url value="/my-account/profile" var="profileUrl"/>
										<a href="${profileUrl}" data-role="button" data-theme="c" data-icon="delete" class="ignoreIcon">
											<spring:theme code="text.button.cancel"/>
										</a>
									</div>
									<div class="ui-block-b">
										<ycommerce:testId code="profilePage_SaveUpdatePasswordButton">
											<button class="form" data-theme="b" data-icon="check">
												<spring:theme code="text.button.save"/>
											</button>
										</ycommerce:testId>
									</div>
								</fieldset>
							</li>
						</ul>
					</form:form>
				</div>
			</div>
			</p>
