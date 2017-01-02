<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>

<c:if test="${!hideBreadcrumb}">
	<div class="accmob-navigationHolder">
		<div class="accmob-navigationContent">
			<div id="breadcrumb" class="accmobBackLink">
				<nav:breadcrumb breadcrumbs="${breadcrumbs}" />
			</div>
		</div>
	</div>
</c:if>
