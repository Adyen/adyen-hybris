<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="deliveryMethod" required="true" type="de.hybris.platform.commercefacades.order.data.DeliveryModeData"%>
<%@ attribute name="isSelected" required="false" type="java.lang.Boolean"%>
<%@ attribute name="isSelectable" required="false" type="java.lang.Boolean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set value="${not empty deliveryMethod}" var="deliveryMethodOk" />
<div class="deliveryMethodDetailsList" data-theme="d">
	<c:choose>
		<c:when test="${deliveryMethodOk}">
			<c:choose>
				<c:when test="${isSelectable}">
					<c:choose>
						<c:when test="${isSelected}">
							<input type="radio" name="delivery_method" id="${deliveryMethod.code}" value="${deliveryMethod.code}" checked="checked" data-theme="d" />
						</c:when>
						<c:otherwise>
							<input type="radio" name="delivery_method" id="${deliveryMethod.code}" value="${deliveryMethod.code}" data-theme="d" />
						</c:otherwise>
					</c:choose>
					<label for="${deliveryMethod.code}">
						<ul class="mFormList">
							<li>${deliveryMethod.name}</li>
							<li>${deliveryMethod.description}&nbsp;-&nbsp;${deliveryMethod.deliveryCost.formattedValue}</li>
						</ul>
					</label>
				</c:when>
				<c:otherwise>
					<div>
						<span>${deliveryMethod.name}</span><br /> <span>${deliveryMethod.description}</span><br /> <span>${deliveryMethod.deliveryCost.formattedValue}</span><br />
					</div>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<div>
				<spring:theme code="checkout.multi.deliveryMethod.noExistingDeliveryMethod" />
			</div>
		</c:otherwise>
	</c:choose>
</div>
