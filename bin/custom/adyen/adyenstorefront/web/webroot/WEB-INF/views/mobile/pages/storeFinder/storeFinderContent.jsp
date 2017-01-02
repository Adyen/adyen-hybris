<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/mobile/store" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<store:storeListForm storeSearchPageData="${searchPageData}" locationQuery="${locationQuery}" geoPoint="${geoPoint}"/>
