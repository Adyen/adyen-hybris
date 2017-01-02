<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup" %>

<div class="searchTopHolder">
	<nav:searchTermAndSortingBar pageData="${searchPageData}" top="true" showSearchTerm="true" />
</div>
<div>
	<ul data-role="listview" id="resultsList" data-inset="true" data-theme="e" data-dividertheme="b">
		<c:forEach items="${searchPageData.results}" var="product">
			<product:productListerItem product="${product}" />
		</c:forEach>
	</ul>
</div>
<storepickup:pickupStorePopup/>
