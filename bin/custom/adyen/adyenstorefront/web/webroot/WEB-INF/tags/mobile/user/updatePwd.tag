<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="item_container_holder" data-content-theme="d" data-theme="e">
	<div id="globalMessages" data-theme="e">
		<common:globalMessages/>
	</div>
	<div data-content-theme="e" data-theme="e">
		<h3>
			<spring:theme code="text.account.profile.updatePasswordForm" text="Update Password" />
		</h3>
		<p>
		<div class="item_container">
			<p class="continuous-text">
				<spring:theme code="text.account.profile.updatePassword" text="Please use this form to update your account password"/>
			</p>
			<p class="continuous-text">
				<spring:theme code="form.required" text="Fields marked * are required"/>
			</p>
			<form:form method="post" commandName="updatePwdForm" data-ajax="false">
				<common:errors/>
				<ul class="mFormList">
					<li><formElement:formPasswordBox idKey="updatePwd-pwd" labelKey="updatePwd.pwd" path="pwd" inputCSS="text password strength" mandatory="true" /></li>
					<li><formElement:formPasswordBox idKey="updatePwd.checkPwd" labelKey="updatePwd.checkPwd" path="checkPwd" inputCSS="text password" mandatory="true" /></li>
				</ul>
				<span style="display: block; clear: both;">
					<button class="form"><spring:theme code="updatePwd.submit"/></button>
				</span>
			</form:form>
		</div>
	</div>
	</p>
</div>
