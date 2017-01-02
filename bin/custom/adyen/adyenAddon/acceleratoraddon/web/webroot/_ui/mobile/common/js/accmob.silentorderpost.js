ACCMOB.silentorderpost = {

	bindUseDeliveryAddress: function ()
	{
		$('#useDeliveryAddress').on("change", function ()
		{
			if ($('#useDeliveryAddress').is(":checked"))
			{
				var options = {'countryIsoCode': $('#useDeliveryAddressFields').data('countryisocode'), 'useDeliveryAddress': true};
				ACCMOB.silentorderpost.enableAddressForm();
				ACCMOB.silentorderpost.displayCreditCardAddressForm(options, ACCMOB.silentorderpost.useDeliveryAddressSelected);
			}
			else
			{
				ACCMOB.silentorderpost.clearAddressForm();
				ACCMOB.silentorderpost.enableAddressForm();
			}
		});

		if ($('#useDeliveryAddress').is(":checked"))
		{
			ACCMOB.silentorderpost.disableAddressForm();
		}

	},

	bindSubmitSilentOrderPostForm: function ()
	{
		$('.submit_silentOrderPostForm').click(function ()
		{
			ACCMOB.common.showPageLoadingMsg();
			$('.billingAddressForm').filter(":hidden").remove();
			ACCMOB.silentorderpost.enableAddressForm();
			$('#silentOrderPostForm').submit();
		});
	},

	bindCycleFocusEvent: function ()
	{
		$('#lastInTheForm').blur(function ()
		{
			$('#silentOrderPostForm [tabindex$="10"]').focus();
		})
	},
	displayStartDateIssueNum: function ()
	{
		var cardType = $('#silentOrderPostForm [tabindex="1"]').val();
		if (cardType == 'maestro' || cardType == 'switch')
		{
			$('#startDate').removeAttr('hidden');
			$('#issueNum').removeAttr('hidden');
		}
		else
		{
			$('#startDate').attr('hidden', 'true');
			$('#issueNum').attr('hidden', 'true');
		}
	},

	isEmpty: function (obj)
	{
		if (typeof obj == 'undefined' || obj === null || obj === '') return true;
		return false;
	},

	disableAddressForm: function ()
	{
		$('input[id^="address\\."]').prop('disabled', true);
		$('select[id^="address\\."]').prop('disabled', true);
	},

	enableAddressForm: function ()
	{
		$('input[id^="address\\."]').prop('disabled', false);
		$('select[id^="address\\."]').prop('disabled', false);
	},

	clearAddressForm: function ()
	{
		$('input[id^="address\\."]').val("");
		$('select[id^="address\\."]').val("");
		$('#address\\.country').selectmenu("refresh");
		$('#address\\.region').selectmenu("refresh");
	},

	useDeliveryAddressSelected: function ()
	{
		if ($('#useDeliveryAddress').is(":checked"))
		{
			$('#address\\.country').val($('#useDeliveryAddressFields').data('countryisocode'));
			ACCMOB.silentorderpost.updateAddressFormElements();
			ACCMOB.silentorderpost.disableAddressForm();
		}
		else
		{
			ACCMOB.silentorderpost.clearAddressForm();
			ACCMOB.silentorderpost.enableAddressForm();
		}
	},

	bindCreditCardAddressForm: function ()
	{
		$('#billingCountrySelector :input').on("change", function ()
		{
			var options = {'countryIsoCode': $(this).val(), 'useDeliveryAddress': false};
			ACCMOB.silentorderpost.displayCreditCardAddressForm(options, ACCMOB.silentorderpost.updateAddressFormElements);
		});
	},

	displayCreditCardAddressForm: function (options, callback)
	{
		$.ajax({
			url: ACCMOB.config.encodedContextPath + '/checkout/multi/sop/billingaddressform',
			async: true,
			data: options,
			dataType: "html",
			beforeSend: function ()
			{
				ACCMOB.common.showPageLoadingMsg();
			}
		}).done(function (data)
				{
					$("#billingAddressForm").html($(data).html());
					if (typeof callback == 'function')
					{
						callback.call();
					}
					ACCMOB.common.hidePageLoadingMsg();
				});
	},

	updateAddressFormElements: function ()
	{
		$('#address\\.country').selectmenu("refresh");
		$("div.billingAddressForm input[type='checkbox']").checkboxradio();
		$("div.billingAddressForm input[type='text']").textinput();
		$("div.billingAddressForm [data-role=button]").button();
		$("div.billingAddressForm fieldset").controlgroup();
		$("div.billingAddressForm select").selectmenu();
	}
};

$(document).ready(function ()
{
	with (ACCMOB.silentorderpost)
	{
		displayStartDateIssueNum();
		$('#silentOrderPostForm [tabindex="1"]').on("change", function ()
		{
			displayStartDateIssueNum();
		});
		bindUseDeliveryAddress();
		bindSubmitSilentOrderPostForm();
		bindCreditCardAddressForm();
	}
});

