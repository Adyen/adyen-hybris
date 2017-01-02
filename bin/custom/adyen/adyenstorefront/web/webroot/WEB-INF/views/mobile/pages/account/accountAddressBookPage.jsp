<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>

<div class="item_container_holder" data-content-theme="d" data-theme="e">
	<h3><spring:theme code="text.account.addressBook" text="Address Book" /></h3>
	<div><spring:theme code="text.account.addressBook.manageDeliveryAddresses" /></div>
	<div class="item_container">
		<h6 class="descriptionHeadline">
			<spring:theme code="text.headline.addaddress" text="Click to add an address" />
		</h6>
		<c:choose>
			<c:when test="${not empty addressData}">
				<a data-role="button" href="add-address" data-theme="b" data-icon="plus">
					<spring:theme code="text.account.addressBook.addAddress" text="Add new address" />
				</a>
				<p></p>
				<div class="fakeHR"></div>
				<c:forEach items="${addressData}" var="address" varStatus="rowCounter">
					<ul class="ui-li ui-li-static ui-body-b">
						<li>
							<div class="ui-grid-a" data-theme="b" data-role="content">
								<div class="ui-block-a" style="width: 100%">
									<ul>
										
										<li>${address.title}&nbsp;${address.firstName}&nbsp;${address.lastName}</li>
										<li>${address.line1}</li>
										<c:if test="${fn:length(address.line2) > 0}">
											<li>${address.line2}</li>
										</c:if>
										<li>${address.town}</li>
										<li>${address.postalCode}</li>
										<li>${address.country.name}</li>
										<li>
											<fieldset class="ui-grid-a">
												<div class="ui-block-a">
													<c:url value="/my-account/remove-address/${address.id}" var="removeAddressFormAction" />
													<form:form id="removeAddressForm${address.id}" action="${removeAddressFormAction}" method="post" class="removeAddressForm">
														<input type="hidden" name="addressId" value="${address.id}" />
														<a href="#" data-role="button" data-theme="d" data-icon="delete" class="removeAddressButton" pid="${address.id}"
															data-message='<spring:theme code="text.address.remove.confirm"/>' data-headerText='<spring:theme code="text.headertext"/>'>
															<spring:theme code="text.remove" text="Remove" />
														</a>
													</form:form>
												</div>
												<div class="ui-block-b">
													<c:choose>
															<c:when test="${not address.defaultAddress}">
																	<c:url value="/my-account/set-default-address/${address.id}" var="setDefaultAddressUrl" />
																	<a href="${setDefaultAddressUrl}" data-role="button" data-theme="d" id="edit">
																			<spring:theme code="text.setDefault" text="Set as default" />
																	</a>
															</c:when>
															<c:otherwise>
																	<span class="defaultAdressText"><spring:theme code="text.account.addressBook.yourDefaultAddress" text="Default" /></c>
															</c:otherwise>
													</c:choose>
												</div>
											</fieldset>
										</li>
										<li>
											<c:url value="/my-account/edit-address/${address.id}" var="editAddressUrl" />
											<a href="${editAddressUrl}" data-role="button" data-theme="c" data-icon="check">
												<spring:theme code="text.edit" text="Edit" />
											</a>
										</li>
									</ul>
								</div>
							</div>
						</li>
					</ul>
					<c:if test="${not rowCounter.last}">
						<div class="fakeHR"></div>
					</c:if>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<p class="emptyMessage">
					<spring:theme code="text.account.addressBook.noSavedAddresses" />
				</p>
				<div class="fakeHR"></div>
				<a data-role="button" href="add-address" data-theme="b" data-icon="plus">
					<spring:theme code="text.account.addressBook.addAddress" text="Add new address" />
				</a>
			</c:otherwise>
		</c:choose>
	</div>
</div>
