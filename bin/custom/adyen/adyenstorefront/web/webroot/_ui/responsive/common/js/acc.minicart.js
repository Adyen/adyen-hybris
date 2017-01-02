ACC.minicart = {

	_autoload: [
		"bindMiniCart"
	],

	bindMiniCart: function(){

		$(document).on("click",".js-mini-cart-link", function(e){
			e.preventDefault();
			var url = $(this).data("miniCartUrl");
			var cartName = ($(this).find(".js-mini-cart-count").html() != 0) ? $(this).data("miniCartName"):$(this).data("miniCartEmptyName");
				
			ACC.colorbox.open(cartName,{
				href: url,
				maxWidth:"100%",
				width:"320px",
				initialWidth :"320px"
			});
		})

		$(document).on("click",".js-mini-cart-close-button", function(e){
			e.preventDefault();
			ACC.colorbox.close();
		})
	},

	updateMiniCartDisplay: function(){
		var miniCartRefreshUrl = $(".js-mini-cart-link").data("miniCartRefreshUrl");
		$.get(miniCartRefreshUrl,function(data){
			var data = $.parseJSON(data);
			$(".js-mini-cart-link .js-mini-cart-count").html(data.miniCartCount)
			$(".js-mini-cart-link .js-mini-cart-price").html(data.miniCartPrice)
		})
	}

};