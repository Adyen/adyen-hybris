<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageData" required="true" type="de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="category" tagdir="/WEB-INF/tags/mobile/category" %>
<c:if test="${not empty pageData.subCategories}">
    <category:categoryNav categories="${pageData.subCategories}"/>
</c:if>

