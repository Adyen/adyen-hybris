ACC.checkoutpickupdetails = {

	bindAll: function ()
	{
		this.bindPickupDetails();
	},

	bindPickupDetails: function ()
	{
		$(".pickupSummaryDetail").hide();

		$(".pickupSummaryDetailsButton").click(function (e)
		{
			e.preventDefault();
			$(this).parent().children(".pickupSummaryDetail").toggle();
			if ($(this).parent().children(".pickupSummaryDetail").is(":visible"))
			{
				$(this).html(hideText);
			}
			else
			{
				$(this).html(showText);
			}
		});
	}
};

$(document).ready(function ()
{
	ACC.checkoutpickupdetails.bindAll();
});
