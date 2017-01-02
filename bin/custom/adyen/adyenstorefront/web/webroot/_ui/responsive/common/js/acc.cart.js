ACC.cart = {

	_autoload: [
		"bindHelp"
	],

	bindHelp: function(){
		$(document).on("click",".js-cart-help",function(e){
			e.preventDefault();
			var title = $(this).data("help");
			ACC.colorbox.open(title,{
				html:$(".js-help-popup-content").html(),
				width:"300px"
			});
		})
	}

};