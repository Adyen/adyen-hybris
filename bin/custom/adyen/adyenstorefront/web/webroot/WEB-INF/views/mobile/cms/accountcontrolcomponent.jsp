<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div id="accountcontrol">
	<div class="top-nav-bar" data-theme="f" data-role="header">
		<c:if test="${empty hideHeaderLinks}">
			<h6 class="descriptionHeadline"><spring:theme code="text.headline.myaccount" text="Click here to login or get to your Account"/></h6>
			<a href="#" id="top-nav-bar-account" data-role="button" role="button" data-theme="f" data-iconpos="notext" data-icon="user" title="Login and Account">
				<spring:theme code="text.header.loginandaccount" text="Login and Account"/>
			</a>
		</c:if>
	</div>

	<c:if test="${empty hideHeaderLinks}">
		<div id="userSettings" class="top-nav-bar-layer user-settings header-popup menu-container" style="display:none; right:0;">
			<ul data-role="listview" data-inset="true" data-theme="f">
				<sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
					<li>
						<ycommerce:testId code="header_Login_link">
							<a href="<c:url value='/login'/>"><spring:theme code="header.mobile.link.login"/></a>
						</ycommerce:testId>
					</li>
				</sec:authorize>
				<li>
					<spring:url value="/my-account" var="encodedUrl"/>
					<a href="${encodedUrl}">
						<spring:theme code="text.account.account" text="Account"/>
					</a>
				</li>
				<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
					<li>
						<ycommerce:testId code="header_signOut">
							<a href="<c:url value='/logout'/>"><spring:theme code="text.logout"/></a>
						</ycommerce:testId>
					</li>
				</sec:authorize>
			</ul>
		</div>
	</c:if>
</div>
