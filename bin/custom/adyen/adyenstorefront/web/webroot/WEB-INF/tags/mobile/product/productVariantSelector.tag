<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>

<%-- Determine if product is one of apparel style or size variant --%>
<c:if test="${product.variantType eq 'ApparelStyleVariantProduct'}">
	<c:set var="variantStyles" value="${product.variantOptions}"/>
</c:if>
<c:if test="${(not empty product.baseOptions[0].options) and (product.baseOptions[0].variantType eq 'ApparelStyleVariantProduct')}">
	<c:set var="variantStyles" value="${product.baseOptions[0].options}"/>
	<c:set var="variantSizes" value="${product.variantOptions}"/>
	<c:set var="currentStyleUrl" value="${product.url}"/>
</c:if>
<c:if test="${(not empty product.baseOptions[1].options) and (product.baseOptions[0].variantType eq 'ApparelSizeVariantProduct')}">
	<c:set var="variantStyles" value="${product.baseOptions[1].options}"/>
	<c:set var="variantSizes" value="${product.baseOptions[0].options}"/>
	<c:set var="currentStyleUrl" value="${product.baseOptions[1].selected.url}"/>
</c:if>
<c:url value="${currentStyleUrl}" var="currentStyledProductUrl"/>
<%-- check for presence of color variant --%>
<c:if test='${not empty variantStyles}'>
	<c:set var="foundStyle" value="false"/>
	<c:forEach items="${variantStyles}" var="variantStyle">
		<c:forEach items="${variantStyle.variantOptionQualifiers}" var="variantOptionQualifier">
			<c:if test="${variantOptionQualifier.qualifier eq 'style'}">
				<c:set var="styleValue" value="${variantOptionQualifier.value}"/>
				<c:if test="${not empty styleValue }"><c:set var="foundStyle" value="true"/></c:if>
			</c:if>
		</c:forEach>
	</c:forEach>
</c:if>
<c:set var="showAddToCart" value="${true}" scope="session" />
<%-- checks to see if size selector has been selected: Used to display a stock level message
		in cases where the color selector has been selected, but the size selector may not yet be selected. --%>
<c:set var="sizeSelected" value='false'/>
<%-- Determine if product is other variant --%>
<c:if test="${empty variantStyles}">
	<c:if test="${not empty product.variantOptions}">
		<c:set var="variantOptions" value="${product.variantOptions}"/>
	</c:if>
	<c:if test="${not empty product.baseOptions[0].options}">
		<c:set var="variantOptions" value="${product.baseOptions[0].options}"/>
	</c:if>
</c:if>
<c:if test="${not empty variantStyles}">
	<c:set var="showAddToCart" value="${false}" scope="session"/>
	<c:if test="${foundStyle}">
		<%-- Only display the style selector if more than one style is available --%>
		<c:if test="${fn:length(variantStyles) gt 1}">
		<div class="ui-grid-a variant">
			<%-- Style Selector --%>
			<select id="colorSelector" class='variantSelector' data-theme="d">
				<c:forEach items="${variantStyles}" var="variantStyle">
					<c:forEach items="${variantStyle.variantOptionQualifiers}" var="variantOptionQualifier">
						<c:if test="${variantOptionQualifier.qualifier eq 'style'}">
							<c:set var="styleValue" value="${variantOptionQualifier.value}"/>
						</c:if>
					</c:forEach>
					<c:if test="${styleValue != ''}">
						<option value="<c:url value="${variantStyle.url}"/>"
						${(variantStyle.url eq currentStyleUrl) ? 'selected="selected"' : ''}">${styleValue}</option>
					</c:if>
				</c:forEach>
			</select>
		</div>
		</c:if>
		<div class="ui-grid-a variant">
			<%-- Size Selector --%>
			<select id="Size" class='variantSelector' data-theme="d">
				<c:if test="${empty variantSizes}">
					<option <c:if test="${empty variantParams['size']}">selected="selected"</c:if>><spring:theme
							code="product.variants.select.size"/></option>
				</c:if>
				<c:if test="${not empty variantSizes}">
					<option value="${currentStyledProductUrl}">
						<spring:theme code="product.variants.select.size"/>
					</option>
					<c:forEach items="${variantSizes}" var="variantSize">
						<c:set var="optionsString" value=""/>
						<c:forEach items="${variantSize.variantOptionQualifiers}" var="variantOptionQualifier">
							<c:if test="${variantOptionQualifier.qualifier eq 'size'}">
								<c:set var="optionsString">${optionsString} ${variantOptionQualifier.value} </c:set>
							</c:if>
						</c:forEach>
						<c:if test="${(variantSize.stock.stockLevel gt 0) and (variantSize.stock.stockLevelStatus ne 'outOfStock')}">
							<c:set var="stockLevel">${variantSize.stock.stockLevel}&nbsp;<spring:theme
									code="product.variants.in.stock"/></c:set>
						</c:if>
						<c:if test="${(empty variantSize.stock.stockLevel) and (variantSize.stock.stockLevelStatus eq 'inStock')}">
							<c:set var="stockLevel"><spring:theme code="product.variants.available"/></c:set>
						</c:if>
						<c:if test="${(variantSize.stock.stockLevel le 0) and (variantSize.stock.stockLevelStatus ne 'inStock')}">
							<c:set var="stockLevel"><spring:theme code="product.variants.out.of.stock"/></c:set>
						</c:if>
						<c:if test="${(variantSize.url eq product.url)}">
							<c:set var="showAddToCart" value="${true}" scope="session" />
						</c:if>
						<c:url value="${variantSize.url}" var="variantSizeUrl"/>
						<option value="${variantSizeUrl}" ${(variantSize.url eq product.url) ? 'selected="selected"' : ''}>${optionsString}&nbsp;${stockLevel}</option>
						<c:if test='${(variantSize.url eq product.url)}'>
							<c:set var='sizeSelected' value='true'/>
						</c:if>
					</c:forEach>
				</c:if>
			</select>
		</div>
	</c:if>
</c:if>
<c:if test="${not empty variantOptions}">
	<c:set var="showAddToCart" value="${false}" scope="session" />
	<div class="variant_options">
		<div class="size">
			<select id="variant" class='variantSelector' data-theme="d">
				<option selected="selected" disabled="disabled"><spring:theme code="product.variants.select.variant"/></option>
				<c:forEach items="${variantOptions}" var="variantOption">
					<c:set var="optionsString" value=""/>
					<c:forEach items="${variantOption.variantOptionQualifiers}" var="variantOptionQualifier">
						<c:set var="optionsString">${optionsString} ${variantOptionQualifier.name}&nbsp;${variantOptionQualifier.value}, </c:set>
					</c:forEach>
					<c:if test="${(variantOption.stock.stockLevel gt 0) and (variantSize.stock.stockLevelStatus ne 'outOfStock')}">
						<c:set var="stockLevel">${variantOption.stock.stockLevel} <spring:theme
								code="product.variants.in.stock"/></c:set>
					</c:if>
					<c:if test="${(variantOption.stock.stockLevel le 0) and (variantSize.stock.stockLevelStatus eq 'inStock')}">
						<c:set var="stockLevel"><spring:theme code="product.variants.available"/></c:set>
					</c:if>
					<c:if test="${(variantOption.stock.stockLevel le 0) and (variantSize.stock.stockLevelStatus ne 'inStock')}">
						<c:set var="stockLevel"><spring:theme code="product.variants.out.of.stock"/></c:set>
					</c:if>
					<c:if test="${variantOption.url eq product.url}">
						<c:set var="showAddToCart" value="${true}" scope="session" />
					</c:if>
					<option value="<c:url value="${variantOption.url}"/>" ${(variantOption.url eq product.url) ? 'selected="selected"' : ''}>${optionsString}
					&nbsp;<format:price priceData="${variantOption.priceData}"/>&nbsp;&nbsp;${variantOption.stock.stockLevel}
					</option>
				</c:forEach>
			</select>
		</div>
	</div>
</c:if>