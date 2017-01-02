<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component" %>

<c:choose>
	<c:when test="${not empty suggestions and component.maximumNumberProducts > 0}">
		<div data-role="collapsible" data-theme="e" data-content-theme="c" data-collapsed="false">
			<h3>${component.title}</h3>
			<div class="referenceGallery swipeGallery" data-enabled='true' data-commonresourcepath="${commonResourcePath}">
				<c:forEach end="${component.maximumNumberProducts}" items="${suggestions}" var="suggestion" varStatus="varStatus">
					<c:url value="${suggestion.url}/quickView" var="productQuickViewUrl" />
					<c:if test="${varStatus.index % 2 == 0}">
					<div class="referencedSlide swipeGallerySlide"></c:if>
						<div class="thumb">
							<a href="${productQuickViewUrl}" class="referencedProductImage" data-url="${productQuickViewUrl}" data-rel="dialog" data-transition="pop">
								<product:productPrimaryImage product="${suggestion}" format="thumbnail" zoomable="false" />
								<div class='referencePrice'>
									<format:fromPrice priceData="${suggestion.price}" />
								</div>
								<div class="productTitle">${suggestion.name}</div>
							</a>
						</div>
					<c:if test="${(varStatus.last) || (varStatus.index % 2 == 1)}">
					</div></c:if>
				</c:forEach>
			</div>
			<div class="dots">
				<c:if test="${fn:length(suggestions) gt 2}">
					<span><img src='${commonResourcePath}/images/closeddot.png' /></span>
					<c:forEach items="${suggestions}" step="3">
						<span><img src='${commonResourcePath}/images/opendot.png' /></span>
					</c:forEach>
				</c:if>
			</div>
		</div>
	</c:when>

	<c:otherwise>
		<component:emptyComponent/>
	</c:otherwise>
</c:choose>
