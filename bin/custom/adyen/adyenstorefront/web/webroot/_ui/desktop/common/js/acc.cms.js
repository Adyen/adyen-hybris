ACC.cms = {

	bindAll: function()
	{
		this.bindNavigationBarMenu();
	},

	bindNavigationBarMenu: function()
	{
		$('li.La ul ul a').each(function ()
		{
			$(this).focus(function ()
			{
				$(this).addClass('focused');
				var menuParent = $(this).closest('ul').parent().closest('ul');
				$(menuParent).addClass('dropdown-visible');
			});

			$(this).blur(function ()
			{
				$(this).removeClass('focused');
				var menuParent = $(this).closest('ul').parent().closest('ul');
				if (!$('.focused', menuParent).length)
				{
					$(menuParent).removeClass('dropdown-visible');
				}
			});
		});
	}
};

$(document).ready(function()
{
	ACC.cms.bindAll();
});
