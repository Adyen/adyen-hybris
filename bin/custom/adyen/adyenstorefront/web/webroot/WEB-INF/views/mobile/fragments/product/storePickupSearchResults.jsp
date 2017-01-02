<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/mobile/storepickup" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:url var="pickUpInStoreFormAction" value="/store-pickup/${searchPageData.product.code}/pointOfServices"/>

<div class="accmob-navigationHolder">
	<div class="accmob-navigationContent">
	    <a id="changeLocation" href="#"><spring:theme code="pickup.change.location" text="Change Location"/></a>
    </div>
</div>

<storepickup:pickupStoreResults searchPageData="${searchPageData}" cartPage="${cartPage}" entryNumber="${entryNumber}"/>

<div class="pickup_store_search-form-footer span-16">
	<div class="pickup_store_search-paging span-16 right last">
		<c:if test="${(searchPageData.pagination.currentPage + 1) lt searchPageData.pagination.numberOfPages}">
			<form action="${pickUpInStoreFormAction}" id="next_results_storepickup_form" method="get">
				<input type="hidden" name="page" id="next_page_value" value="0"/>
				<input type="hidden" name="locationQuery" id="locationForSearch" value="${locationQuery}"/>
				<input type="hidden" name="entryNumber" value="${entryNumber}" />
				<input type="hidden" name="cartPage" id="atCartPage" value="${cartPage}" />
			</form>
		</c:if>
	</div>
</div>
