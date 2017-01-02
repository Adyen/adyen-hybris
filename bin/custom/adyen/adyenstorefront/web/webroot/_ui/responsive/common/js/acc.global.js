ACC.global = {

	_autoload: [
		["passwordStrength", $('.password-strength').length > 0],
		"bindToggleOffcanvas",
		"bindToggleXsSearch",
		"bindToggleHeaderLinks",
		"bindHoverIntentMainNavigation",
		"initImager"
	],

	passwordStrength: function(){
		$('.password-strength').pstrength();
	},

	bindToggleOffcanvas: function(){
		$(document).on("click",".js-toggle-sm-navigation",function(){
			ACC.global.toggleClassState($("main"),"offcanvas");
		});
	},

	bindToggleXsSearch: function(){
		$(document).on("click",".js-toggle-xs-search",function(){
			ACC.global.toggleClassState($(".site-search"),"active");
		});
	},

	bindToggleHeaderLinks: function(){
		$(document).on("click",".js-toggle-header-links",function(){
			var $e = $(".md-secondary-navigation");
			$(this).blur();
			ACC.global.toggleClassState($(this),"active");
			ACC.global.toggleClassState($e,"active")? $e.slideDown(300): $e.slideUp(300);
		})
	},

	toggleClassState: function($e,c){
		$e.hasClass(c)? $e.removeClass(c): $e.addClass(c);
		return $e.hasClass(c);
	},

	bindHoverIntentMainNavigation: function(){
		$("nav.main-navigation > ul > li").hoverIntent(function(){
			$(this).addClass("md-show-sub")
		},function(){
			$(this).removeClass("md-show-sub")
		})
	},

	initImager: function(){
		new Imager('.js-responsive-image');
	},

	// usage: ACC.global.addGoogleMapsApi("callback function"); // callback function name like "ACC.global.myfunction"
 	addGoogleMapsApi: function(callback){
		if(callback != undefined && $(".js-googleMapsApi").length == 0  ){
			$('head').append('<script class="js-googleMapsApi" type="text/javascript" src="//maps.googleapis.com/maps/api/js?key='+ACC.config.googleApiKey+'&sensor=false&callback='+callback+'"></script>');
		}else if(callback != undefined){

			eval(callback+"()");
		}
	}

};
