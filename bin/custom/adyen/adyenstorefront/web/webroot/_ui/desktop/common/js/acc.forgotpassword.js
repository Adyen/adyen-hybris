ACC.forgotpassword = {

	bindAll: function()
	{
		this.bindForgotPasswordLink($('.password-forgotten'));
	},

	bindForgotPasswordLink: function(link)
	{
		link.click(function()
		{
			$.get(link.data('url')).done(function(data) {
				$.colorbox({
					html: data,
					width:500,
					height: false,
					overlayClose: false,
					onOpen: function()
					{
						$('#validEmail').remove();
					},
					onComplete: function()
					{
						var forgottenPwdForm = $('#forgottenPwdForm');
						forgottenPwdForm.ajaxForm({
							success: function(data)
							{
								if ($(data).closest('#validEmail').length)
								{
									
									if ($('#validEmail').length === 0)
									{
										$('#globalMessages').append(data);
									}
									$.colorbox.close();
								}
								else
								{
							
									$("#forgottenPwdForm .control-group").replaceWith($(data).find('.control-group'));
									$.colorbox.resize();
								}
							}
						});
						ACC.common.refreshScreenReaderBuffer();
					},
					onClosed: function()
					{
						ACC.common.refreshScreenReaderBuffer();
					}
				});
			});
		});
	}
};

$(document).ready(function()
{
	ACC.forgotpassword.bindAll();
});