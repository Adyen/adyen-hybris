ACC.password = {

	bindAll: function ()
	{
		$(":password").bind("cut copy paste", function (e)
		{
			e.preventDefault();
		});
	}
};

$(document).ready(function ()
{
	ACC.password.bindAll();
});
