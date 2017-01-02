<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="storefinder" class="top-nav-bar" data-theme="f" data-role="header">
	<a href="<c:url value="/store-finder"/>" id="top-nav-bar-home" data-role="button" role="button" data-iconpos="notext" data-theme="f" data-icon="custom-stores" title="Store Finder">
			<spring:theme code="text.header.storefinder" text="Store Finder"/>
	</a>
</div>

