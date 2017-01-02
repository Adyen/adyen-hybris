<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="searchPageData" required="true" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData" %>
<%@ attribute name="locationQuery" required="false" type="java.lang.String" %>
<%@ attribute name="geoPoint" required="false" type="de.hybris.platform.commerceservices.store.data.GeoPoint" %>
<%@ attribute name="numberPagesShown" required="true" type="java.lang.Integer" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/desktop/store" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/desktop/action" %>

<c:set value="/store-finder?q=${param.q}" var="searchUrl" />
<c:if test="${not empty geoPoint}">
	<c:set value="/store-finder?latitude=${geoPoint.latitude}&longitude=${geoPoint.longitude}&q=${param.q}" var="searchUrl" />
</c:if>



<c:if test="${searchPageData ne null and !empty searchPageData.results}">
<div class="storeResultList">
	<table class="storeResultTable">
		<thead>
		<tr>
			<th id="header1"><spring:theme code="storeFinder.table.store"/></th>
			<th id="header2"><spring:theme code="storeFinder.table.distance"/></th>
			<th id="header3"><spring:theme code="storeFinder.table.address"/></th>
			<th id="header4"><spring:theme code="storeFinder.table.opening"/></th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${searchPageData.results}" var="pos" varStatus="loopStatus">
			<c:url value="${pos.url}" var="posUrl"/>
			<tr class="storeItem">
				<td headers="header1">
					<a href="${posUrl}" class="left">
						<img src="" alt="" class="storeMarker" />
						<ycommerce:testId code="storeFinder_result_image">
							<store:storeImage store="${pos}" format="cartIcon"/>
						</ycommerce:testId>
					</a>
					
					<c:url value="${pos.url}" var="storeUrl" scope="request"/>
					<action:actions element="div" styleClass="itemName" parentComponent="${component}"/>

					<div class="details">
						<div class="itemName">
							<ycommerce:testId code="storeFinder_result_link">
								<a href="${posUrl}">${pos.name}</a>
							</ycommerce:testId>
						</div>	
						<p>${pos.address.phone}</p>
					</div>
				</td>
				
				<td headers="header2">
					${pos.formattedDistance}
				</td>
				
				<td headers="header3">
					<ycommerce:testId code="storeFinder_result_address_label">
						<c:if test="${not empty pos.address}">
							<ul>
								<li>${pos.address.line1}</li>
								<li>${pos.address.line2}</li>
								<li>${pos.address.town}</li>
								<li>${pos.address.postalCode}</li>
							</ul>
						</c:if>
					</ycommerce:testId>
				</td>
				
				<td headers="header4">
					<store:openingSchedule openingSchedule="${pos.openingHours}"/>
				</td>
				
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
	
	<nav:pagination top="false"
					supportShowAll="false"
					supportShowPaged="false"
					searchPageData="${searchPageData}"
					searchUrl="${searchUrl}"
					msgKey="text.storefinder.desktop.page"
					numberPagesShown="${numberPagesShown}"/>
					
</div>
</c:if>



