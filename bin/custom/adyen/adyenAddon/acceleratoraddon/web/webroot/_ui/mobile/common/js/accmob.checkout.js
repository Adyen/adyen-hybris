ACCMOB.checkout = {
	bindAll: function ()
	{
		this.bindCheck();
	},

	bindCheck: function ()
	{
		$('#checkoutButton').click(function ()
		{
				var expressCheckoutObject = $('.doExpressCheckout');
				if(expressCheckoutObject.is(":checked"))
				{
					$("#checkoutButton").attr("href", expressCheckoutObject.data("expressCheckoutUrl"))
				}
			return true;
		});
	}
};

$(document).ready(function ()
{
	ACCMOB.checkout.bindAll();
});
