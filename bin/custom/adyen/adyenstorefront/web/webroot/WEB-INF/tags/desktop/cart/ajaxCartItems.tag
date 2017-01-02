<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>

<div class="item_container_holder">
	<div class="title_holder">
		<h2><spring:theme code="basket.page.title.yourItems"/></h2>
	</div>
	<div class="item_container">
		<table class="your_cart">
			<thead>
				<tr>
					<th id="header1"><span class="hidden">Product</span></th>
					<th id="header2"><span class="hidden">Product Details</span></th>
					<th id="header3"><spring:theme code="basket.page.itemPrice"/></th>
					<th id="header4"><spring:theme code="basket.page.quantity"/></th>
					<th id="header5"><spring:theme code="basket.page.shipping"/></th>
					<th id="header6"><spring:theme code="basket.page.total"/></th>
				</tr>
			</thead>
			<tbody id="cart_items_tbody">
				<c:forEach items="${allItems}" var="entry">
					<c:url value="${entry.product.url}" var="productUrl"/>
					<tr>
						<td headers="header1" class="product_image">
							<a href="${productUrl}">
								<product:productPrimaryImage product="${entry.product}" format="thumbnail"/>
							</a>
						</td>
						<td headers="header2" class="product_details">
							<h2><a href="${productUrl}">${entry.product.name}</a></h2>
							<c:forEach items="${entry.product.baseOptions}" var="option">
								<c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
									<c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
										<dl>
											<dt>${selectedOption.name}:</dt>
											<dd>${selectedOption.value}</dd>
										</dl>
									</c:forEach>
								</c:if>
							</c:forEach>
							<ul>
								<c:if test="${not empty cartData.potentialProductPromotions}">
									<c:forEach items="${cartData.potentialProductPromotions}" var="promotion">
										<c:set var="displayed" value="false"/>
										<c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
											<c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
												<c:set var="displayed" value="true"/>
												<li><span>${promotion.description}</span></li>
											</c:if>
										</c:forEach>
									</c:forEach>
								</c:if>
								<c:if test="${not empty cartData.appliedProductPromotions}">
									<c:forEach items="${cartData.appliedProductPromotions}" var="promotion">
										<c:set var="displayed" value="false"/>
										<c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
											<c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
												<c:set var="displayed" value="true"/>
												<li><span>${promotion.description}</span></li>
											</c:if>
										</c:forEach>
									</c:forEach>
								</c:if>
							</ul>
						</td>
						<td headers="header3">
							<format:price priceData="${entry.basePrice}"/>
						</td>
						<td headers="header4" class="quantity">
							${entry.quantity}
						</td>
						<td headers="header5" class="shipping">
						</td>
						<td headers="header6" class="total">
							<format:price priceData="${entry.totalPrice}"/>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>
