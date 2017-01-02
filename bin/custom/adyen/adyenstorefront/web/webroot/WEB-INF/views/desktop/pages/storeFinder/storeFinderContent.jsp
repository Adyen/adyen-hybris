<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/desktop/store" %>

<store:storeSearch errorNoResults="${errorNoResults}"/>
<store:storesMap storeSearchPageData="${searchPageData}"/>
<store:storeListForm searchPageData="${searchPageData}" locationQuery="${locationQuery}" numberPagesShown="${numberPagesShown}" geoPoint="${geoPoint}"/>