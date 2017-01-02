<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>

<div class="item_container_holder">
	<div class="title_holder">
		<h1><spring:theme code="text.account.order.orderByTracking" text="View Order by Tracking"/></h1>
	</div>
	<div class="item_container">
		<c:forEach items="${order.consignments}" var="consignment">
		 	<order:orderConsignmentDetails consignment="${consignment}"/>
		</c:forEach>		
	</div>
</div>
