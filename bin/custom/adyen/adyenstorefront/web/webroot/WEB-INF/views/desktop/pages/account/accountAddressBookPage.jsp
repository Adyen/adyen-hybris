<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<div class="span-20 last">
	<div class="accountContentPane clearfix">
		<div class="headline">
			<spring:theme code="text.account.addressBook" text="Address Book"/>
		</div>
		<div class="description">
			<spring:theme code="text.account.addressBook.manageYourAddresses" text="Manage your address book"/>
		</div>
		<c:choose>
			<c:when test="${not empty addressData}">
				<c:forEach items="${addressData}" var="address">
					<div class="addressItem">
						<ycommerce:testId code="addressBook_address_label">
							<ul>
								<li>${fn:escapeXml(address.title)}&nbsp;${fn:escapeXml(address.firstName)}&nbsp;${fn:escapeXml(address.lastName)}</li>
								<li>${fn:escapeXml(address.line1)}</li>
								<li>${fn:escapeXml(address.line2)}</li>
								<li>${fn:escapeXml(address.town)}</li>
								<li>${fn:escapeXml(address.region.name)}</li>
								<li>${fn:escapeXml(address.postalCode)}</li>
								<li>${fn:escapeXml(address.country.name)}</li>
							</ul>
						</ycommerce:testId>
						<div class="buttons">
							<ycommerce:testId code="addressBook_addressOptions_label">
								<c:if test="${not address.defaultAddress}">
									<ycommerce:testId code="addressBook_isDefault_button"><a class="button" href="set-default-address/${address.id}">
										<spring:theme code="text.setDefault" text="Set as default"/>
									</a></ycommerce:testId>
								</c:if>
								<c:if test="${address.defaultAddress}">
									<ycommerce:testId code="addressBook_isDefault_label">
										<span class="is-default-address"><spring:theme code="text.default" text="Default"/></span>
									</ycommerce:testId>
								</c:if>
								<ycommerce:testId code="addressBook_editAddress_button">
									<a class="button" href="edit-address/${address.id}">
										<spring:theme code="text.edit" text="Edit"/>
									</a>
								</ycommerce:testId>
								<ycommerce:testId code="addressBook_removeAddress_button">
									<a class="button removeAddressButton" data-address-id="${address.id}"><spring:theme code="text.remove" text="Remove"/></a>
								</ycommerce:testId>
							</ycommerce:testId>
						</div>
					</div>
					<div style="display:none">
						<div id="popup_confirm_address_removal_${address.id}">
							<div class="addressItem">
								<ul>
									<li>${fn:escapeXml(address.title)}&nbsp;${fn:escapeXml(address.firstName)}&nbsp;${fn:escapeXml(address.lastName)}</li>
									<li>${fn:escapeXml(address.line1)}</li>
									<li>${fn:escapeXml(address.line2)}</li>
									<li>${fn:escapeXml(address.town)}</li>
									<li>${fn:escapeXml(address.region.name)}</li>
									<li>${fn:escapeXml(address.postalCode)}</li>
									<li>${fn:escapeXml(address.country.name)}</li>
								</ul>
								<spring:theme code="text.adress.remove.confirmation" text="Are you sure you would like to delete this address?"/>
								<div class="buttons">
									<a class="button removeAddressButton" data-address-id="${address.id}" href="remove-address/${address.id}">
										<spring:theme code="text.yes" text="Yes"/>
									</a>
									<a class="button closeColorBox" data-address-id="${address.id}">
										<spring:theme code="text.no" text="No"/>
									</a>
								</div>
							</div>
						</div>
					</div>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<p class="emptyMessage">
					<spring:theme code="text.account.addressBook.noSavedAddresses"/>
				</p>
			</c:otherwise>
		</c:choose>
		<ycommerce:testId code="addressBook_addNewAddress_button">
			<a href="add-address" class="button positive">
				<spring:theme code="text.account.addressBook.addAddress" text="Add new address"/>
			</a>
		</ycommerce:testId>
	</div>
</div>