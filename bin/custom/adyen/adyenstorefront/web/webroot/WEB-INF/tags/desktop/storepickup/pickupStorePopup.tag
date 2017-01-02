<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div id="popup_store_pickup_form" class="searchPOS clearfix" style="display:none">
	
	<div class="headline"><spring:theme code="pickup.product.availability" /></div>

	<div class="prod_grid column">
		<span class="thumb"></span>
		<div class="details"></div>
		<div class="price"></div>
		<div class="quantity pickup_store_search-quantity">
			<label data-for="pickupQty"><spring:theme code="basket.page.quantity" /></label>
			<input type="text" size="1" maxlength="3"  data-id="pickupQty" name="qty" class="qty" />
		</div>
	</div>
	
	<div class="column last searchPOSContent">
			<form:form name="pickupInStoreForm" action="${actionUrl}" method="post" class="searchPOSForm clearfix">
				<table>
					<tr>
						<td>
						   <div class="control-group left">
							   <div class="controls">
								   <input type="text" name="locationQuery" data-id="locationForSearch" class="left" placeholder="<spring:theme code="pickup.search.message" />" />
							   </div>
						   </div>
							<input type="hidden" name="cartPage" data-id="atCartPage" value="${cartPage}" />
							<input type="hidden" name="entryNumber" value="${entryNumber}" class="entryNumber" />
						</td>
						<td><button type="submit" class="" data-id="pickupstore_search_button"><spring:theme code="pickup.search.button" /></button></td>
						<td><button type="submit" class="" data-id="find_pickupStoresNearMe_button"><spring:theme code="storeFinder.findStoresNearMe"/></button></td>
					</tr>
				</table>
			</form:form>
			<div data-id="pickup_store_results" ></div>
		</div>
		
</div>
