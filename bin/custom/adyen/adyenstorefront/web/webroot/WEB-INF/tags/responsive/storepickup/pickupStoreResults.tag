<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="entryNumber" required="false" type="java.lang.Long" %>
<%@ attribute name="cartPage" required="false" type="java.lang.Boolean" %>
<%@ attribute name="searchPageData" required="true" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/responsive/store" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/responsive/storepickup" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"  %>

<c:url var="addToCartToPickupInStoreUrl" value="/store-pickup/cart/add"/>
<c:url var="updateSelectStoreUrl" value="/store-pickup/cart/update"/>

{"data":[
	<c:forEach items="${searchPageData.results}" var="pickupStore" varStatus="pickupEntryNumber">
		<c:set value="${ycommerce:storeImage(pickupStore, 'store')}"  var="storeImage"/>
		<c:set var="stockPickup"><storepickup:pickupStoreStockLevel stockData="${pickupStore.stockData}"/></c:set>
		{
			"name" : "${pickupStore.name}",
			"displayName" : "${pickupStore.displayName}",
			"town" : "${pickupStore.address.town}",
			"line1" : "${pickupStore.address.line1}",
			"line2" : "${pickupStore.address.line2}",
			"country" : "${pickupStore.address.country.name}",
			"postalCode" : "${pickupStore.address.postalCode}",
			"formattedDistance" : "${pickupStore.formattedDistance}",
			"url" : "${storeImage.url}",
			"stockPickup" : "${stockPickup}",
			<storepickup:pickupStoreOpeningSchedule store="${pickupStore}"/>
			"productcode":"${searchPageData.product.code}",
			"storeLatitude":"${pickupStore.geoPoint.latitude}",
			"storeLongitude":"${pickupStore.geoPoint.longitude}",
			"stockLevel": "${pickupStore.stockData.stockLevel}"
		}<c:if test="${!pickupEntryNumber.last}">,</c:if>
	</c:forEach>
]}
