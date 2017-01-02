<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="hideHeaderLinks" required="false"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>

<cms:pageSlot position="TopHeaderSlot" var="component" element="div" class="container">
	<cms:component component="${component}" />
</cms:pageSlot>

<header class="main-header main-header-md">
	<div class="container">
		<div class="row">
			<div class="col-sm-12 col-md-4">
				<div class="site-logo">
					<button
						class="btn btn-default btn-large pull-right toggle-header-links js-toggle-header-links">
						<span class="glyphicon glyphicon-user"></span>
					</button>

					<cms:pageSlot position="SiteLogo" var="logo" limit="1">
						<cms:component component="${logo}" />
					</cms:pageSlot>
				</div>
			</div>
			<div class="col-sm-12 col-md-8">
				<div class="md-secondary-navigation">
					<ul>
						<c:if test="${empty hideHeaderLinks}">
							<c:if test="${uiExperienceOverride}">
								<li class="backToMobileLink"><c:url
										value="/_s/ui-experience?level=" var="backToMobileStoreUrl" />
									<a href="${backToMobileStoreUrl}"> <spring:theme
											code="text.backToMobileStore" />
								</a></li>
							</c:if>


							<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
								<c:set var="maxNumberChars" value="25" />
								<c:if test="${fn:length(user.firstName) gt maxNumberChars}">
									<c:set target="${user}" property="firstName"
										value="${fn:substring(user.firstName, 0, maxNumberChars)}..." />
								</c:if>

								<li class="logged_in">
									<ycommerce:testId code="header_LoggedUser">
										<spring:theme code="header.welcome" arguments="${user.firstName},${user.lastName}" htmlEscape="true" />
									</ycommerce:testId>
								</li>
							</sec:authorize>


							<sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
								<li>
									<ycommerce:testId code="header_Login_link">
										<a href="<c:url value="/login"/>">
											<spring:theme code="header.link.login" />
										</a>
									</ycommerce:testId>
								</li>
							</sec:authorize>

							<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
								<li>
									<ycommerce:testId code="header_signOut">
										<a href="<c:url value='/logout'/>">
											<spring:theme code="header.link.logout" />
										</a>
									</ycommerce:testId>
								</li>
							</sec:authorize>

							
							<li>
								<ycommerce:testId code="header_myAccount">
									<a href="<c:url value="/my-account"/>">
										<spring:theme code="header.link.account" />
									</a>
								</ycommerce:testId>
							</li>
							

						</c:if>

						<cms:pageSlot position="HeaderLinks" var="link">
							<cms:component component="${link}" element="li" />
						</cms:pageSlot>

						<c:if test="${empty hideHeaderLinks}">
							<li>
								<ycommerce:testId code="header_StoreFinder_link">
									<a href="<c:url value="/store-finder"/>">
										<spring:theme code="general.find.a.store" />
									</a>
								</ycommerce:testId>
							</li>
						</c:if>
					</ul>
				</div>


				<div class="sm-navigation">
					<div class="row">
						<div class="col-xs-6 col-sm-2 visible-xs visible-sm">
							<button class="btn btn-default js-toggle-sm-navigation"
								type="button">
								<span class="glyphicon glyphicon-align-justify"></span>
							</button>
							<button	class="btn btn-default js-toggle-xs-search hidden-sm hidden-md hidden-lg" type="button">
								<span class="glyphicon glyphicon-search"></span>
							</button>
						</div>

						<div class="col-xs-6 col-sm-4 col-md-4 col-sm-push-6 col-md-push-8" id="miniCartSlot">
							<cms:pageSlot position="MiniCart" var="cart" limit="1">
								<cms:component component="${cart}" />
							</cms:pageSlot>
							<button	class="btn btn-default btn-large pull-right toggle-header-links js-toggle-header-links">
								<span class="glyphicon glyphicon-user"></span>
							</button>
						</div>

						<div class="col-xs-12 col-sm-6 col-md-8 col-sm-pull-4 col-md-pull-4">
							<cms:pageSlot position="SearchBox" var="component">
								<cms:component component="${component}" />
							</cms:pageSlot>
						</div>

					</div>
				</div>

			</div>
		</div>
	</div>
	<a id="skiptonavigation"></a>
	<nav:topNavigation />
</header>


<cms:pageSlot position="BottomHeaderSlot" var="component" element="div"	class="container">
	<cms:component component="${component}" />
</cms:pageSlot>
