<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${not empty jirafeApiToken}">
<script type="text/javascript">
/* Jirafe */

<c:choose>
	<c:when test="${pageType == 'PRODUCTSEARCH'}">
		var jirafe = {
			"id":		"${jirafeSiteId}",
			"baseUrl":	"${jirafeDataUrl}",
			"search":	{ "keyword": "${searchPageData.freeTextSearch}" }
		};
	</c:when>

	<c:when test="${pageType == 'PRODUCT'}">
		var jirafe = {
			"id":		"${jirafeSiteId}",
			"baseUrl":	"${jirafeDataUrl}",
			"product":	{
				"sku":		"${product.code}",
				"name":		"${product.name}",
				"price":	"${product.price.value}",
				"categories": [ <c:forEach items="${product.categories}" var="category" varStatus="status">
									"${category.name}"<c:if test="${not status.last}">,</c:if>
								</c:forEach> ]
				}
		};
	</c:when>
	
	<c:when test="${pageType == 'CATEGORY'}">
		var jirafe = {
			"id":		"${jirafeSiteId}",
			"baseUrl":	"${jirafeDataUrl}",
			"category":	{ "name": "${categoryName}" }
		};
	</c:when>

	<c:when test="${pageType == 'CART'}">
		var jirafe = {
				"id": "${jirafeSiteId}",
				"baseUrl": "${jirafeDataUrl}",
				"cart": {
					"total": "${cartData.totalPrice.value}",
					"products": [ <c:forEach items="${cartData.entries}" var="entry" varStatus="status">
									{
										"sku": "${entry.product.code}",
										"name": "${entry.product.name}",
										"qty": "${entry.quantity}",
										"price": "${entry.basePrice.value}",
										<c:if test="${not empty entry.product.categories}">
											"category": "${entry.product.categories[0].name}"
										</c:if>
									}<c:if test="${not status.last}">,</c:if>
								</c:forEach>]
				}
		};
	</c:when>

	<c:when test="${pageType == 'ORDERCONFIRMATION'}">
		var jirafe = {
			"id": "${jirafeSiteId}",
			"baseUrl": "${jirafeDataUrl}",
			"confirm": {
				"orderid":	"${orderData.code}",
				"total":	"${orderData.totalPrice.value}",
				"shipping":	"${orderData.deliveryCost.value}",
				"tax":		"${orderData.totalTax.value}",
				"discount":	"${orderData.totalDiscounts.value}",
				"subtotal":	"${orderData.subTotal.value}",
				"products":	[ <c:forEach items="${orderData.entries}" var="entry" varStatus="status">
								{
									"sku":		"${entry.product.code}",
									"name":		"${entry.product.name}",
									"qty":		"${entry.quantity}",
									"price":	"${entry.basePrice.value}"
									<c:if test="${not empty entry.product.categories}">
										, "category": "${entry.product.categories[0].name}"
									</c:if>
								}<c:if test="${not status.last}">,</c:if>
							</c:forEach> ]
			}
		};
	</c:when>

	<c:otherwise>
		var jirafe = { "id": "${jirafeSiteId}", "baseUrl":"${jirafeDataUrl}" };
	</c:otherwise>
</c:choose>

(function(){
	var d=document,g=d.createElement('script'),s=d.getElementsByTagName('script')[0];
	g.type='text/javascript';g.defer=g.async=true;g.src=d.location.protocol+'//c.jirafe.com/jirafe.js';
	s.parentNode.insertBefore(g, s);
})();


<%-- JS method to dynamically update Jirafe with a new snapshot of the cart data --%>
function trackAddToCart_jirafe(cartData) {

	jirafe = {
		"force": "true",
		"id": "${jirafeSiteId}",
		"baseUrl": "${jirafeDataUrl}",
		"cart": {
			"total": cartData.total,
			"products": []
		}
	};

	for (var i=0; i<cartData.products.length; i++)
	{
		var productData = {
			"sku": cartData.products[i].code,
			"name": cartData.products[i].name,
			"qty": cartData.products[i].quantity,
			"price": cartData.products[i].price,
			"category": cartData.products[i].categories[0]
		};
		jirafe.cart.products.push(productData);
	}

	(ff = function(){
		var d=document,g=d.createElement('script'),h=d.getElementsByTagName('head')[0];
		g.type='text/javascript';g.src=d.location.protocol+'//test-c.jirafe.com/jirafe.js';
		h.appendChild(g);
	})();
}

window.mediator.subscribe('trackAddToCart', function(data) {
	if (data.cartData)
	{
		trackAddToCart_jirafe(data.cartData);
	}
});
</script>
</c:if>