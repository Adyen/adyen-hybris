ACCMOB.hopdebug = {

	bindAll: function ()
	{
		this.bindShowDebugMode();
	},

	bindShowDebugMode: function ()
	{
		var debugModeEnabled = $('#hopDebugMode').data("hopDebugMode");
		
		if (!debugModeEnabled && !$('#showDebugPage').attr('value'))
		{
			$('#hostedOrderPagePostForm').submit();
		}
	}
};


$(document).ready(function ()
{
	ACCMOB.hopdebug.bindAll();
});
