<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="openingSchedule" required="true" type="de.hybris.platform.commercefacades.storelocator.data.OpeningScheduleData"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<c:if test="${not empty openingSchedule}">
	<ycommerce:testId code="storeDetails_table_openingSchedule_label">
		<div data-content-theme="b" data-theme="b">
			<ul class="mFormList" data-inset="true">
				<c:forEach items="${openingSchedule.weekDayOpeningList}" var="weekDay">
					<li>
						<div class="ui-grid-a">
							<div class="ui-block-a" style="width:20%">${weekDay.weekDay}&nbsp;</div>
							<div class="ui-block-b" style="width:50%">
								<c:choose>
									<c:when test="${weekDay.closed}">
										<spring:theme code="storeDetails.table.opening.closed"/>
									</c:when>
									<c:otherwise>
										${weekDay.openingTime.formattedHour} - ${weekDay.closingTime.formattedHour}
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</li>
				</c:forEach>
			</ul>
		</div>
	</ycommerce:testId>
</c:if>
