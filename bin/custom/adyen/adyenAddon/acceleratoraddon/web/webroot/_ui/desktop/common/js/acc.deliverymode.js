ACC.deliverymode = {

	bindEditDeliveryMethodButton: function ()
	{
		$('.summaryDeliveryMode .editButton').on("click", function ()
		{
			$.ajax({
				url: getDeliveryModesUrl,
				type: 'GET',
				dataType: 'json',
				success: function (data)
				{
					// Fill the available delivery addresses and select button
					$('#delivery_modes_dl').html($('#deliveryModesTemplate').tmpl({deliveryModes: data}));
					$('#delivery_modes_button').html($('#deliveryModeButton').tmpl({deliveryModes: data}));

					// Show the delivery modes popup
					$.colorbox({
						inline: true,
						href: "#popup_checkout_delivery_modes",
						height: false,
						overlayClose: false,
						onComplete: function ()
						{
							ACC.common.refreshScreenReaderBuffer();
						},
						onClosed: function ()
						{
							ACC.common.refreshScreenReaderBuffer();
						}
					});

					ACC.deliverymode.bindUseThisDeliveryMode();
				},
				error: function (xht, textStatus, ex)
				{
					alert("Failed to get delivery modes. Error details [" + xht + ", " + textStatus + ", " + ex + "]");
				}
			});
			return false;
		});
	},

	bindUseThisDeliveryMode: function ()
	{
		$('#use_this_delivery_method').click(function ()
		{
			var selectedCode = $('input:radio[name=delivery]:checked').val();
			if (selectedCode)
			{
				$.ajax({
					url: setDeliveryModeUrl,
					type: 'POST',
					dataType: 'json',
					data: {modeCode: selectedCode},
					beforeSend: function ()
					{
						$.colorbox.toggleLoadingOverlay();
					},
					success: function (data)
					{
						if (data != null)
						{
							ACC.refresh.refreshPage(data);
							parent.$.colorbox.close();
						}
						else
						{
							alert("Failed to set delivery mode");
						}
					},
					error: function (xht, textStatus, ex)
					{
						alert("Ajax call failed while trying to set delivery mode. Error details [" + xht + ", " + textStatus + ", " + ex + "]");
					},
					complete: function ()
					{
						$.colorbox.toggleLoadingOverlay();						
					}
					
				});
			}
			return false;
		});
	},

	refreshDeliveryMethodSection: function (data)
	{
		$('#checkout_summary_deliverymode_ul').replaceWith($('#deliveryModeSummaryTemplate').tmpl(data));
		ACC.deliverymode.bindEditDeliveryMethodButton();
	}
}

$(document).ready(function ()
{
	with (ACC.deliverymode)
	{
		bindEditDeliveryMethodButton();
	}
});
