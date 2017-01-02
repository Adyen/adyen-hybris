ACC.termsandconditions = {

	bindTermsAndConditionsLink: function() {
		$('.termsAndConditionsLink').click(function(e) {
			e.preventDefault();
			$.colorbox({
				href: $(this).attr("href"),
				onComplete: function() {
					ACC.common.refreshScreenReaderBuffer();
				},
				onClosed: function() {
					ACC.common.refreshScreenReaderBuffer();
				}
			});
		});
	}
}

$(document).ready(function(){
	with(ACC.termsandconditions) {
		bindTermsAndConditionsLink();
	}
});
