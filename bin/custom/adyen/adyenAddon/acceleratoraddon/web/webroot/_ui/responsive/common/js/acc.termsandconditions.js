ACC.termsandconditions = {

	bindTermsAndConditionsLink: function() {
		$(document).on("click",".termsAndConditionsLink",function(e) {
			e.preventDefault();
			$.colorbox({
				maxWidth:"100%",
				maxHeight:"80%",
				width:"870px",
				scrolling:true,
				href: $(this).attr("href"),
				close:'<span class="glyphicon glyphicon-remove"></span>',
				title:'<div class="headline"><span class="headline-text">Terms and Conditions</span></div>',
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

$(function(){
	with(ACC.termsandconditions) {
		bindTermsAndConditionsLink();
	}
});
