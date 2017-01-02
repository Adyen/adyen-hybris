<%@ page contentType="text/plain" language="java" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<?xml version="1.0" encoding="UTF-8"?>
<sitemapindex xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
	<c:forEach items="${siteMapUrls}" var="loc">
		<sitemap>
			<loc>${loc}</loc>
		</sitemap>
	</c:forEach>
</sitemapindex>

