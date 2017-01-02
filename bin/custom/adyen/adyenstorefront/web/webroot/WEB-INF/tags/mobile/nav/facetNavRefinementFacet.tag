<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="facetData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.FacetData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>

<c:if test="${not empty facetData.values}">
	<ul data-role="listview" data-inset="true" class="facetValueList" data-content-theme="d" data-facet="${facetData.name}">
		<li data-theme="d" data-role="fieldcontain">
			<fieldset data-role="controlgroup">
				<c:forEach items="${facetData.values}" var="facetValue">
					<input type="checkbox" id="${facetValue.name}" data-query="${facetData.code}:${facetValue.code}"
						class="facet" ${facetValue.selected ? 'checked="checked" ' : ''} />
					<label for="${facetValue.name}">
						${facetValue.name}&nbsp;
						<span class="ui-li-count">
							<spring:theme code="mobile.search.nav.facetValueCount" arguments="${facetValue.count}"/>
						</span>
					</label>
				</c:forEach>
			</fieldset>
		</li>
	</ul>
</c:if>
