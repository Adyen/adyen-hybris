<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="deliveryMethods" required="true" type="java.util.List"%>
<%@ attribute name="selectedDeliveryMethodId" required="false" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/addons/adyenAddon/mobile/checkout/multi"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set value="select" var="selectDeliveryMethodUrl" />
<c:choose>
	<c:when test="${not empty deliveryMethods}">
		<form:form id="selectDeliveryMethodUrl" action="${selectDeliveryMethodUrl}" method="get">
			<fieldset data-role="controlgroup" data-theme="d">
				<c:forEach items="${deliveryMethods}" var="deliveryMethod">
					<multi-checkout:deliveryMethodDetails id="${deliveryMethod.code}" deliveryMethod="${deliveryMethod}" isSelected="${deliveryMethod.code eq selectedDeliveryMethodId}" isSelectable="true" />
				</c:forEach>
			</fieldset>
			<div class="fakeHR"></div>
			<c:if test="${not empty selectedDeliveryMethodId}">
				<button type="submit" data-icon="arrow-r" data-iconpos="right" data-theme="b" class="onefullWidth show_processing_message">
					<spring:theme code="mobile.checkout.continue.button" />
				</button>
			</c:if>
		</form:form>
	</c:when>
	<c:otherwise>
		<ul>
			<li><spring:theme code="checkout.multi.deliveryMethod.noExistingDeliveryMethod" /></li>
		</ul>
	</c:otherwise>
</c:choose>
