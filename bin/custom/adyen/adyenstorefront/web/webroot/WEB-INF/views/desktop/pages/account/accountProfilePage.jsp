<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="headline">
	<spring:theme code="text.account.profile" text="Profile"/>
</div>
<table class="account-profile-data">
	<tr>
		<td><spring:theme code="profile.title" text="Title"/>: </td>
		<td>${fn:escapeXml(title.name)}</td>
	</tr>
	<tr>
		<td><spring:theme code="profile.firstName" text="First name"/>: </td>
		<td>${fn:escapeXml(customerData.firstName)}</td>
	</tr>
	<tr>
		<td><spring:theme code="profile.lastName" text="Last name"/>: </td>
		<td>${fn:escapeXml(customerData.lastName)}</td>
	</tr>
	<tr>
		<td><spring:theme code="profile.email" text="E-mail"/>: </td>
		<td>${fn:escapeXml(customerData.displayUid)}</td>
	</tr>
</table>
<a class="button" href="update-password"><spring:theme code="text.account.profile.changePassword" text="Change password"/></a>
<a class="button" href="update-profile"><spring:theme code="text.account.profile.updatePersonalDetails" text="Update personal details"/></a>
<a class="button" href="update-email"><spring:theme code="text.account.profile.updateEmail" text="Update email"/></a>