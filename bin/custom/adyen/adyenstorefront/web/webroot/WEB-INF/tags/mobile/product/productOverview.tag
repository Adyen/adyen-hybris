<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<ycommerce:testId code="product_overview_details">
	<div data-role="collapsible" data-theme="e" data-content-theme="c" data-collapsed="true" data-icon="arrow-u">
		<h3><spring:theme code="product.overview"/></h3>
		<div class="item_container_holder">
			<p style="font-size: 12px; padding: 0px; margin: 0px; border: 0px;">
				${product.description}
			</p>
			<c:if test="${not empty product.classifications}">
				<c:forEach items="${product.classifications}" var="classification">
					<div class="featureClass">
						<h4>${classification.name}</h4>
						<table>
							<tbody>
							<c:forEach items="${classification.features}" var="feature">
								<tr>
									<td><b>${feature.name}</b></td>
									<td>
										<c:forEach items="${feature.featureValues}" var="value" varStatus="status">
											${value.value}
											<c:choose>
												<c:when test="${feature.range}">${not status.last ? '-' : feature.featureUnit.symbol}</c:when>
												<c:otherwise>${feature.featureUnit.symbol} ${not status.last ? '<br/>' : ''}</c:otherwise>
											</c:choose>
										</c:forEach>
									</td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
					</div>
				</c:forEach>
			</c:if>
		</div>
	</div>
</ycommerce:testId>
