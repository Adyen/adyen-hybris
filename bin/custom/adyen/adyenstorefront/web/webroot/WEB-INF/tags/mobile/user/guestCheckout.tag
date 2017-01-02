<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String" %>
<%@ attribute name="action" required="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<div class="item_container_holder">
	<div class="title_holder">
		<h2><spring:theme code="guest.checkout" arguments="${siteName}"/></h2>
	</div>

	<div class="item_container">
		<ul class="mContentList">
			<li class="continuous-text">
				<spring:theme code="guest.description"/>
			</li>
			<li class="continuous-text">
				<spring:theme code="guest.required.message"/>
			</li>
		</ul>
		<form:form action="${action}" method="post" commandName="guestForm">
			<div class="form_field-elements">
				<formElement:formInputBox idKey="guest.email" labelKey="guest.email" path="email" inputCSS="text" mandatory="true"/>
			</div>

			<div class="form-field-button">
				<ycommerce:testId code="guest_Checkout_button">
					<button type="submit" class="form" data-role="button" data-theme="b">
						<spring:theme code="${actionNameKey}"/>
					</button>
				</ycommerce:testId>
			</div>
		</form:form>
	</div>
</div>
