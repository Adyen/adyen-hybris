ACC.stars = {

	bindStarsWrapperRadioButtons: function(radioButtons)
	{
		var length = radioButtons.length;
		radioButtons.change(function() {
			for (var btnNo = 1; btnNo <= length; btnNo++)
			{
				var ratingId = '#rating' + btnNo;

				if (btnNo <= $(this).val())
				{
					$(ratingId).prev().removeClass('no_star');
				}
				else
				{
					$(ratingId).prev().addClass('no_star');
				}

				$(ratingId).prev().removeClass('selected');
			}
			$(this).prev().addClass('selected');
		});
	},

	bindStarsWrapperRadioButtonsFirstTimeFocus: function(radioButtons)
	{
		radioButtons.one('focus', function() {
			$(this).trigger('change');
		});
	},

	initialize: function()
	{
		this.bindStarsWrapperRadioButtons($('#stars-wrapper input'));
		this.bindStarsWrapperRadioButtonsFirstTimeFocus($('#stars-wrapper input'));
	}
}

$(document).ready(function() {
	ACC.stars.initialize();
});