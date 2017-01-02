<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<link type="text/css" rel="stylesheet" media="screen" href="${commonResourcePath}/css/owl.carousel.css"/>
<script defer src="${commonResourcePath}/js/owl.carousel.min.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.carousel.js"></script>

<div class="span-12 last" data-theme="b">

	<div id="homepage_slider" class="owl-carousel owl-theme simple-banner">
		<c:forEach items="${banners}" var="banner">
			<c:if test="${ycommerce:evaluateRestrictions(banner)}">
				<c:url value="${banner.urlLink}" var="encodedUrl" />
				<div class="item">
					<a tabindex="-1" href="${encodedUrl}" <c:if test="${banner.external}"> target="_blank"</c:if>> 
						<img src="${banner.media.url}" 	alt="${not empty banner.headline ? banner.headline : banner.media.altText}"	title="${not empty banner.headline ? banner.headline : banner.media.altText}" />
					</a>
				</div>
			</c:if>
		</c:forEach>
	</div>

</div>
