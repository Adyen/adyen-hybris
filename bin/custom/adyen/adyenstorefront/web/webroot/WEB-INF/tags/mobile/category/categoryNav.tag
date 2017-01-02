<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="categories" required="true" type="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:forEach items="${categories}" var="category">
	<li>
		<c:url value="${category.url}" var="categoryUrl" /> 
		<a href="${categoryUrl}">${category.name}</a>
	</li>
</c:forEach>
