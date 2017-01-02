<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>

<c:set value="${component.styleClass} ${dropDownLayout}" var="bannerClasses"/>

<li class="${bannerClasses} <c:if test="${not empty component.navigationNode.children}"> has-sub js-enquire-has-sub</c:if>">
	<cms:component component="${component.link}" evaluateRestriction="true"/>
	<c:if test="${not empty component.navigationNode.children}">
		<div class="sub-navigation">
			<a class="sm-back js-enquire-sub-close" href="#">Back</a>
			<div class="row">
				<c:forEach items="${component.navigationNode.children}" var="child">
					<c:if test="${child.visible}">

					<c:set value="${fn:length(child.links)/component.wrapAfter}" var="columns"/>

						<c:choose>
							<c:when test="${columns > 0 && columns <= 1}">
								<c:set value="col-md-4" var="sectionClass" />
							</c:when>

							<c:when test="${columns > 1 && columns < 3}">
								<c:set value="col-md-8" var="sectionClass" />
								<c:set value="column-2" var="columnClass" />
							</c:when>

							<c:when test="${columns > 2 && columns < 4}">
								<c:set value="col-md-12" var="sectionClass" />
								<c:set value="column-3" var="columnClass" />
							</c:when>

							<c:when test="${columns > 3 && columns < 5}">
								<c:set value="col-md-12" var="sectionClass" />
								<c:set value="column-4" var="columnClass" />
							</c:when>
							
							<c:otherwise>
								<c:set value="col-md-12" var="sectionClass" />
								<c:set value="column-5" var="columnClass" />
							</c:otherwise>
						</c:choose>

						<div class="sub-navigation-section ${sectionClass}">
							<c:if test="${not empty child.title}">
								<div class="title">${child.title}</div>
							</c:if>
							
							<c:if test="${columns > 1}">
								<div class="row">
							</c:if>
							
								<c:forEach items="${child.links}" step="${component.wrapAfter}" var="childlink" varStatus="i">
									<c:if test="${columns > 1}">
										<div class=" sub-navigation-section-column ${columnClass} ">
									</c:if>


									<ul class="sub-navigation-list">
										<c:forEach items="${child.links}" var="childlink" begin="${i.index}" end="${i.index + component.wrapAfter - 1}">
											<cms:component component="${childlink}" evaluateRestriction="true" element="li" />
										</c:forEach>
									</ul>

									<c:if test="${columns > 1}">
										</div>
									</c:if>
								</c:forEach>
							<c:if test="${columns > 1}">
								</div>
							</c:if>
						</div>
					</c:if>
				</c:forEach>
			</div>
		</div>
	</c:if>
</li>