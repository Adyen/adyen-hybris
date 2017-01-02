ACC.checkoutcartdetails = {

	bindAll: function ()
	{
		this.bindCheckCartDetails();
	},

	bindCheckCartDetails: function ()
	{
		$("#checkout-cart-details").hide();

		$("#checkout-cart-details-btn").click(function (e)
		{
			e.preventDefault();
			$("#checkout-cart-details").toggle();
			if ($("#checkout-cart-details").is(":visible"))
			{
				$("#checkout-cart-details-btn").html(hideText);
			}
			else
			{
				$("#checkout-cart-details-btn").html(showText);
			}
		});
	}
};

$(document).ready(function ()
{
	ACC.checkoutcartdetails.bindAll();
});
