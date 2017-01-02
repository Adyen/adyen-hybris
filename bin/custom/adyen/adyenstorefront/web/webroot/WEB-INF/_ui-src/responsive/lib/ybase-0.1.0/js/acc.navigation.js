ACC.navigation = {

	_autoload: [
		"offcanvasNavigation"
	],

	offcanvasNavigation: function(){

		enquire.register("screen and (max-width:"+screenSmMax+")", {

			match : function() {

				$(document).on("click",".js-enquire-offcanvas-navigation .js-enquire-has-sub > a",function(e){
					e.preventDefault();
					$(".js-enquire-offcanvas-navigation > ul").addClass("active");
					$(".js-enquire-offcanvas-navigation .js-enquire-has-sub").removeClass("active");
					$(this).parent(".js-enquire-has-sub").addClass("active");
				})


				$(document).on("click",".js-enquire-offcanvas-navigation .js-enquire-sub-close",function(e){
					e.preventDefault();
					$(".js-enquire-offcanvas-navigation > ul").removeClass("active");
					$(".js-enquire-offcanvas-navigation .js-enquire-has-sub").removeClass("active");
				})



			},      
			                            
			unmatch : function() {

				$(".js-enquire-offcanvas-navigation > ul").removeClass("active");
				$(".js-enquire-offcanvas-navigation .js-enquire-has-sub").removeClass("active");

				$(document).off("click",".js-enquire-offcanvas-navigation .js-enquire-has-sub > a");
				$(document).off("click",".js-enquire-offcanvas-navigation .js-enquire-sub-close");


			}  
		
		  
		});

	}




};