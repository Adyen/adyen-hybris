<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="store" required="true" type="de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/mobile/store"%>

<c:if test="${not empty store.openingHours}">
	<div>
		<h2>
			<spring:theme code="storeDetails.table.opening"/>
		</h2>
		<div>
			<store:openingSchedule openingSchedule="${store.openingHours}"/>
		</div>
	</div>
</c:if>
<c:if test="${not empty store.openingHours.specialDayOpeningList}">
	<div>
		<h3>
			<spring:theme code="storeDetails.table.openingSpecialDays"/>
		</h3>
		<div>
			<store:openingSpecialDays openingSchedule="${store.openingHours}"/>
		</div>
	</div>
</c:if>
<c:if test="${not empty store.features}">
	<div class="misc_details">
		<h2>
			<spring:theme code="storeDetails.table.features"/>
		</h2>
		<ul class="mFormList" data-inset="true">
			<c:forEach items="${store.features}" var="feature">
				<li>${feature.value}</li>
			</c:forEach>
		</ul>
	</div>
</c:if>
