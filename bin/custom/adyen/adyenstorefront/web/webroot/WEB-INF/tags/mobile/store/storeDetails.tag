<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="store" required="true" type="de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/mobile/store"%>

<div class="store_details">
	<div>
		<div class="ui-grid-a">
			<div class="ui-block-a" style="width: 100%">
				<ul class="mFormList">
					<li>
						<div class="ui-grid-a">
							<div class="ui-block-a" style="width: 100%">
								<div class="ui-grid-a">
									<span class="store_name">${store.name}</span>
								</div>
								<c:if test="${not empty store.address}">
									<div class="ui-grid-a">
										<span>${store.address.line1}&nbsp;${store.address.line2}</span>
									</div>
									<div class="ui-grid-a">
										<span>${store.address.town}&nbsp;${store.address.postalCode}</span>
									</div>
									<div class="ui-grid-a">
										<div class="ui-block-a">
											<span>${store.address.country.name}</span>
										</div>
										<div class="ui-block-a">
											<span class="stores-tel"><a href="tel:${ycommerce:encodeUrl(store.address.phone)}">${store.address.phone}</a></span>
										</div>
									</div>
								</c:if>
							</div>
						</div>
					</li>
				</ul>
			</div>
		</div>
	</div>
</div>
