<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<c:forEach items="${medias}" var="media">
	<c:choose>
		<c:when test="${empty imagerData}">
			<c:set var="imagerData">"${media.width}":"${media.url}"</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="imagerData">${imagerData},"${media.width}":"${media.url}"</c:set>
		</c:otherwise>
	</c:choose>
	<c:if test="${empty altText}">
		<c:set var="altText" value="${media.altText}"/>
	</c:if>
</c:forEach>

<c:url value="${urlLink}" var="encodedUrl" />
<div class="simple-responsive-banner-component">
	<c:choose>
		<c:when test="${empty encodedUrl || encodedUrl eq '#'}">
				<img class="js-responsive-image"  data-media='{${imagerData}}' alt='${altText}' title='${altText}' style="">
		</c:when>
		<c:otherwise>
				<a href="${encodedUrl}">
					<img class="js-responsive-image"  data-media='{${imagerData}}' title='${altText}' alt='${altText}' style="">
				</a>
		</c:otherwise>
	</c:choose>
</div>