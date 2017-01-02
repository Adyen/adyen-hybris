<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>

<%@ attribute name="store" required="true" type="de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/responsive/store"%>

{
		<c:forEach items="${store.features}" var="feature" varStatus="featureNumber">
			"${feature.value}":"${feature.value}"<c:if test="${!featureNumber.last}">,</c:if>
		</c:forEach>
},