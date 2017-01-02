<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<c:forEach items="${navigationNodes}" var="node">
	<c:if test="${node.visible}">
		<c:forEach items="${node.links}" step="${component.wrapAfter}" varStatus="i">
			<c:forEach items="${node.links}" var="childlink" begin="${i.index}" end="${i.index + component.wrapAfter - 1}">
				<cms:component component="${childlink}" evaluateRestriction="true" element="li" />
			</c:forEach>
		</c:forEach>
	</c:if>
</c:forEach>
