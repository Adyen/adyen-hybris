ACC.placeorder = {

	bindAll: function ()
	{
		this.bindPlaceOrder();
		this.updatePlaceOrderButton();
		this.bindSecurityCodeWhatIs();
	},
	
	bindPlaceOrder: function ()
	{
		$(".placeOrderWithSecurityCode").on("click", function (e)
		{
			ACC.common.blockFormAndShowProcessingMessage($(this));
			if($("#hppForm").attr('action') && $('#Terms1').is(':checked')){
				$("#hppForm").submit();
				e.preventDefault();
			}else{
				$(".securityCodeClass").val($("#SecurityCode").val());
				$("#placeOrderForm1").submit();
			}
		});
	},

	updatePlaceOrderButton: function ()
	{
		
		$(".place-order").removeAttr("disabled");
		// need rewrite /  class changes
	},

	bindSecurityCodeWhatIs: function ()
	{
		$('.security_code_what').bt($("#checkout_summary_payment_div").data("securityWhatText"),
				{
					trigger: 'click',
					positions: 'bottom',
					fill: '#efefef',
					cssStyles: {
						fontSize: '11px'
					}
				});
	}
};

$(document).ready(function ()
{
	ACC.placeorder.bindAll();
});


