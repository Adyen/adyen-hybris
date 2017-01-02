<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<template:page pageTitle="${pageTitle}">

	<div class="global-alerts">
		<div class="alert alert-info" role="alert">
			<spring:theme code="text.page.message.underconstruction" text="Information: Page Under Construction - Not Completely Functional"/>
		</div>
	</div>

	<div class="no-space">
		<div class="simpleimagecomponent">
			<a href="#"><theme:image code="img.categoryPage.banner.one" /></a>
		</div>

		<div class="row">
			<div class="simpleimagecomponent col-xs-6 col-sm-3">
				<a href="#"><theme:image code="img.categoryPage.banner.two.three.four.five" /></a>
			</div>
			<div class="simpleimagecomponent col-xs-6 col-sm-3">
				<a href="#"><theme:image code="img.categoryPage.banner.two.three.four.five" /></a>
			</div>
			<div class="simpleimagecomponent col-xs-6 col-sm-3">
				<a href="#"><theme:image code="img.categoryPage.banner.two.three.four.five" /></a>
			</div>
			<div class="simpleimagecomponent col-xs-6 col-sm-3">
				<a href="#"><theme:image code="img.categoryPage.banner.two.three.four.five" /></a>
			</div>
		</div>

		<div class="simpleimagecomponent">
			<a href="#"><theme:image code="img.categoryPage.banner.six.seven" /></a>
		</div>
		
		<div class="simpleimagecomponent">
			<a href="#"><theme:image code="img.categoryPage.banner.six.seven" /></a>
		</div>
	</div>


</template:page>