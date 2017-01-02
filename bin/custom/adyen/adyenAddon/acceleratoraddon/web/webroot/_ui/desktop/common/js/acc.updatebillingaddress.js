ACC.updatebillingaddress = {
	bindCycleFocusEvent: function ()
	{
		$('#lastInTheForm').blur(function ()
		{
			$('#paymentDetailsForm [tabindex$="10"]').focus();
		})
	},

	updateBillingAddressForm: function ()
	{
		var newAddress = $('#differentAddress').attr("checked");
		if (newAddress)
		{
			$("#newBillingAddressFields :input").removeAttr('disabled');
		}
		else
		{
			$("#newBillingAddressFields :input").attr('disabled', 'disabled');
		}
	}
}

$(document).ready(function ()
{
	ACC.updatebillingaddress.updateBillingAddressForm();
	ACC.silentorderpost.displayStartDateIssueNum();

	if ($("#differentAddress").length > 0)
	{
		$("#differentAddress").click(function ()
		{
			ACC.updatebillingaddress.updateBillingAddressForm();
		})
	}
	else
	{
		$("#newBillingAddressFields :input").removeAttr('disabled');
	}

	$('#paymentDetailsForm [tabindex="1"]').change(function ()
	{
		ACC.silentorderpost.displayStartDateIssueNum();
	});

	ACC.updatebillingaddress.bindCycleFocusEvent();
});
