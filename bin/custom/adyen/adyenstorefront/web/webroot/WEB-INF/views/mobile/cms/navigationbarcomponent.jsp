<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>

<c:if test="${empty component.navigationNode.children}">
	<c:set value="${component.styleClass} ${dropDownLayout}" var="bannerClasses"/>
	<cms:component component="${component.link}" element="li" class="La ${bannerClasses}" evaluateRestriction="true"/>
</c:if>
<c:if test="${not empty component.navigationNode.children}">
	<li class="La ${bannerClasses}">
		<div data-role="collapsiblelistview" class="navigationNodeParent">
			<h3 data-theme="d" class="ui-li-heading">
				<cms:component component="${component.link}" evaluateRestriction="true"/>
			</h3>
			<a data-role="button" data-icon="plus" href="#"></a>
			<ul data-role="listview" data-theme="f">
				<c:forEach items="${component.navigationNode.children}" var="child">
					<c:if test="${child.visible}">
						<div data-role="collapsiblelistview" class="navigationNodeParent ui-btn-up-f" data-theme="f">
							<h3 data-theme="d" class="ui-li-heading"><a href="#">${child.title}</a></h3>
						</div>
						<ul data-role="listview" data-theme="f">
							<c:forEach items="${child.links}" var="childlink">
								<cms:component component="${childlink}" evaluateRestriction="true" element="li" class="navigationNodeChild"/>
							</c:forEach>
						</ul>
					</c:if>
				</c:forEach>
			</ul>
		</div>
	</li>
</c:if>
