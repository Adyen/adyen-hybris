ACC.forgottenpassword = {

	_autoload: [
		"bindLink"
	],

	bindLink: function(){
		$(document).on("click",".js-password-forgotten",function(e){
			e.preventDefault();

			ACC.colorbox.open(
				$(this).data("cboxTitle"),
				{
					href: $(this).attr("href"),
					width:"350px",
					onComplete: function(){
						ACC.forgottenpassword.bindforgottenPwdForm();
					}
				}
			);
		});
	},

	bindforgottenPwdForm: function(){
		$('form#forgottenPwdForm').ajaxForm({
		    target: '.forgotten-password',
		    success: function() { 
				ACC.colorbox.resize();
				ACC.forgottenpassword.bindforgottenPwdForm();
			}
		});
	}
};