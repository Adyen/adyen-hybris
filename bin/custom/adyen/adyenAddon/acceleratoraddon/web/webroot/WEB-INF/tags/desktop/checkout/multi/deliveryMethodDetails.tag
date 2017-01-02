<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="deliveryMethod" required="true" type="de.hybris.platform.commercefacades.order.data.DeliveryModeData" %>
<%@ attribute name="isSelected" required="false" type="java.lang.Boolean" %>

<li class="delivery_method_item">
	<input type="radio" name="delivery_method" id="${deliveryMethod.code}" value="${deliveryMethod.code}" ${isSelected ? 'checked="checked"' : ''}/>
	<label for="${deliveryMethod.code}">${deliveryMethod.name}&nbsp;-&nbsp;${deliveryMethod.description}&nbsp;-&nbsp;${deliveryMethod.deliveryCost.formattedValue}</label>
</li>
