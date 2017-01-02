<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<h6 class="descriptionHeadline">
	<spring:theme code="text.headline.footer.navigationbar" text="Browse through the footer navigation bar" />
</h6>
<div id="footer" data-role="footer" data-theme="d" data-content-theme="b">
	<div id="navbar" data-role="navbar" data-iconpos="top">
		<cms:pageSlot position="Footer" var="feature" element="ul">
			<cms:component component="${feature}" />
		</cms:pageSlot>
	</div>
	<div class="switch-storefront" data-role="navbar">
		<ul>
			<li>
				<a href="<c:url value="/_s/ui-experience?level=Desktop"/>">
					<spring:theme code="text.viewfullsite" />
				</a>
			</li>
		</ul>
	</div>
	<div id="copyright" class="switch-storefront copyright">
		<p><spring:theme code="text.copyright" /></p>
	</div>
</div>
