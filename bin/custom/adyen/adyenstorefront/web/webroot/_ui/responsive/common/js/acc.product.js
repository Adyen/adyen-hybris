ACC.product = {

	_autoload: [
		"bindToAddToCartForm",
		"enableStorePickupButton",
		"enableAddToCartButton",
		"enableVariantSelectors",
		"bindFacets"
	],


	bindFacets:function(){
		$(document).on("click",".js-show-facets",function(e){
			e.preventDefault();

			ACC.colorbox.open("Select Refinements",{
				href: "#product-facet",
				inline: true,
				width:"320px",
				onComplete: function(){

					$(document).on("click",".js-product-facet .js-facet-name",function(e){
						e.preventDefault();
						$(".js-product-facet  .js-facet").removeClass("active");
						$(this).parents(".js-facet").addClass("active");
						$.colorbox.resize()
					})
				},
				onClosed: function(){
					$(document).off("click",".js-product-facet .js-facet-name");
				}
			});
		});



		enquire.register("screen and (min-width:"+screenSmMax+")",  function() {
			$("#cboxClose").click();
		});


	},



	enableAddToCartButton: function ()
	{
		$('.js-add-to-cart').removeAttr("disabled");
	},
	
	enableVariantSelectors: function ()
	{
		$('.variant-select').removeAttr("disabled");
	},
	
	bindToAddToCartForm: function ()
	{
		var addToCartForm = $('.add_to_cart_form');
		addToCartForm.ajaxForm({success: ACC.product.displayAddToCartPopup});
	},

	bindToAddToCartStorePickUpForm: function ()
	{
		var addToCartStorePickUpForm = $('#colorbox #add_to_cart_storepickup_form');
		addToCartStorePickUpForm.ajaxForm({success: ACC.product.displayAddToCartPopup});
	},


	enableStorePickupButton: function ()
	{
		$('.js-pickup-in-store-button').removeAttr("disabled");
	},

	displayAddToCartPopup: function (cartResult, statusText, xhr, formElement)
	{
		$('#addToCartLayer').remove();

		if (typeof ACC.minicart.updateMiniCartDisplay == 'function')
		{
			ACC.minicart.updateMiniCartDisplay();
		}
		var titleHeader = $('#addToCartTitle').html();


		ACC.colorbox.open(titleHeader,{
			html: cartResult.addToCartLayer,
			width:"320px",
		});

		var productCode = $('[name=productCodePost]', formElement).val();
		var quantityField = $('[name=qty]', formElement).val();

		var quantity = 1;
		if (quantityField != undefined)
		{
			quantity = quantityField;
		}

		ACC.track.trackAddToCart(productCode, quantity, cartResult.cartData);

	}

};