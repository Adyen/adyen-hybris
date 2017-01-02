<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="searchUrl" required="true" %>
<%@ attribute name="searchPageData" required="true" type="de.hybris.platform.commerceservices.search.pagedata.SearchPageData" %>
<%@ attribute name="top" required="true" type="java.lang.Boolean" %>
<%@ attribute name="supportShowAll" required="true" type="java.lang.Boolean" %>
<%@ attribute name="supportShowPaged" required="true" type="java.lang.Boolean" %>
<%@ attribute name="msgKey" required="false" %>
<%@ attribute name="numberPagesShown" required="false" type="java.lang.Integer" %>

<%@ taglib prefix="pagination" tagdir="/WEB-INF/tags/responsive/nav/pagination" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:set var="themeMsgKey" value="${not empty msgKey ? msgKey : 'search.page'}"/>

<c:if test="${searchPageData.pagination.totalNumberOfResults == 0 && top }">
	<div class="paginationBar top clearfix">
		<ycommerce:testId code="searchResults_productsFound_label">
			<div class="totalResults"><spring:theme code="${themeMsgKey}.totalResults" arguments="${searchPageData.pagination.totalNumberOfResults}"/></div>
		</ycommerce:testId>
	</div>
</c:if>

<c:if test="${searchPageData.pagination.totalNumberOfResults > 0}">

	<div class="pagination-bar ${(top)?"top":"bottom"}">

		<div class="row">

			<div class="col-xs-6 col-md-4">
				<ycommerce:testId code="searchResults_productsFound_label">
					<spring:theme code="${themeMsgKey}.totalResults" arguments="${searchPageData.pagination.totalNumberOfResults}"/>
				</ycommerce:testId>
			</div>

			<c:if test="${not empty searchPageData.sorts}">

				<div class="col-xs-6 col-md-4 col-md-push-4">
					<pagination:pageSelectionPagination searchUrl="${searchUrl}" searchPageData="${searchPageData}" numberPagesShown="${numberPagesShown}" themeMsgKey="${themeMsgKey}"/>
				</div>
				
				<c:if test="${ top}">
					<div class="helper clearfix hidden-md hidden-lg"></div>

					<div class="sort-refine-bar">
						<div class="col-xs-6 col-md-4 col-md-pull-4">
							<div class="form-group">
								<form id="sortForm${top ? '1' : '2'}" name="sortForm${top ? '1' : '2'}" method="get" action="#">

									<select id="sortOptions${top ? '1' : '2'}" name="sort" class="form-control">
										<option disabled><spring:theme code="${themeMsgKey}.sortTitle"/></option>
										<c:forEach items="${searchPageData.sorts}" var="sort">
											<option value="${sort.code}" ${sort.selected? 'selected="selected"' : ''}>
												<c:choose>
													<c:when test="${not empty sort.name}">
														${sort.name}
													</c:when>
													<c:otherwise>
														<spring:theme code="${themeMsgKey}.sort.${sort.code}"/>
													</c:otherwise>
												</c:choose>
											</option>
										</c:forEach>
									</select>
									<c:catch var="errorException">
										<spring:eval expression="searchPageData.currentQuery.query" var="dummyVar"/><%-- This will throw an exception is it is not supported --%>
										<input type="hidden" name="q" value="${searchPageData.currentQuery.query.value}"/>
									</c:catch>

									<c:if test="${supportShowAll}">
										<ycommerce:testId code="searchResults_showAll_link">
											<input type="hidden" name="show" value="Page"/>
										</ycommerce:testId>
									</c:if>
									<c:if test="${supportShowPaged}">
										<ycommerce:testId code="searchResults_showPage_link">
											<input type="hidden" name="show" value="All"/>
										</ycommerce:testId>
									</c:if>
								</form>
							</div>
						</div>
						<div class="col-xs-6 col-md-4 hidden-md hidden-lg">
							<button class="btn btn-default pull-right js-show-facets"><spring:theme code="search.nav.refine.button"/></button>
							<!--<a href="" class="btn btn-link pull-right"><span
									class="glyphicon glyphicon-th-list"></span></a>
							<a href="" class="btn btn-link pull-right"><span
									class="glyphicon glyphicon-th"></span></a>-->
						</div>
					</div>
				</c:if>


			</c:if>

		</div>
	</div>
</c:if>