<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<spring:url value="/my-account/update-profile" var="updateProfileUrl"/>
<spring:url value="/my-account/update-password" var="updatePasswordUrl"/>
<spring:url value="/my-account/update-email" var="updateEmailUrl"/>
<spring:url value="/my-account/address-book" var="addressBookUrl"/>
<spring:url value="/my-account/payment-details" var="paymentDetailsUrl"/>
<spring:url value="/my-account/orders" var="ordersUrl"/>
<spring:url value="/my-account/order/" var="orderDetailsUrl"/>

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
					<div class="account-section-header">Order History</div>
					<div class="account-section-content	">
					
					<c:if test="${not empty searchPageData.results}">
						<div class="account-orderhistory">
							<div class="account-orderhistory-pagination">
								<div class="row">
									<div class="col-xs-6">
										<div class="account-orderhistory-pagination-text">
											<strong>1-10</strong> of <strong>99</strong> Orders										
										</div>
									</div>
									
									<div class="col-xs-6">
										<div class="hidden-xs hidden-sm pull-right">
											<ul class="pagination">
												<li class="disabled"><span>&laquo;</span></li>
												<li class="active"><span>1 <span class="sr-only">(current)</span></span></li>
												<li><a href="#">2</a></li>
												<li><a href="#">5</a></li>
												<li><a href="#">&raquo;</a></li>
											</ul>
										</div>

										<div class="hidden-md hidden-lg pull-right">
											<ul class="pager">
												<li><a href="#">Previous</a></li>
												<li><a href="#">Next</a></li>
											</ul>
										</div>
									</div>
									
								</div>
							</div>
							
							<div class="account-orderhistory-sort">
								<select name="" id="">
									<option value="">Sort By Most Recent</option>
								</select>
							</div>
							
							<div class="account-orderhistory-list">
								<ul>
									<c:forEach items="${searchPageData.results}" var="order">
									
										<li class="account-orderhistory-list-item">
										<a href="${orderDetailsUrl}${order.code}">
											<div class="row">
												<div class="col-xs-9">
													<div class="row">
														<div class="col-sm-4">
															<div class="order-id">Order ${order.code}</div>
														</div>
														<div class="col-sm-4">
															<div class="place-at">Placed at <fmt:formatDate value="${order.placed}" dateStyle="long" timeStyle="short" type="both"/></div>
														</div>
														<div class="col-sm-4">
															<div class="price-items">
																<strong>${order.total.formattedValue} Items</strong>
															</div>
														</div>
													</div>
												</div>
												
												<div class="col-xs-3">
													<div class="status">
														<spring:theme code="text.account.order.status.display.${order.statusDisplay}"/>
													</div>
												</div>
											</div>
										</a>
										</li>
									</c:forEach>
								</ul>
							</div>
						</div>
					</c:if>
					
					<div class="account-orderhistory-pagination">
						<div class="row">
							<div class="col-xs-6">
								<div class="account-orderhistory-pagination-text">
									<strong>1-10</strong> of <strong>99</strong> Orders
								</div>
							</div>

							<div class="col-xs-6">
								<div class="hidden-xs hidden-sm pull-right">
									<ul class="pagination">
										<li class="disabled"><span>&laquo;</span></li>
										<li class="active"><span>1 <span class="sr-only">(current)</span><span></li>
										<li><a href="#">2</a></li>
										<li><a href="#">5</a></li>
										<li><a href="#">&raquo;</a></li>
									</ul>
								</div>

								<div class="hidden-md hidden-lg pull-right">
									<ul class="pager">
										<li><a href="#">Previous</a></li>
										<li><a href="#">Next</a></li>
									</ul>
								</div>
							</div>
						</div>
					</div>
						
					<c:if test="${empty searchPageData.results}">
						<spring:theme code="text.account.orderHistory.noOrders" text="You have no orders"/>
					</c:if>
				
				</div>
			</div>
		</div>
	</div>

</template:page>