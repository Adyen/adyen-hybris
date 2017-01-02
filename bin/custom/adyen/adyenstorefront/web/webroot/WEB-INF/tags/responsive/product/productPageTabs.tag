<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<div class="tabs js-tabs tabs-responsive">

	<div class="tabhead">
		<a href=""><spring:theme code="product.product.details" /></a> <span
			class="glyphicon"></span>
	</div>	
	<div class="tabbody">
		<product:productDetailsTab product="${product}" />
	</div>

	<div class="tabhead">
		<a href=""><spring:theme code="product.product.spec" /></a> <span
			class="glyphicon"></span>
	</div>
	<div class="tabbody">
		<product:productDetailsClassifications product="${product}" />
	</div>
	
	<div id="tabreview" class="tabhead">
		<a href=""><spring:theme code="review.reviews" /></a> <span
			class="glyphicon"></span>
	</div>	
	<div class="tabbody">
		<product:productPageReviewsTab product="${product}" />
	</div>

	<cms:pageSlot position="Tabs" var="tabs">
		<cms:component component="${tabs}" />
	</cms:pageSlot>

</div>
