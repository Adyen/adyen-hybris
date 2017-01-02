ACC.refresh = {
	refreshCartTotals: function (checkoutCartData)
	{
		$('#ajaxCart').html($('#cartTotalsTemplate').tmpl(checkoutCartData));
	},

	refreshPage: function (checkoutCartData)
	{
		//update delivery address, delivery method and payment sections, and cart totals section
		ACC.address.refreshDeliveryAddressSection(checkoutCartData);
		ACC.payment.refreshPaymentDetailsSection(checkoutCartData);
		ACC.refresh.refreshCartTotals(checkoutCartData);
		ACC.placeorder.updatePlaceOrderButton();
	},
	getCheckoutCartDataAndRefreshPage: function ()
	{
		$.getJSON(getCheckoutCartUrl, function (checkoutCartData)
		{
			ACC.refresh.refreshPage(checkoutCartData);
		});
	}
}

$.blockUI.defaults.overlayCSS = {};
$.blockUI.defaults.css = {};
