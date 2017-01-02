<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="navbar" class="top-nav-bar" data-theme="f" data-role="header">
	<a href="#" id="top-nav-bar-menu" data-role="button" role="button" data-iconpos="notext" data-theme="f" data-icon="home" title="<spring:theme code="text.button.menu"/>">
		<spring:theme code="text.button.menu"/>
	</a>
</div>

<div id="menuContainer" class="top-nav-bar-layer accmob-topMenu header-popup menu-container" style="display:none" data-theme="f">
	<h6 class="descriptionHeadline"><spring:theme code="text.headline.categories" text="Click here the menu button to get to the categories"/></h6>
	<ul>
		<li class="La  auto ui-btn ui-btn-up-f ui-btn-icon-right ui-li-has-arrow ui-li">
			<div class="ui-btn-inner ui-li">
				<div class="ui-btn-text">
					<a title="Home" href="<c:url value="/"/>" class="ui-link-inherit"><spring:theme code="menu.button.home" text="Home"/>
					</a>
				</div>
				<span class="ui-icon ui-icon-arrow-r ui-icon-shadow">&nbsp;</span>
			</div>
		</li>
	</ul>
	<ul data-role="listview" data-inset="true" class="menulist" data-theme="f">
	<c:forEach items="${components}" var="component">
		<cms:component component="${component}"/>
	</c:forEach>
	</ul>
</div>

