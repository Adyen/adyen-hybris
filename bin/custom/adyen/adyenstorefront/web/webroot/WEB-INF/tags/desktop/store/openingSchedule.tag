<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>

<%@ attribute name="openingSchedule" required="true" type="de.hybris.platform.commercefacades.storelocator.data.OpeningScheduleData" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/desktop/store"%>


<c:if test="${not empty openingSchedule}">
	<ycommerce:testId code="storeDetails_table_openingSchedule_label">
		<table class="store-openings weekday_openings">
			<tbody>
				<c:forEach items="${openingSchedule.weekDayOpeningList}" var="weekDay">
					<tr class="${weekDay.closed ? 'weekday_openings_closed' : 'weekday_openings'}">
						<td class="weekday_openings_day">${weekDay.weekDay}</td>
						<td class="weekday_openings_times">
							<c:choose>
								<c:when test="${weekDay.closed}" >
										<spring:theme code="storeDetails.table.opening.closed" />
								</c:when>
								<c:otherwise>
									${weekDay.openingTime.formattedHour} - ${weekDay.closingTime.formattedHour}
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</ycommerce:testId>
</c:if>
