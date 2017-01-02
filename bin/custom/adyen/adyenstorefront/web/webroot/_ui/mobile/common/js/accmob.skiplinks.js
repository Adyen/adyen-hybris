ACCMOB.skiplinks = {

	bindAll: function ()
	{
		this.bindLinks();
	},

	bindLinks: function ()
	{
		$("a[href^='#']").not("a[href='#']").click(function ()
		{
			var target = $(this).attr("href");
			$(target).attr("tabIndex", -1).focus();
		});
	}
};

$(document).ready(function ()
{
	if ($.browser.webkit)
	{
		ACCMOB.skiplinks.bindAll();
	}
});
