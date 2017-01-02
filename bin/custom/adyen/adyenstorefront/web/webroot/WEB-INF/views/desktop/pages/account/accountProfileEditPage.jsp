<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:url var="profileUrl" value="/my-account/profile" />
<div class="span-24">
	<div class="span-20 last">
		<div class="accountContentPane clearfix">
			<div class="headline"><spring:theme code="text.account.profile" text="Profile"/></div>
			<div class="required right"><spring:theme code="form.required" text="Fields marked * are required"/></div>
			<div class="description"><spring:theme code="text.account.profile.updateForm" text="Please use this form to update your personal details"/></div>
			<form:form action="update-profile" method="post" commandName="updateProfileForm">
				<formElement:formSelectBox idKey="profile.title" labelKey="profile.title" path="titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="form.select.empty" items="${titleData}"/>
				<formElement:formInputBox idKey="profile.firstName" labelKey="profile.firstName" path="firstName" inputCSS="text" mandatory="true"/>
				<formElement:formInputBox idKey="profile.lastName" labelKey="profile.lastName" path="lastName" inputCSS="text" mandatory="true"/>
				<div class="form-actions">
					<button type="button" class="negative" onclick="window.location='${profileUrl}'"><spring:theme code="text.account.profile.cancel" text="Cancel"/></button>
					<ycommerce:testId code="profilePage_SaveUpdatesButton">
						<button class="positive" type="submit"><spring:theme code="text.account.profile.saveUpdates" text="Save Updates"/></button>
					</ycommerce:testId>
				</div>
			</form:form>
		</div>
	</div>
</div>