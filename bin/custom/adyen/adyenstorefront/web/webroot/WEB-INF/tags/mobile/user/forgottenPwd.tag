<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div data-role="content" data-content-theme="c" data-theme="c" id="forgottenPwd" class="forgottenPwd">
	<div class="accmob-navigationHolder">
		<div class="accmob-navigationContent">
			<div id="breadcrumb" class="accmobBackLink accmobBackLinkSingle registerNewCustomerLinkHolderBack">
				<a href="javascript:void(0)" data-role="link" class="productLink ui-link">
					<spring:theme code="register.back.login" /> &raquo;
				</a>
			</div>
		</div>
	</div>
	<div data-role="content" class="item_container_holder">
		<h3><spring:theme code="forgottenPwd.title"/></h3>
		<div class="item_container">
			<p class="continuous-text"><spring:theme code="forgottenPwd.description"/></p>
			<form:form method="post" commandName="forgottenPwdForm">
				<common:errors/>
				<ul class="mFormList" data-split-theme="d" data-theme="d">
					<li>
						<formElement:formInputBox idKey="forgottenPwd.email" labelKey="forgottenPwd.email" path="email" inputCSS="text" mandatory="true"/>
					</li>
					<li>
						<ycommerce:testId code="forgottenPassword_sendEmail">
							<button data-theme="c" data-role="button" class="form" type="submit">
								<spring:theme code="forgottenPwd.submit"/>
							</button>
						</ycommerce:testId>
					</li>
				</ul>
			</form:form>
		</div>
	</div>
</div>