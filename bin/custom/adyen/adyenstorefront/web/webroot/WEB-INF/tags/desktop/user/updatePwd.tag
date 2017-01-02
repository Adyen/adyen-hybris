<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<cms:pageSlot position="SideContent" var="feature" element="div" class="span-4 side-content-slot cms_disp-img_slot">
	<cms:component component="${feature}"/>
</cms:pageSlot>

<div class="span-20 last">
	<div class="item_container_holder">
		<div class="title_holder">
			<h2><spring:theme code="updatePwd.title"/></h2>
		</div>

		<div class="item_container">
			<p><spring:theme code="updatePwd.description"/></p>
			<p class="required"><spring:theme code="form.required"/></p>
			<form:form method="post" commandName="updatePwdForm">
				<div class="form_field-elements">
					<div class="form_field-input">
						<formElement:formPasswordBox idKey="updatePwd-pwd" labelKey="updatePwd.pwd" path="pwd" inputCSS="text password strength" mandatory="true"/>
						<formElement:formPasswordBox idKey="updatePwd.checkPwd" labelKey="updatePwd.checkPwd" path="checkPwd" inputCSS="text password" mandatory="true" errorPath="updatePwdForm"/>
					</div>
				</div>
				<div class="form-field-button">
					<ycommerce:testId code="update_update_button">
						<button class="form"><spring:theme code="updatePwd.submit"/></button>
					</ycommerce:testId>
				</div>
			</form:form>
		</div>
	</div>
</div>
