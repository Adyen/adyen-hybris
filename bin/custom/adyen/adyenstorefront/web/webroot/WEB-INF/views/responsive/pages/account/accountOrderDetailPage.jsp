<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
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
					<div class="account-section-header">Order Details</div>
					<div class="account-section-content	">
						<div class="account-orderdetail">
							<div class="account-orderdetail-overview">
								<div class="canel-panel">
									<button class="btn btn-default btn-block">Cancel</button>
									<p>You may cancel your order within a limited amount of
										time after placing your order.</p>
								</div>
								<strong>Order # 0001 - New</strong><br> Order Total
								$1,597.34 <br> Order Placed on 2014/06/20 at 22:01 <br>
							</div>



							<div class="account-orderdetail-item-section">
								<div class="account-orderdetail-item-section-header">
									<div class="button-panel">
										<button class="btn btn-primary btn-block">Track
											Package</button>
										<button class="btn btn-default btn-block">Edit
											Delivery Address</button>
										<button class="btn btn-default btn-block">Edit
											Shipping Method</button>
									</div>
									<strong>Shipment # 0001 - New on 2014/06/20</strong><br>
									Mr Fitzgerald Sharp<br> 572 Stratford Road, Glidden,
									Wisconsin 5860<br> <br> Shipping Method Normal

								</div>

								<div class="account-orderdetail-item-section-body">
									<ul>

										<li class="product-item">
											<div class="thumb">
												<theme:image code="img.missingProductImage.thumbnail" />
											</div>

											<div class="price-total">$988.88</div>

											<div class="details">
												<div class="name">Canon - EOS Rebel SL1 DSLR Camera w
													18-55mm IS STM lens - Black</div>
												<div class="price">$988.88</div>
												<div class="qty">Qty 1</div>
											</div>
										</li>

										<li class="product-item">
											<div class="thumb">
												<theme:image code="img.missingProductImage.thumbnail" />
											</div>

											<div class="price-total">$988.88</div>

											<div class="details">
												<div class="name">Canon - EOS Rebel SL1 DSLR Camera w
													18-55mm IS STM lens - Black</div>
												<div class="price">$988.88</div>
												<div class="qty">Qty 1</div>
											</div>
										</li>

										<li class="product-item">
											<div class="thumb">
												<theme:image code="img.missingProductImage.thumbnail" />
											</div>

											<div class="price-total">$988.88</div>

											<div class="details">
												<div class="name">Canon - EOS Rebel SL1 DSLR Camera w
													18-55mm IS STM lens - Black</div>
												<div class="price">$988.88</div>
												<div class="qty">Qty 1</div>
											</div>
										</li>

									</ul>
								</div>
								<div class="account-orderdetail-item-section-footer">
									<div class="subtotals">

										<div class="subtotal">
											Subtotal <span>$899.88</span>
										</div>
										<div class="shipping">
											Shipping <span>$899.88</span>
										</div>
										<div class="tax">
											Tax <span>$899.88</span>
										</div>

										<div class="totals">
											Total <span>$988.88</span>
										</div>
									</div>
								</div>
							</div>


							<div class="account-orderdetail-item-section">
								<div class="account-orderdetail-item-section-header">
									<strong>Pick up # 0001 - New on 2014/06/20</strong><br>
									Kawasaki Mets Mizonokuchi Hotel<br> Takatsu-Ku,
									01.01.2005, Kawasaki, 213-0001<br> (5454) 54654 45545

								</div>

								<div class="account-orderdetail-item-section-body">
									<ul>

										<li class="product-item">
											<div class="thumb">
												<theme:image code="img.missingProductImage.thumbnail" />
											</div>

											<div class="price-total">$988.88</div>

											<div class="details">
												<div class="name">Canon - EOS Rebel SL1 DSLR Camera w
													18-55mm IS STM lens - Black</div>
												<div class="price">$988.88</div>
												<div class="qty">Qty 1</div>
											</div>
										</li>

										<li class="product-item">
											<div class="thumb">
												<theme:image code="img.missingProductImage.thumbnail" />
											</div>

											<div class="price-total">$988.88</div>

											<div class="details">
												<div class="name">Canon - EOS Rebel SL1 DSLR Camera w
													18-55mm IS STM lens - Black</div>
												<div class="price">$988.88</div>
												<div class="qty">Qty 1</div>
											</div>
										</li>

										<li class="product-item">
											<div class="thumb">
												<theme:image code="img.missingProductImage.thumbnail" />
											</div>

											<div class="price-total">$988.88</div>

											<div class="details">
												<div class="name">Canon - EOS Rebel SL1 DSLR Camera w
													18-55mm IS STM lens - Black</div>
												<div class="price">$988.88</div>
												<div class="qty">Qty 1</div>
											</div>
										</li>

									</ul>
								</div>
								<div class="account-orderdetail-item-section-footer">
									<div class="subtotals">

										<div class="subtotal">
											Subtotal <span>$899.88</span>
										</div>
										<div class="shipping">
											Shipping <span>$899.88</span>
										</div>
										<div class="tax">
											Tax <span>$899.88</span>
										</div>

										<div class="totals">
											Total <span>$988.88</span>
										</div>
									</div>
								</div>
							</div>





						</div>

					</div>
				</div>
			</div>
		</div>






</template:page>