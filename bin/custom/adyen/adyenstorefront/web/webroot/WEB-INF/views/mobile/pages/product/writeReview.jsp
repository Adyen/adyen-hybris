<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/mobile/formElement"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/mobile/product"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/mobile/common"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/mobile/nav"%>
<c:url value="${product.url}" var="productUrl" />
<template:page pageTitle="${pageTitle}">
	<div class="item_container_holder">
		<div id="globalMessages">
			<common:globalMessages />
		</div>
		<h3>
			<spring:theme code="review.write.title.product" arguments="${product.name}" />
		</h3>
		<p>
		<div class="item_container writeReview">
			<div class="ui-grid-a reviewDetails">
				<div class="ui-block-a">
					<a href="${productUrl}" data-transition="slide"> <product:productCartImage product="${product}" format="thumbnail" /> </a>
				</div>
				<div class="ui-block-b">
					<p>${product.summary}</p>
				</div>
			</div>
			<ul class="mFormList" data-inset="true" data-theme="d">
				<li>
					<p class="smalltext"><spring:theme code="review.required" /></p>
				</li>
				<c:url value="${product.url}/writeReview" var="reviewFormAction" />
				<form:form method="post" action="${reviewFormAction}" commandName="reviewForm">
					<common:errors />
					<li><formElement:formInputBox idKey="review.headline" labelKey="review.headline" path="headline" inputCSS="text" mandatory="true" /></li>
					<li><formElement:formTextArea idKey="review.comment" labelKey="review.comment" path="comment" areaCSS="textarea" mandatory="true" /></li>
					<spring:bind path="rating">
						<li>
							<div class="ui-field-contain ui-body ui-br">
								<label for="rating" class="ui-input-text"> <spring:theme code="review.rating" /> </label>
								<form:select path="rating" id="rating">
									<form:option value='1'>&#9733</form:option>
									<form:option value='2'>&#9733 &#9733</form:option>
									<form:option value='3'>&#9733 &#9733 &#9733</form:option>
									<form:option value='4'>&#9733 &#9733 &#9733 &#9733</form:option>
									<form:option value='5'>&#9733 &#9733 &#9733 &#9733 &#9733</form:option>
								</form:select>
								<p><form:errors path="rating" /></p>
							</div>
						</li>
					</spring:bind>
					<li><formElement:formInputBox idKey="review.alias" labelKey="review.alias" path="alias" inputCSS="text" tabindex="8" /></li>
					<li>
						<button class="form" data-theme="c" value='<spring:theme code="review.submit"/>'>
							<spring:theme code="review.submit" />
						</button>
					</li>
				</form:form>
			</ul>
		</div>
		</p>
	</div>
</template:page>
