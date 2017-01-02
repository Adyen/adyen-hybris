ACC.langcurrency = {

	bindAll: function ()
	{
		this.bindLangCurrencySelector();
	},

	bindLangCurrencySelector: function ()
	{
		$('.lang-selector').change(function ()
		{
			$(this).parents('.lang-form').submit();
		})

		$('.currency-selector').change(function ()
		{
			$(this).parents('.currency-form').submit();
		})
	}
};

$(document).ready(function ()
{
	ACC.langcurrency.bindAll();
});
