<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ attribute name="orderGroup" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryGroupData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<c:set var="pos" value="${orderGroup.entries[0].deliveryPointOfService}"/>



<div class="orderList">
	<div class="headline"><spring:theme code="basket.page.title.pickupFrom" text="Pick Up from\:" />	${pos.name}, ${pos.address.line1} ${pos.address.line2}, ${pos.address.town}		</div>
	
	<table class="orderListTable">
		<thead>
			<tr>
				<th id="header2" colspan="2"><spring:theme code="text.productDetails" text="Product Details"/></th>
				<th id="header4"><spring:theme code="text.quantity" text="Quantity"/></th>
				<th id="header5"><spring:theme code="text.itemPrice" text="Item Price"/></th>
				<th id="header6"><spring:theme code="text.total" text="Total"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${orderGroup.entries}" var="entry">
				<c:url value="${entry.product.url}" var="productUrl"/>
				<tr class="item">
					<td headers="header2" class="thumb">
						<a href="${productUrl}">
							<product:productPrimaryImage product="${entry.product}" format="thumbnail"/>
						</a>
					</td>
					<td headers="header2" class="details">
					
							<ycommerce:testId code="orderDetails_productName_link">
								<div class="itemName"><a href="${entry.product.purchasable ? productUrl : ''}">${entry.product.name}</a></div>
							</ycommerce:testId>
						
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
						<c:if test="${not empty order.appliedProductPromotions}">
							<ul>
								<c:forEach items="${order.appliedProductPromotions}" var="promotion">
									<c:set var="displayed" value="false"/>
									<c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
										<c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
											<c:set var="displayed" value="true"/>
											<li><span>${promotion.description}</span></li>
										</c:if>
									</c:forEach>
								</c:forEach>
							</ul>
						</c:if>
					</td>
					<td headers="header4" class="quantity">
						<ycommerce:testId code="orderDetails_productQuantity_label">${entry.quantity}</ycommerce:testId>
					</td>
					<td headers="header5">
						<ycommerce:testId code="orderDetails_productItemPrice_label"><format:price priceData="${entry.basePrice}" displayFreeForZero="true"/></ycommerce:testId>
					</td>
					<td headers="header6" class="total">
						<ycommerce:testId code="orderDetails_productTotalPrice_label"><format:price priceData="${entry.totalPrice}" displayFreeForZero="true"/></ycommerce:testId>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>


</div>
