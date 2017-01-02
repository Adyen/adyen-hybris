<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:url value="/my-account/update-profile" var="updateProfileUrl"/>
<spring:url value="/my-account/update-password" var="updatePasswordUrl"/>
<spring:url value="/my-account/update-email" var="updateEmailUrl"/>
<spring:url value="/my-account/address-book" var="addressBookUrl"/>
<spring:url value="/my-account/payment-details" var="paymentDetailsUrl"/>
<spring:url value="/my-account/orders" var="ordersUrl"/>

<template:page pageTitle="${pageTitle}">
	
	<div class="global-alerts">
		<div class="alert alert-info" role="alert">
			<spring:theme code="text.page.message.underconstruction" text="Information: Page Under Construction - Not Completely Functional"/>
		</div>
	</div>
	
	<div class="container">
		<div class="row">
			<div class="col-md-3 col-lg-2">
				<div class="account-navigation">
					<div class="account-navigation-header">
						<span class="hidden-xs hidden-sm">
							<spring:theme code="text.account.yourAccount" text="My Account"/>
						</span> 
						<a class="hidden-md hidden-lg" href="">
							<span class="glyphicon glyphicon-chevron-left"></span> 
							<spring:theme code="text.account.yourAccount" text="My Account"/> 
						</a>
					</div>
					<ul class="account-navigation-list">
						<li class="title">
							<spring:theme code="text.account.profile" text="Profile"/>
						</li>
						<li>
							<a href="${updateProfileUrl}">
								<spring:theme code="text.account.profile.updatePersonalDetails" text="Update personal details"/> 
							</a>
						</li>
						<li>
							<a href="${updatePasswordUrl}">
								<spring:theme code="text.account.profile.changePassword" text="Change your password"/> 
							</a>
						</li>
						<li>
							<a href="${updateEmailUrl}">
								<spring:theme code="text.account.profile.updateEmail" text="Update your email"/> 
							</a>
						</li>
						<li class="title">
							<spring:theme code="text.account.addressBook" text="Address Book"/>
						</li>
						<li>
							<a href="${addressBookUrl}">
								<spring:theme code="text.account.addressBook.manageDeliveryAddresses" text="Manage your delivery address"/> 
							</a>
						</li>
						<li class="title">
							<spring:theme code="text.account.paymentDetails" text="Payment Details"/>
						</li>
						<li>
							<a href="${paymentDetailsUrl}">
								<spring:theme code="text.account.paymentDetails.managePaymentDetails" text="Manage your payment details"/> 
							</a>
						</li>
						<li class="title">
							<spring:theme code="text.account.orderHistory" text="Order History"/>
						</li>
						<li>
							<a href="${ordersUrl}">
								<spring:theme code="text.account.orderHistory.viewOrders" text="View your order history"/> 
							</a>
						</li>
					</ul>
				</div>

			</div>
			<div class="col-md-9 col-lg-10">
				<div class="account-section">
					<div class="account-section-header">
						<spring:theme code="text.account.addressBook" text="Address Book"/>
					</div>
					<div class="account-section-content	 account-section-content-small ">
						<div class="account-addressbook">
							<p>Manage your address book.</p>
							<c:choose>
								<c:when test="${not empty addressData}">
							<ul class="account-addressbook-list">
							<c:forEach items="${addressData}" var="address">
				
								<li>
									<div class="remove">
										<button class="btn btn-default">
											<span class="glyphicon glyphicon-trash"></span>
										</button>
									</div> <strong>${fn:escapeXml(address.title)}&nbsp;${fn:escapeXml(address.firstName)}&nbsp;${fn:escapeXml(address.lastName)}</strong><br>${fn:escapeXml(address.line1)}
									<c:if test="${not empty fn:escapeXml(address.line2)}">
									<br>
									${fn:escapeXml(address.line2)}
									</c:if><br>${fn:escapeXml(address.town)}&nbsp;${fn:escapeXml(address.region.name)}<br> ${fn:escapeXml(address.country.name)}&nbsp;${fn:escapeXml(address.postalCode)}

									<div class="actions">

										<button class="btn btn-default">Set as Default</button>

										<ycommerce:testId code="addressBook_editAddress_button">
											<a class="btn btn-default" href="edit-address/${address.id}">
												<spring:theme code="text.edit" text="Edit"/>
											</a>
										</ycommerce:testId>
									</div>
								</li>
							</c:forEach>
							</ul>
							</c:when>
							</c:choose>
							<a href="" class="btn btn-primary btn-block">Add New Address</a>
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>
	
</template:page>