<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="deliveryAddress" required="true" type="de.hybris.platform.commercefacades.user.data.AddressData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div data-theme="b" data-role="content">
	<ycommerce:testId code="checkout_deliveryAddressData_text">
		<div data-theme="d">
			<h4 class="subItemHeader">
				<spring:theme code="checkout.summary.deliveryAddress.header" htmlEscape="false" />
				<span></span>
			</h4>
		</div>
		<div class="ui-grid-a" data-theme="d" style="width: 100%">
			<div class="ui-block-a" style="width: 85%">
				<ul class="mFormList">
					<li>${deliveryAddress.title}&nbsp;${deliveryAddress .firstName}&nbsp;${deliveryAddress.lastName}</li>
					<li>${deliveryAddress.line1}</li>
					<c:if test="${not empty deliveryAddress.line2}">
						<li>${deliveryAddress.line2}</li>
					</c:if>
					<li>${deliveryAddress.town}</li>
					<li>${deliveryAddress.region.name}</li>
					<li>${deliveryAddress.postalCode}</li>
					<li>${deliveryAddress.country.name}</li>
				</ul>
			</div>
			<div class="ui-block-b" style="width: 15%">
				<ycommerce:testId code="checkout_changeAddress_element">
					<c:url value="/checkout/multi/delivery-address/edit" var="editAddressUrl" />
					<a href="${editAddressUrl}/?editAddressCode=${deliveryAddress.id}" data-theme="c">
						<spring:theme code="mobile.checkout.edit.link" />
					</a>
				</ycommerce:testId>
			</div>
		</div>
	</ycommerce:testId>
</div>
