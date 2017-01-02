<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product" %>

<ul data-role="listview" data-split-theme="d" data-theme="e" data-divider-theme="e">
    <li data-role="list-divider" >
        <div class="ui-grid-a">
            <h2 class="cartItemsHeadline">
                <spring:theme code="basket.page.title.yourItems"/>
                <span class="cart-id-nr">(<spring:theme code="basket.page.number"/> ${cartData.code})</span>
            </h2>
            <div class="cartItemsHelp">
                <a href="#" data-cartcode='${cartData.code}' id='helpLink'>
                    <spring:theme code="text.help"/>
                </a>
            </div>
            <div id='modalHelpMessage'>
                <spring:theme code="basket.page.cartHelpMessageMobile" text="Help? Call us with cart ID: ${cartData.code}" arguments="${cartData.code}"/>
            </div>
        </div>
    </li>
    <c:forEach items="${cartData.entries}" var="entry">
        <c:url value="${entry.product.url}" var="entryProductUrl"/>
        <li class="cartListItem">
            <ycommerce:testId code="cart_product_name">
                <h3 class="ui-li-heading cartProductTitle">
                    <a href="${entryProductUrl}" data-transition="slide">${entry.product.name}</a>
                </h3>
            </ycommerce:testId>
            <div class="ui-grid-a">
              <div class="ui-block-a cartItemproductImage" style="width: 38%">
                  <a href="${entryProductUrl}" data-transition="slide">
                      <product:productCartImage product="${entry.product}" format="thumbnail"/>
                  </a>
              </div>
				<div class="ui-grid-a">
					<c:forEach items="${entry.product.baseOptions}" var="option">
						<c:if test="${option.selected.url eq entry.product.url}">
							<c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
								<c:if test="${not empty selectedOption.name and not empty selectedOption.value}">
									<div class="ui-block-a" style="width: 62%"><span class="selectedOptions">${selectedOption.name}:</span> ${selectedOption.value}</div>
								</c:if>
							</c:forEach>
						</c:if>
					</c:forEach>
				</div>
              <div class="ui-block-b" style="width: 62%">
                <div class="clear"></div>
                <div class="qtyForm">
                    <c:url value="/" var="updateCartFormAction"/>
                    <form:form id="updateCartForm${entry.entryNumber}"
                               data-ajax="false"
                               action="${updateCartFormAction}cart/update"
                               method="post"
                               commandName="updateQuantityForm${entry.entryNumber}"
                               data-cart='{"cartCode" : "${cartData.code}","productPostPrice":"${entry.basePrice.value}","productName":"${entry.product.name}"}'>
                        <input type="hidden" name="entryNumber" value="${entry.entryNumber}"/>
                        <input type="hidden" name="productCode" value="${entry.product.code}"/>
                        <ycommerce:testId code="cart_product_quantity">
                            <form:select disabled="${not entry.updateable}"
                                         id="quantity${entry.entryNumber}"
                                         class="quantitySelector"
                                         entryNumber="${entry.entryNumber}"
                                         path="quantity"
                                         data-theme="d"
                                         data-ajax="false">
                                <formElement:formProductQuantitySelectOption stockLevel="${entry.product.stock.stockLevel}" quantity="${entry.quantity}" startSelectBoxCounter="0"/>
                            </form:select>
                        </ycommerce:testId>
	                    <c:if test="${entry.updateable}" >
							<ycommerce:testId code="cart_product_removeProduct">
								<a href="#" id="${entry.entryNumber}" class="submitRemoveProduct">
									<spring:theme code="text.iconCartRemove" var="iconCartRemove"/>
									${iconCartRemove}
						    </a>
							</ycommerce:testId>
						</c:if>
                    </form:form>
                </div>
                <div class="clear"></div>
            </div>
        </div>
		<c:if test="${ycommerce:checkIfPickupEnabledForStore() eq true}">
			<fieldset data-role="controlgroup" class="cart-entry-shipping-mode">
				<c:url value="/store-pickup/cart/update/delivery" var="cartEntryShippingModeAction" />
				<c:set var="canBePickedUp" value="${entry.product.availableForPickup and not empty entry.deliveryPointOfService.name}" />
				<c:set var="shipChecked" value="${not canBePickedUp ? 'checked=\"checked\"' : ''}" />
				<c:set var="pickupChecked" value="${canBePickedUp ? 'checked=\"checked\"' : ''}" />
				<c:set var="changeStoreMessageCode" value="${not empty entry.deliveryPointOfService ? 'basket.page.shipping.change.store' : 'basket.page.shipping.find.store'}" />
				<form:form id="cartEntryShippingModeForm_${entry.product.code}${entry.entryNumber}" class="cartForm"  action="${cartEntryShippingModeAction}" method="POST">
					<input type="hidden" id="entryNumber" name="entryNumber" value="${entry.entryNumber}" />
					<input type="hidden" id="hiddenPickupQty" name="hiddenPickupQty" value="${entry.quantity}" class="qty" />
					<c:if test="${entry.product.stock.stockLevelStatus.code ne 'outOfStock'}">
						<input type="radio" name="shippingStatus_entry_${entry.entryNumber}" id="shipRadioButton_entry_${entry.entryNumber}" ${shipChecked} class="updateToShippingSelection" />
						<label for="shipRadioButton_entry_${entry.entryNumber}"><spring:theme code="basket.page.shipping.ship"/></label>
					</c:if>
					<c:if test="${entry.product.availableForPickup}">
						<span class="cart-pickup-container">
							<input type="radio" name="shippingStatus_entry_${entry.entryNumber}" id="pickUpRadioButton_entry_${entry.entryNumber}" ${pickupChecked} class="showStoreFinderLink" />
							<label for="pickUpRadioButton_entry_${entry.entryNumber}">
								<spring:theme code="basket.page.shipping.pickup"/>
								<span class="basket-page-shipping-pickup">${entry.deliveryPointOfService.name}</span>
							</label>
							<c:set var="hideChangeStoreLink" value="${not canBePickedUp ? 'style=display:none' : ''}" />
							<span id="changeStore_entry_${entry.entryNumber}" class="cart-changeStore" ${hideChangeStoreLink}>
								<c:url value="/store-pickup/${entry.product.code}" var="encodedUrl"/>
								<a href="#" class="ui-link" data-productCode="${entry.product.code}" data-rel="dialog" data-transition="pop" data-entrynumber="${entry.entryNumber}">
									<spring:theme code="${changeStoreMessageCode}"/>
								</a>
							</span>
						</span>
					</c:if>
				</form:form>
			</fieldset>
		</c:if>

      <div class="ui-grid-a basket-page-item-prices">
      	<div class="ui-block-a">
          <spring:theme code="basket.page.itemPrice"/>
         </div>
         <div class="ui-block-b">
          <span class="itemPrice"><format:price priceData="${entry.basePrice}" displayFreeForZero="true"/></span>
         </div>
         <div class="ui-block-a">
          <spring:theme code="basket.page.total"/>
         </div>
         <div class="ui-block-b">
          <span class="itemTotalPrice"> <format:price priceData="${entry.totalPrice}" displayFreeForZero="true"/> </span>
         </div>
        </div>

        <div class="ui-grid-a potential-product-promotions">
          <c:if test="${not empty cartData.potentialProductPromotions}">
              <c:forEach items="${cartData.potentialProductPromotions}" var="promotion">
                  <c:set var="displayed" value="false"/>
                  <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                      <c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber && not empty promotion.description}">
                          <c:set var="displayed" value="true"/>
                          <ul class="cart-promotions itemPromotionBox">
                              <li class="cart-promotions-potential">
                                  <ycommerce:testId code="cart_promotion_label">
                                      <span>${promotion.description}</span>
                                  </ycommerce:testId>
                              </li>
                          </ul>
                      </c:if>
                  </c:forEach>
              </c:forEach>
          </c:if>
          <c:set var="hasAppliedPromotions" value="false"/>
          <c:if test="${not empty cartData.appliedProductPromotions}">
              <c:forEach items="${cartData.appliedProductPromotions}" var="promotion">
                  <c:if test="${not hasAppliedPromotions}">
                      <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                          <c:if test="${not hasAppliedPromotions && consumedEntry.orderEntryNumber == entry.entryNumber}">
                              <c:set var="hasAppliedPromotions" value="true"/>
                          </c:if>
                      </c:forEach>
                  </c:if>
              </c:forEach>
          </c:if>
          <c:if test="${hasAppliedPromotions}">
              <ul class="cart-promotions  itemPromotionBox">
                  <c:forEach items="${cartData.appliedProductPromotions}" var="promotion">
                      <c:set var="displayed" value="false"/>
                      <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                          <c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
                              <c:set var="displayed" value="true"/>
                              <li class="cart-promotions-applied">
                                  <ycommerce:testId code="cart_appliedPromotion_label">
                                      <span>${promotion.description}</span>
                                  </ycommerce:testId>
                              </li>
                          </c:if>
                      </c:forEach>
                  </c:forEach>
              </ul>
          </c:if>
       </div>

      </li>
  </c:forEach>
</ul>
