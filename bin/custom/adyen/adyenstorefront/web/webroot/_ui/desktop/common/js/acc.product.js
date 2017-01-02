ACC.product = {
	
	initQuickviewLightbox:function(){
		this.enableAddToCartButton();
		this.bindToAddToCartForm();
	},
	
	
	enableAddToCartButton: function ()
	{
		$('#addToCartButton').removeAttr("disabled");
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
		var addToCartStorePickUpForm = $('#pickup_store_results .add_to_cart_storepickup_form');

		addToCartStorePickUpForm.ajaxForm({success: ACC.product.displayAddToCartPopup});
	},
	
	
	enableStorePickupButton: function ()
	{
		$('.pickupInStoreButton').removeAttr("disabled");
	},


	displayAddToCartPopup: function (cartResult, statusText, xhr, formElement)
	{
		$('#addToCartLayer').remove();
		
		if (typeof ACC.minicart.refreshMiniCartCount == 'function')
		{
			ACC.minicart.refreshMiniCartCount();
		}
		
		$("#header").append(cartResult.addToCartLayer);
		

		$('#addToCartLayer').fadeIn(function(){
			$.colorbox.close();
			if (typeof timeoutId != 'undefined')
			{
				clearTimeout(timeoutId);
			}
			timeoutId = setTimeout(function ()
			{
				$('#addToCartLayer').fadeOut(function(){
			 	   $('#addToCartLayer').remove();
					
				});
			}, 5000);
			
		});
		
		var productCode = $('[name=productCodePost]', formElement).val();
		var quantityField = $('[name=qty]', formElement).val();
		
		var quantity = 1;
		if (quantityField != undefined)
		{
			quantity = quantityField;
		}

		var cartAnalyticsData = cartResult.cartAnalyticsData;

		var cartData = {"cartCode": cartAnalyticsData.cartCode,
				"productCode": productCode, "quantity": quantity,
				"productPrice":cartAnalyticsData.productPostPrice,
				"productName":cartAnalyticsData.productName} ;

		ACC.track.trackAddToCart(productCode, quantity, cartData);
	}

};

$(document).ready(function ()
{
	with(ACC.product)
	{
		bindToAddToCartForm();
		bindToAddToCartStorePickUpForm();
		enableStorePickupButton();
		enableAddToCartButton();
		enableVariantSelectors();
	}
});

