<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>

<%@ attribute name="openingSchedule" required="true" type="de.hybris.platform.commercefacades.storelocator.data.OpeningScheduleData" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:if test="${not empty openingSchedule}">
	<dl class="dl-horizontal">
		<c:forEach items="${openingSchedule.specialDayOpeningList}" var="specialDay">
			
				<dt>${specialDay.formattedDate}</dt>
				<dt>${specialDay.name}</dt>
					<c:choose>
						<c:when test="${specialDay.closed}" >
							<dd><spring:theme code="storeDetails.table.opening.closed" /></dd>
						</c:when>
						<c:otherwise>
							<dd>${specialDay.openingTime.formattedHour} - ${specialDay.closingTime.formattedHour}</dd>
						</c:otherwise>
					</c:choose>
				<dt>${specialDay.comment}</dt>
		</c:forEach>
	</dl>
</c:if>
