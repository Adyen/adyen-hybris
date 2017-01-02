<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="category" tagdir="/WEB-INF/tags/mobile/category"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<c:if test="${not empty subCategories}">
	<ul id="categoryResultsList" data-role="listview" data-inset="true" data-theme="e" data-content-theme="e" class="mainNavigation">
		<category:categoryNav categories="${subCategories}"/>
	</ul>
</c:if>
