<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="stockLevel" required="true" type="java.lang.Integer"%>
<%@ attribute name="quantity" required="false" type="java.lang.Integer"%>
<%@ attribute name="startSelectBoxCounter" required="false" type="java.lang.Integer"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${quantity == null}">
	<c:set var="quantity" value="-1" />
</c:if>
<c:if test="${startSelectBoxCounter == null}">
	<c:set var="startSelectBoxCounter" value="1" />
</c:if>
<c:choose>
	<c:when test="${stockLevel gt 0 and stockLevel < 100}">
		<c:forEach var="i" begin="${startSelectBoxCounter}" end="${stockLevel}">
			<c:choose>
				<c:when test='${i == quantity}'>
					<option value="${i}" selected='selected' data-ajax="false">${i}</option>
				</c:when>
				<c:otherwise>
					<option value="${i}" data-ajax="false">${i}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<c:forEach var="i" begin="${startSelectBoxCounter}" end="${(quantity > 100)? quantity: 100}">
			<c:choose>
				<c:when test='${i == quantity}'>
					<option value="${i}" selected='selected' data-ajax="false">${i}</option>
				</c:when>
				<c:otherwise>
					<option value="${i}" data-ajax="false">${i}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</c:otherwise>
</c:choose>
