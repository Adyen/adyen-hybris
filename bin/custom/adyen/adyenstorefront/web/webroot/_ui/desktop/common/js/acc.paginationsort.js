ACC.paginationsort = {

	downUpKeysPressed: false,

	bindAll: function ()
	{
		this.bindPaginaSort();
	},
	bindPaginaSort: function ()
	{
		with (ACC.paginationsort)
		{
			bindSortForm($('#sort_form1'));
			bindSortForm($('#sort_form2'));
		}
	},
	bindSortForm: function (sortForm)
	{
		if ($.browser.msie)
		{
			this.sortFormIEFix($(sortForm).children('select'), $(sortForm).children('select').val());
		}

		sortForm.change(function ()
		{
			if (!$.browser.msie)
			{
				this.submit();
			}
			else
			{
				if (!ACC.paginationsort.downUpPressed)
				{
					this.submit();
				}
				ACC.paginationsort.downUpPressed = false;
			}
		});
	},
	sortFormIEFix: function (sortOptions, selectedOption)
	{
		sortOptions.keydown(function (e)
		{
			// Pressed up or down keys
			if (e.keyCode === 38 || e.keyCode === 40)
			{
				ACC.paginationsort.downUpPressed = true;
			}
			// Pressed enter
			else if (e.keyCode === 13 && selectedOption !== $(this).val())
			{
				$(this).parent().submit();
			}
			// Any other key
			else
			{
				ACC.paginationsort.downUpPressed = false;
			}
		});
	}
};

$(document).ready(function ()
{
	ACC.paginationsort.bindAll();
});
