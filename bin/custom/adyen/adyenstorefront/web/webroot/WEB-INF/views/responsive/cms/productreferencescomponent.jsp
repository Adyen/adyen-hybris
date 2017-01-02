<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>


<c:choose>
	<c:when
		test="${not empty productReferences and component.maximumNumberProducts > 0}">
		<div class="carousel-component">

			<div class="headline">${component.title}</div>

			<div class="carousel js-owl-carousel js-owl-lazy-reference js-owl-carousel-reference">
				
				<div id="quickViewTitle" class="quickView-header" style="display:none">
					<div class="headline">		
						<span class="headline-text"><spring:theme code="popup.quick.view.select"/></span>
					</div>
				</div>
				
				<c:forEach end="${component.maximumNumberProducts}"
					items="${productReferences}" var="productReference">
					<c:url value="${productReference.target.url}/quickView" var="productUrl"/>
					<div class="item">
						<a href="${productUrl}" class="js-reference-item" data-quickview-title="<spring:theme code="popup.quick.view.select"/></span>">
						<product:productPrimaryReferenceImage
							product="${productReference.target}" format="product" />
						 
						<c:if test="${component.displayProductTitles}">
							<div class="item-name">${productReference.target.name}</div>
						</c:if>
						<c:if test="${component.displayProductPrices}">
							<div class="priceContainer">
								<format:fromPrice priceData="${productReference.target.price}" />
							</div>
						</c:if>
						</a>

					</div>
				</c:forEach>
			</div>
		</div>
	</c:when>

	<c:otherwise>
		<component:emptyComponent />
	</c:otherwise>
</c:choose>