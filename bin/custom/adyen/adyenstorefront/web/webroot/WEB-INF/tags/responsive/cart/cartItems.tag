<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/responsive/storepickup" %>

<ul class="cart-list">
    <c:forEach items="${cartData.entries}" var="entry">
        <c:url value="${entry.product.url}" var="productUrl"/>
    
        <li class="product-item">
            <c:if test="${entry.updateable}" >
                <ycommerce:testId code="cart_product_removeProduct">
		            <button class="btn  remove-item remove-entry-button" id="removeEntry_${entry.entryNumber}">
		                <span class="glyphicon glyphicon-remove"></span>
		            </button>
                </ycommerce:testId>
            </c:if>

            <div class="row">
                <div class="col-md-6">
                    <div class="thumb">
                        <a href="${productUrl}"><product:productPrimaryImage product="${entry.product}" format="thumbnail"/></a>
                    </div>
			        
                    <div class="details">                   
                        <ycommerce:testId code="cart_product_name">
                            <a href="${productUrl}"><div class="name">${entry.product.name}</div></a>
                        </ycommerce:testId>
                        <div class="item-sku">${entry.product.code}</div>

                        <c:if test="${ycommerce:doesPotentialPromotionExistForOrderEntry(cartData, entry.entryNumber)}">
	                        <c:forEach items="${cartData.potentialProductPromotions}" var="promotion">
	                            <c:set var="displayed" value="false"/>
	                            <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
	                                <c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber && not empty promotion.description}">
	                                    <c:set var="displayed" value="true"/>
	                                    
	                                        <div class="promo">
		                                         <ycommerce:testId code="cart_potentialPromotion_label">
		                                             ${promotion.description}
		                                         </ycommerce:testId>
	                                        </div>
	                                </c:if>
	                            </c:forEach>
	                        </c:forEach>
                        </c:if>
                        <c:if test="${ycommerce:doesAppliedPromotionExistForOrderEntry(cartData, entry.entryNumber)}">
	                        <c:forEach items="${cartData.appliedProductPromotions}" var="promotion">
	                            <c:set var="displayed" value="false"/>
	                            <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
	                                <c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
	                                    <c:set var="displayed" value="true"/>
	                                    <div class="promo">
	                                        <ycommerce:testId code="cart_appliedPromotion_label">
	                                            ${promotion.description}
	                                        </ycommerce:testId>
	                                    </div>
	                                </c:if>
	                            </c:forEach>
	                        </c:forEach>
                        </c:if>
                        
		                <c:set var="entryStock" value="${entry.product.stock.stockLevelStatus.code}"/>
		                        
		                <c:forEach items="${entry.product.baseOptions}" var="option">
		                    <c:if test="${not empty option.selected and option.selected.url eq entry.product.url}">
		                        <c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
		                            <div>
		                                <strong>${selectedOption.name}:</strong>
		                                <span>${selectedOption.value}</span>
		                            </div>
		                            <c:set var="entryStock" value="${option.selected.stock.stockLevelStatus.code}"/>
		                        </c:forEach>
		                    </c:if>
		                </c:forEach>
                        
                        <div>
                            <c:choose>
                                <c:when test="${not empty entryStock and entryStock ne 'outOfStock'}">
                                    <spring:theme code="basket.page.availability"/>: <span class="stock"><spring:theme code="product.variants.in.stock"/></span>
                                </c:when>
                                <c:otherwise>
                                    <spring:theme code="basket.page.availability"/>: <span class="stock"><spring:theme code="product.variants.out.of.stock"/></span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div>
						    <spring:theme code="basket.page.itemPrice"/>: 
                            <span class="price">
                                <ins>
                                    <format:price priceData="${entry.basePrice}" displayFreeForZero="true"/>
                                </ins>
                            </span>
                        </div>
						
                        <div class="qty">
                            <c:url value="/cart/update" var="cartUpdateFormAction" />
                            <form:form id="updateCartForm${entry.entryNumber}" action="${cartUpdateFormAction}" method="post" commandName="updateQuantityForm${entry.entryNumber}">
                                <input type="hidden" name="entryNumber" value="${entry.entryNumber}"/>
                                <input type="hidden" name="productCode" value="${entry.product.code}"/>
                                <input type="hidden" name="initialQuantity" value="${entry.quantity}"/>
                                <input type="hidden" name="initialQuantity" value="${entry.quantity}"/>
                                <ycommerce:testId code="cart_product_quantity">
                                    <form:label cssClass="" path="quantity" for="quantity${entry.entryNumber}"><spring:theme code="basket.page.qty"/>:</form:label>
                                    <form:input cssClass="form-control update-entry-quantity-input" disabled="${not entry.updateable}" type="text" size="1" id="quantity_${entry.entryNumber}" path="quantity" />
                                </ycommerce:testId>
                            </form:form> 

                            <div class="item-price">
                                <ycommerce:testId code="cart_totalProductPrice_label">
                                    <format:price priceData="${entry.totalPrice}" displayFreeForZero="true"/>
                                </ycommerce:testId>
                            </div>
                        </div>
                    </div>
                </div>
              
                <div class="col-md-6">
                    <div class="pickup">
                        <c:choose>
                             <c:when test="${entry.product.purchasable}">
                             	<div class="radio-column">
                           			<c:if test="${not empty entryStock and entryStock ne 'outOfStock'}">
                                        <c:if test="${entry.deliveryPointOfService eq null or not entry.product.availableForPickup}">
									   		<label for="pick0_${entry.entryNumber}">
									   		<span class="glyphicon glyphicon-gift text-gray"></span> 
									   		<span class="name"><spring:theme code="basket.page.shipping.ship"/></span>
									   		</label>
							    		</c:if>
									</c:if>
								    <c:if test="${not empty entry.deliveryPointOfService.name}">
                                        <label for="pick1_${entry.entryNumber}"> 
                                            <span class="glyphicon glyphicon-home"></span> 
                                            <span class="name"><spring:theme code="basket.page.shipping.pickup"/></span>
                                        </label>
								    </c:if>
                                </div>                
                                
                                <div class="store-column">
                                    <c:choose>
                                        <c:when test="${entry.product.availableForPickup}">
                                            <c:choose>
                                             <c:when test="${not empty entry.deliveryPointOfService.name}">
                                                <div class="store-name">${entry.deliveryPointOfService.name}</div>
                                             </c:when>
                                             <c:otherwise>
                                                 <div class="store-name"></div>
                                             </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:when>
                        </c:choose>
                    </div>
                </div>
            </div>
        </li>
    </c:forEach>
</ul>

<storepickup:pickupStorePopup />
