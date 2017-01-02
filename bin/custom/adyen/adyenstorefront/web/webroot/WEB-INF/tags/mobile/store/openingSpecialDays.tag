<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="openingSchedule" required="true" type="de.hybris.platform.commercefacades.storelocator.data.OpeningScheduleData"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/mobile/store"%>

<c:if test="${not empty openingSchedule}">
	<ycommerce:testId code="storeDetails_table_openingSchedule_label">
		<ul class="mFormList" data-inset="true">
			<c:forEach items="${openingSchedule.specialDayOpeningList}" var="specialDay">
				<li>
					<div class="ui-grid-b">
						<div class="ui-block-b" style="width:20%;">${specialDay.formattedDate}</div>
						<div class="ui-block-b" style="width:50%;">	${specialDay.name}</div>
						<div class="ui-block-b" style="width:30%;">	
							<c:choose>
								<c:when test="${specialDay.closed}">
									<spring:theme code="storeDetails.table.opening.closed"/>
								</c:when>
								<c:otherwise>
									${specialDay.openingTime.formattedHour} - ${specialDay.closingTime.formattedHour}
								</c:otherwise>
							</c:choose>
						</div>
					</div>

				</li>
			</c:forEach>
		</ul>
	</ycommerce:testId>
</c:if>
