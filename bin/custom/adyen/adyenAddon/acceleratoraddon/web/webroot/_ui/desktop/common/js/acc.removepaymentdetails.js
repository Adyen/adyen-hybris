ACC.removepayment = {

	bindAll: function ()
	{
		this.bindRemovePaymentDetails();
	},

	bindRemovePaymentDetails: function ()
	{
		$('.submitRemove').on("click", function ()
		{
			$('#removePaymentDetails' + $(this).attr('id')).submit();
		});
		$('.submitSetDefault').on("click", function ()
		{
			$('#setDefaultPaymentDetails' + $(this).attr('id')).submit();
		});
	}

};

$(document).ready(function ()
{
	ACC.removepayment.bindAll();
});
