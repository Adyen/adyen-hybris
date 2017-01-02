ACCMOB.placeorder = {

	initialize: function ()
	{
		with (ACCMOB.placeorder)
		{
			bindTermsAndConditions($('#Terms1'), $('#Terms2'));
			bindTermsAndConditions($('#Terms2'), $('#Terms1'));
		}
	},

	bindTermsAndConditions: function (checkbox1, checkbox2)
	{
		checkbox1.change(function ()
		{
			$(this).prop('checked', function (i, val)
			{
				checkbox2.prop('checked', val);
				checkbox2.checkboxradio('refresh');
			});
		});
	}
};

$(document).ready(function ()
{
	ACCMOB.placeorder.initialize();
});
