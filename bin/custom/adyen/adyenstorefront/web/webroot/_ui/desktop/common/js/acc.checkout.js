ACC.checkout = {
	spinner: $("<img id='taxesEstimateSpinner' src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />"),

	bindAll: function ()
	{
		this.bindCheckO();
	},

	bindCheckO: function ()
	{
		var cartEntriesError = false;

		// Alternative checkout flows options
		$('.doFlowSelectedChange').change(function ()
		{
			if ('multistep-pci' == $('#selectAltCheckoutFlow').attr('value'))
			{
				$('#selectPciOption').css('display', '');
			}
			else
			{
				$('#selectPciOption').css('display', 'none');

			}
		});

		// Alternative checkout flows version of the doCheckout method to handle the checkout buttons on the cart page.
		$('.doCheckoutBut').click(function ()
		{
			var checkoutUrl = $(this).data("checkoutUrl");
			
			cartEntriesError = ACC.pickupinstore.validatePickupinStoreCartEntires();
			if (!cartEntriesError)
			{
				var expressCheckoutObject = $('.doExpressCheckout');
				if(expressCheckoutObject.is(":checked"))
				{
					window.location = expressCheckoutObject.data("expressCheckoutUrl");
				}
				else
				{
					var flow = $('#selectAltCheckoutFlow').attr('value');
					if (undefined == flow || flow == '')
					{
						// No alternate flow specified, fallback to default behaviour
						window.location = checkoutUrl;
					}
					else
					{
						// Fix multistep-pci flow
						if ('multistep-pci' == flow)
						{
						flow = 'multistep';
						}
						var pci = $('#selectPciOption').attr('value');

						// Build up the redirect URL
						var redirectUrl = checkoutUrl + '/select-flow?flow=' + flow + '&pci=' + pci;
						window.location = redirectUrl;
					}
				}
			}
			return false;
		});

		$('#estimateTaxesButton').click(function ()
		{
			$('#zipCodewrapperDiv').removeClass("form_field_error");
			$('#countryWrapperDiv').removeClass("form_field_error");

			var countryIso = $('#countryIso').val();
			if (countryIso === "")
			{
				$('#countryWrapperDiv').addClass("form_field_error");
			}
			var zipCode = $('#zipCode').val();
			if (zipCode === "")
			{
				$('#zipCodewrapperDiv').addClass("form_field_error");
			}

			if (zipCode !== "" && countryIso !== "")
			{
				$("#order_totals_container").append(ACC.checkout.spinner);
				$.getJSON("cart/estimate", {zipCode: zipCode, isocode: countryIso  }, function (estimatedCartData)
				{
					$("#estimatedTotalTax").text(estimatedCartData.totalTax.formattedValue)
					$("#estimatedTotalPrice").text(estimatedCartData.totalPrice.formattedValue)
					$(".estimatedTotals").show();
					$(".realTotals").hide();
					$("#taxesEstimateSpinner").remove();

				});
			}
		});
	}

};

$(document).ready(function ()
{
	ACC.checkout.bindAll();
});
