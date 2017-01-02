<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

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


		<div class="row">
			<div class="col-md-3 col-lg-2">
				<div class="account-navigation">
					<div class="account-navigation-header">
						<span class="hidden-xs hidden-sm">
							<spring:theme code="text.account.yourAccount" text="Your Account"/>
						</span> 
						<a class="hidden-md hidden-lg" href="">
							<span class="glyphicon glyphicon-chevron-left"></span> 
							<spring:theme code="text.account.yourAccount" text="Your Account"/> 
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
					<div class="account-section-header">Address Details</div>
					<div
						class="account-section-content	 account-section-content-small ">
						<form action="">
						
							<p>Please use this form to add an address.</p>

							<div class="form-group">
								<label for="">Country</label> <select name="" id=""
									class="form-control">
									<option value="">Country</option>
								</select>
							</div>
							<div class="form-group">
								<label for="">Title</label> <select name="" id=""
									class="form-control">
									<option value="">Title</option>
								</select>
							</div>

							<div class="form-group">
								<label for="">First Name</label> <input type="text"
									class="form-control" id="" placeholder="First Name">
							</div>

							<div class="form-group">
								<label for="">Last Name</label> <input type="text"
									class="form-control" id="" placeholder="Last Name">
							</div>

							<div class="form-group">
								<label for="">Address Line 1</label> <input type="text"
									class="form-control" id="" placeholder="Address Line 1">
							</div>

							<div class="form-group">
								<label for="">Address Line 2</label> <input type="text"
									class="form-control" id="" placeholder="Address Line 2">
							</div>

							<div class="form-group">
								<label for="">City</label> <input type="text"
									class="form-control" id="" placeholder="City">
							</div>

							<div class="form-group">
								<label for="">ZIP/Postal Code</label> <input type="text"
									class="form-control" id="" placeholder="ZIP/Postal Code">
							</div>

							<div class="checkbox">
								<label> <input type="checkbox"> Make this my
									default address
								</label>
							</div>



							<button type="submit" class="btn btn-primary btn-block">Add
								Address</button>
						</form>

					</div>
				</div>
			</div>
		</div>






</template:page>