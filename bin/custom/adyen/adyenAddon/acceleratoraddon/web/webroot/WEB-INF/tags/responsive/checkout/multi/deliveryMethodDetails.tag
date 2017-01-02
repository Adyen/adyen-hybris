<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="deliveryMethod" required="true" type="de.hybris.platform.commercefacades.order.data.DeliveryModeData" %>
<%@ attribute name="isSelected" required="false" type="java.lang.Boolean" %>

<option value="${deliveryMethod.code}" ${isSelected ? 'selected="selected"' : ''}>
	${deliveryMethod.name}&nbsp;-&nbsp;${deliveryMethod.description}&nbsp;-&nbsp;${deliveryMethod.deliveryCost.formattedValue}
</option>