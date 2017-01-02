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
					<div class="account-section-header">Payment Details</div>
					<div
						class="account-section-content	 account-section-content-small ">

						<div class="account-paymentdetails">
							<p>Manage your payment details.</p>
							<a href="" class="btn btn-primary btn-block">Add New Payment</a>
							<ul class="account-paymentdetails-list">


								<li>
									<div class="remove">
										<button class="btn btn-default">
											<span class="glyphicon glyphicon-trash"></span>
										</button>
									</div> <strong>Mr Fitzgerald Sharp</strong> (default)<br>Visa<br>************5894<br>07/2018<br>572
									Stratford Road<br> 951 Walker Court<br>Glidden<br>
									Wisconsin 5860

									<div class="actions">

										<button class="btn btn-primary">Edit</button>
									</div>
								</li>

								<li>
									<div class="remove">
										<button class="btn btn-default">
											<span class="glyphicon glyphicon-trash"></span>
										</button>
									</div> <strong>Mr Roberta Robinson</strong><br>Visa<br>************5894<br>07/2018<br>883
									Carroll Street<br> 377 Losee Terrace<br>Fairhaven<br>
									Wyoming 6949

									<div class="actions">

										<button class="btn btn-default">Set as Default</button>

										<button class="btn btn-primary">Edit</button>
									</div>
								</li>

								<li>
									<div class="remove">
										<button class="btn btn-default">
											<span class="glyphicon glyphicon-trash"></span>
										</button>
									</div> <strong>Mr Doreen Hart</strong><br>Visa<br>************5894<br>07/2018<br>175
									Bragg Street<br> 758 Beaumont Street<br>Wescosville<br>
									Oklahoma 1492

									<div class="actions">

										<button class="btn btn-default">Set as Default</button>

										<button class="btn btn-primary">Edit</button>
									</div>
								</li>

								<li>
									<div class="remove">
										<button class="btn btn-default">
											<span class="glyphicon glyphicon-trash"></span>
										</button>
									</div> <strong>Mr Head Decker</strong><br>Visa<br>************5894<br>07/2018<br>568
									Ainslie Street<br> 503 Lewis Avenue<br>Ripley<br>
									Virginia 1987

									<div class="actions">

										<button class="btn btn-default">Set as Default</button>

										<button class="btn btn-primary">Edit</button>
									</div>
								</li>

							</ul>
							<a href="" class="btn btn-primary btn-block">Add New Payment</a>

						</div>



					</div>
				</div>
			</div>
		</div>






</template:page>