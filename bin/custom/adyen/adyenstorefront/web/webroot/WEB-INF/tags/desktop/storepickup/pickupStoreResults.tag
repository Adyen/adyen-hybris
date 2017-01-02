<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="entryNumber" required="false" type="java.lang.Long" %>
<%@ attribute name="cartPage" required="false" type="java.lang.Boolean" %>
<%@ attribute name="searchPageData" required="true" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>



<c:url var="addToCartToPickupInStoreUrl" value="/store-pickup/cart/add"/>
<c:url var="updateSelectStoreUrl" value="/store-pickup/cart/update"/>

<script type="text/javascript">
	var addToCartToPickupInStoreUrl = '${addToCartToPickupInStoreUrl}';
	var searchLocation = '${locationQuery}';
</script>

<ul class="searchPOSResultsList clear_fix">
	<c:forEach items="${searchPageData.results}" var="pickupStore">
		<li class="column searchPOSResult">
			
			<div class="clear_fix address">
				<div class="resultName">${pickupStore.displayName}</div>
				<div class="resultLine1">${pickupStore.address.line1}</div>
				<div class="resultLine2">${pickupStore.address.line2}</div>
				<div class="resultTown">${pickupStore.address.town}</div>
				<div class="resultUip">${pickupStore.address.postalCode}</div>
			</div>
			<div class="resultDistance">${pickupStore.formattedDistance}</div>
			
			<c:choose>
				<c:when test="${pickupStore.stockData.stockLevelStatus.code eq 'outOfStock'}">
					<div class="resultStock negative">
						<spring:theme code="pickup.out.of.stock"/>
					</div>
				</c:when>
				<c:when test="${pickupStore.stockData.stockLevelStatus.code ne 'outOfStock' and empty pickupStore.stockData.stockLevel}">
					<div class="resultStock">
						<spring:theme code="pickup.force.in.stock"/>
					</div>
				</c:when>
				<c:otherwise>
					<div class="resultStock">
						<spring:theme code="pickup.in.stock" arguments="${pickupStore.stockData.stockLevel}"/>
					</div>
				</c:otherwise>
			</c:choose>
			
			
			
			<c:if test="${pickupStore.stockData.stockLevel gt 0 or empty pickupStore.stockData.stockLevel}">
				<c:choose>
					<c:when test="${cartPage}">
						<form:form id="selectStoreForm" class="select_store_form" action="${updateSelectStoreUrl}" method="post">
							<input type="hidden" name="storeNamePost" value="${pickupStore.name}"/>
							<input type="hidden" name="entryNumber" value="${entryNumber}"/>
							<input type="hidden" name="hiddenPickupQty" value="1" class="hiddenPickupQty"/>
							<button type="submit" class="positive  pickup_here_instore_button">
								<spring:theme code="pickup.here.button"/>
							</button>
						</form:form>
					</c:when>
					<c:otherwise>
						<form:form  class="add_to_cart_storepickup_form" action="${addToCartToPickupInStoreUrl}" method="post">
							<input type="hidden" name="storeNamePost" value="${pickupStore.name}"/>
							<input type="hidden" name="productCodePost" value="${searchPageData.product.code}"/>
							<input type="hidden" name="hiddenPickupQty" value="1" class="hiddenPickupQty" />
							<button type="submit" class="positive pickup_add_to_bag_instore_button">
								<spring:theme code="text.addToCart"/>
							</button>
						</form:form>
					</c:otherwise>
				</c:choose>
			</c:if>
		</li>
	</c:forEach>
</ul>
