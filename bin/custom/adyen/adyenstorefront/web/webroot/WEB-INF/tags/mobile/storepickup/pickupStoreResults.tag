<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="entryNumber" required="false" type="java.lang.Long" %>
<%@ attribute name="cartPage" required="false" type="java.lang.Boolean" %>
<%@ attribute name="searchPageData" required="true"
			  type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>



<c:url var="addToCartToPickupInStoreUrl" value="/store-pickup/cart/add"/>
<c:url var="updateSelectStoreUrl" value="/store-pickup/cart/update"/>

<ul data-role="listview" data-theme="d" data-resultsfound="${not empty searchPageData.results}"
		class="pickup_store_results-list">
	<c:choose>
		<c:when test="${not empty searchPageData.results}">
			<c:forEach items="${searchPageData.results}" var="pickupStore">
				<li>
					<c:set var="outOfStockStatus" value=""/>
					<c:if test="${pickupStore.stockData.stockLevelStatus.code eq 'outOfStock'}">
						<c:set var="outOfStockStatus" value=" out-of-stock"/>
					</c:if>
					<div class="ui-grid-60-40 clearfix ${outOfStockStatus}">
						<div class="ui-block-a">
							<c:choose>
								<c:when test="${pickupStore.stockData.stockLevelStatus.code eq 'outOfStock'}">
									<span class="pickup_store_results-list_stock_out">
										<spring:theme code="pickup.out.of.stock"/>
									</span>
								</c:when>
								<c:when test="${pickupStore.stockData.stockLevelStatus.code ne 'outOfStock' and empty pickupStore.stockData.stockLevel}">
									<span class="pickup_store_results-list_stock_in">
										<spring:theme code="pickup.force.in.stock"/>
									</span>
								</c:when>
								<c:otherwise>
									<span class="pickup_store_results-list_stock_level">
										<spring:theme code="pickup.in.stock" arguments="${pickupStore.stockData.stockLevel}"/>
									</span>
								</c:otherwise>
							</c:choose>
							<span class="pickup_store_results-list_name">${pickupStore.displayName}</span>
							<span class="pickup_store_results-list_line1">${pickupStore.address.line1}</span>
							<span class="pickup_store_results-list_line2">${pickupStore.address.line2}</span>
							<span class="pickup_store_results-list_town">${pickupStore.address.town}</span>
							<span class="pickup_store_results-list_zip">${pickupStore.address.postalCode}</span>
						</div>
						<div class="ui-block-b">
							<span class="pickup_store_results-list_miles">${pickupStore.formattedDistance}</span>
							<c:if test="${pickupStore.stockData.stockLevel gt 0 or empty pickupStore.stockData.stockLevel}">
                                <c:set var="actionUrl" value="${cartPage ? updateSelectStoreUrl : addToCartToPickupInStoreUrl}"/>
								<c:set var="formClass" value="${cartPage ? 'select_store_form' : 'add_to_cart_storepickup_form'}" />
								<c:set var="buttonMessageCode" value="${cartPage ? 'pickup.here.button' : 'pickup.results.button'}" />

								<form:form class="${formClass}" action="${actionUrl}" method="post">
									<input type="hidden" name="storeNamePost" value="${pickupStore.name}"/>
									<input type="hidden" name="hiddenPickupQty" class="hiddenPickupQty" value="1"/>
									<c:choose>
										<c:when test="${cartPage}">
											<input type="hidden" name="entryNumber" value="${entryNumber}"/>
										</c:when>
										<c:otherwise>
											<input type="hidden" name="productCodePost" value="${searchPageData.product.code}"/>
										</c:otherwise>
									</c:choose>
									<button type="submit" data-role="button" data-theme="b" class="positive large pickup_here_instore_button">
										<spring:theme code="${buttonMessageCode}"/>
									</button>
								</form:form>
							</c:if>
						</div>
					</div>
				</li>
			</c:forEach>
		</c:when>
		<%-- Only show when there are no results --%>
		<c:when test="${searchPageData.pagination.totalNumberOfResults eq 0}">
			<li><p><spring:theme code="text.storefinder.mobile.page.noResults"/></p></li>
		</c:when>
	</c:choose>
</ul>


