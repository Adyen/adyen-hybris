<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

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
						<span class="hidden-xs hidden-sm">Your Account</span> <a
							class="hidden-md hidden-lg" href=""><span
							class="glyphicon glyphicon-chevron-left"></span> Your Account</a>
					</div>
					<ul class="account-navigation-list">
						<li class="title">Profile</li>
						<li class="active"><a href="">Update personal details</a></li>
						<li><a href="">Change your password</a></li>
						<li><a href="">Update your email</a></li>
						<li class="title">Address Book</li>
						<li><a href="">Manage your delivery address</a></li>
						<li class="title">Payment Details</li>
						<li><a href="">Manage your payment details</a></li>
						<li class="title">Order History</li>
						<li><a href="">View your order history</a></li>
						<li><a href="">View your return history</a></li>
					</ul>

				</div>



			</div>
			<div class="col-md-9 col-lg-10">
				<div class="account-section">
					<div class="account-section-header">Profile</div>
					<div
						class="account-section-content	 account-section-content-small ">
						<div class="account-profil">

							<div class="account-profil-info">
								<div class="account-profil-info-line">
									<span>Title:</span> Mr
								</div>
								<div class="account-profil-info-line">
									<span>First Name:</span> Fitzgerald
								</div>
								<div class="account-profil-info-line">
									<span>Last Name:</span> Sharp
								</div>
								<div class="account-profil-info-line">
									<span>Email:</span> fitzgerald.Sharp@company.com
								</div>
							</div>

							<a href="#" class="btn btn-default">Change your Password</a> <a
								href="#" class="btn btn-default">Update your Personal
								Details</a> <a href="#" class="btn btn-default">Update your
								Email</a>

						</div>

					</div>
				</div>
			</div>
		</div>



</template:page>