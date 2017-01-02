<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/desktop/address"%>

<div class="span-24">
	<div class="span-20 last">
		<div class="accountContentPane clearfix">
			<div class="headline"><spring:theme code="text.account.addressBook.addressDetails" text="Address Details"/></div>
			<div class="required right"><spring:theme code="form.required" text="Fields marked * are required"/></div>
			<div class="description"><spring:theme code="text.account.addressBook.addEditform" text="Please use this form to add/edit an address."/></div>
			<address:addressFormSelector supportedCountries="${countries}" regions="${regions}" cancelUrl="/my-account/address-book"/>
			<address:suggestedAddresses selectedAddressUrl="/my-account/select-suggested-address"/>
		</div>
	</div>
</div>