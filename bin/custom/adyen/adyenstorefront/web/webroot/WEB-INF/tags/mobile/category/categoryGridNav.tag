<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="category" required="true" type="de.hybris.platform.commercefacades.product.data.CategoryData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:url value="${category.url}" var="categoryUrl" />
<a href="${categoryUrl}"><img src="${category.image.url}" title="${category.name}" alt="${category.name}"/></a>
<a href="${categoryUrl}">${category.name}</a>

