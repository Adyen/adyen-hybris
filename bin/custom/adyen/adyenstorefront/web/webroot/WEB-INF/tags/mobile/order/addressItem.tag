<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="address" required="true" type="de.hybris.platform.commercefacades.user.data.AddressData" %>
<%@ attribute name="type" required="false" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<div data-theme="b" data-role="content">
	<div data-theme="d">
		<h4 class="subItemHeader">
			<spring:theme code="text.${type}Address" />
		</h4>
	</div>
	<div data-theme="d">
		<ul class="mFormList">
			<li>${address.title}&nbsp;${order.deliveryAddress.firstName}&nbsp;${address.lastName}</li>
			<li>${address.line1}</li>
			<c:if test="${not empty address.line2}">
				<li>${address.line2}</li>
			</c:if>
			<li>${address.town}</li>
			<li>${address.postalCode}</li>
			<li>${address.country.name}</li>
		</ul>
	</div>
</div>
