ACC.payment = {

	bindAddPaymentMethodButton: function ()
	{
		$('.summaryPayment .editButton').on("click", function ()
		{
			
			
		
			
			$.ajax({
				url: getPaymentDetailsFormUrl,
				data: {createUpdateStatus:'' },
				success: function(){
					ACC.payment.bindCreateUpdatePaymentDetailsForm();
					
					$.colorbox({
						inline: true,
						href: "#popup_checkout_add_payment_method",
						height: false,
						overlayClose: false,
						onComplete: function ()
						{
							
							$('#colorbox #adyenPaymentDetailsForm').show();
							$('#colorbox  #summaryOverlayViewSavedPaymentMethods').show();
							$('#colorbox  #summaryPaymentSavedPaymentMethods').hide();
							$.colorbox.resize();
							ACC.common.refreshScreenReaderBuffer();
						},
						onClosed: function ()
						{
							ACC.common.refreshScreenReaderBuffer();
						}
					});
					
				}
			});
			
		
			return false;
		});
	},
	
	
	bindUseSavedCardButton: function ()
	{
		
		
		$(document).on("click",'#summaryOverlayViewSavedPaymentMethods', function ()
		{
			$('#adyenPaymentDetailsForm').hide();
			$('#summaryOverlayViewSavedPaymentMethods').hide();
			$('#summaryPaymentSavedPaymentMethods').show();
			
			$.ajax({
				url: getSavedCardsUrl,
				type: 'GET',
				cache: false,
				dataType: 'json',
				success: function (data)
				{
					console.log(data)
					// Fill the available saved cards
					$('#summaryPaymentSavedPaymentMethods').html($('#savedCardsTemplate').tmpl({savedCards: data}));
					ACC.payment.bindUseThisSavedCardButton();
					ACC.payment.bindEnterNewPaymentButton();

					// Show the saved cards popup
					$.colorbox({
						inline: true,
						href: "#popup_checkout_saved_payment_method",
						height: false,
						innerHeight: "530px",
						onComplete: function ()
						{
							ACC.common.refreshScreenReaderBuffer();
						},
						onClosed: function ()
						{
							ACC.common.refreshScreenReaderBuffer();
						}
					});
				},
				error: function (xht, textStatus, ex)
				{
					alert("Failed to get saved cards. Error details [" + xht + ", " + textStatus + ", " + ex + "]");
				}
			});

			return false;
		});
	},

	bindEnterNewPaymentButton: function ()
	{
	
		
		$('button.enter_new_payment_button').on("click", function ()
		{
			// Show the payment method popup
			$.colorbox({
				inline: true,
				href: "#popup_checkout_add_payment_method",
				height: false,
				innerHeight: "930px",
				onComplete: function ()
				{
					
					$('#colorbox #adyenPaymentDetailsForm').show();
					$('#colorbox  #summaryOverlayViewSavedPaymentMethods').show();
					$('#colorbox  #summaryPaymentSavedPaymentMethods').hide();
					ACC.common.refreshScreenReaderBuffer();
				},
				onClosed: function ()
				{
					ACC.common.refreshScreenReaderBuffer();
				}
			});
			return false;
		});
	},

	bindCreateUpdatePaymentDetailsForm: function ()
	{
		ACC.payment.bindUseSavedCardButton();

		$('.create_update_payment_form').each(function ()
		{
			var options = {
				type: 'POST',
				beforeSubmit: function ()
				{
					
					
					
					$('#popup_checkout_add_payment_method').block({ message: "<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />" });
				},
				success: function (data)
				{
					$('#popup_checkout_add_payment_method').html(data);
					var status = $('.create_update_payment_id').attr('status');
					if (status != null && "success" == status.toLowerCase())
					{
						alert(11111);
						ACC.refresh.getCheckoutCartDataAndRefreshPage();
						parent.$.colorbox.close();
					}
					else
					{
						ACC.payment.bindCreateUpdatePaymentDetailsForm();
					}
				},
				error: function (xht, textStatus, ex)
				{
					alert("Failed to create/update payment details. Error details [" + xht + ", " + textStatus + ", " + ex + "]");
				},
				complete: function ()
				{
					$('#popup_checkout_add_payment_method').unblock();
				}
			};

			$(this).ajaxForm(options);
		});

	},

	bindUseThisSavedCardButton: function ()
	{
		$(document).on("click",'.use_this_saved_card_button', function ()
		{
			var paymentId = $(this).attr('data-payment');
			$.postJSON(setPaymentDetailsUrl, {paymentId: paymentId}, ACC.payment.handleSelectSavedCardSuccess);
			return false;
		});
	},

	handleSelectSavedCardSuccess: function (data)
	{
		if (data != null)
		{
			ACC.refresh.refreshPage(data);

			parent.$.colorbox.close();
		}
		else
		{
			alert("Failed to set payment details");
		}
	},

	refreshPaymentDetailsSection: function (data)
	{
		$('.summaryPayment').replaceWith($('#paymentSummaryTemplate').tmpl(data));

		//bind edit payment details button
		ACC.payment.bindAddPaymentMethodButton();
		if (!typeof bindSecurityCodeWhatIs == 'undefined')
			bindSecurityCodeWhatIs();
	},
	
	showSavedPayments: function ()
	{
		$(document).on("click","#viewSavedPayments",function(){
			var data = $("#savedPaymentListHolder").html();
			$.colorbox({
				
				height: false,
				html: data,
				onComplete: function ()
				{
					
					$(this).colorbox.resize();
				}
			});
			
		})
		
		
	},

	bindPaymentCardTypeSelect: function ()
	{
		$("#card_cardType").change(function ()
		{
			var cardType = $(this).val();
			if (cardType == '024')
			{
				$('#startDate, #issueNum').show();
			}
			else
			{
				$('#startDate, #issueNum').hide();
			}
		});
	},
	
}


$(document).ready(function ()
{
	ACC.payment.bindAddPaymentMethodButton();
	ACC.payment.bindPaymentCardTypeSelect();
	ACC.payment.showSavedPayments();
	if (!typeof bindSecurityCodeWhatIs == 'undefined')
	{
		bindSecurityCodeWhatIs();
	}
});
	
	
	
	
