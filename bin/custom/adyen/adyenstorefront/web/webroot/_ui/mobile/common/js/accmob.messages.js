ACCMOB.messages = {

	createDialog: function (config)
	{
		$.mobile.easydialog(config);
	},

	bindGlobalMessages: function ()
	{
		var accErrorMsgs = $("#accErrorMsgs");
		if (accErrorMsgs.length > 0)
		{
			$.mobile.easydialog({
				content: accErrorMsgs.html(),
				header: accErrorMsgs.data('headertext'),
				type: 'error'
			});
		}

		var accInfoMsgs = $("#accInfoMsgs");
		if (accInfoMsgs.length > 0)
		{

			$.mobile.easydialog({
				content: accInfoMsgs.html(),
				header: accInfoMsgs.data('headertext'),
				type: 'info'
			});
			
		}

		var accConfMsgs = $("#accConfMsgs");
		if (accConfMsgs.length > 0)
		{
			$.mobile.easydialog({
				content: accConfMsgs.html(),
				header: accConfMsgs.data('headertext'),
				type: 'conf'
			});
			
		}
	},

	bindFormErrors: function ()
	{
		var errors = $("#form-errors");
		var accErrorMsgs = $("#accErrorMsgs");

		if (errors.length > 0)
		{
			$.mobile.easydialog({
				content: accErrorMsgs.html(),
				header: accErrorMsgs.data('headertext'),
				type: 'error'
			});
		}
	},

	initialize: function ()
	{
		with (ACCMOB.messages)
		{
			bindGlobalMessages();
			bindFormErrors();
		}
	}
};

$(document).ready(function ()
{
	ACCMOB.messages.initialize();
});
