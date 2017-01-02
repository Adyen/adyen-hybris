ACC.langcurrency = {

	bindAll: function ()
	{
		this.bindLangCurrencySelector();
	},

	bindLangCurrencySelector: function ()
	{
		$('#lang-selector').change(function ()
		{
			$('#lang-form').submit();
		})

		$('#currency-selector').change(function ()
		{
			$('#currency-form').submit();
		})
	}
};

$(document).ready(function ()
{
	ACC.langcurrency.bindAll();
});
