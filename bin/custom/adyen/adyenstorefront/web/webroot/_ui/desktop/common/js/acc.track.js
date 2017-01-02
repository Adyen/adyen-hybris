ACC.track = {
	trackAddToCart: function (productCode, quantity, cartData)
	{
		window.mediator.publish('trackAddToCart',{
			productCode: productCode,
			quantity: quantity,
			cartData: cartData
		});
	},
	trackRemoveFromCart: function(productCode, initialCartQuantity,cartData)
	{
		window.mediator.publish('trackRemoveFromCart',{
			productCode: productCode,
			initialCartQuantity: initialCartQuantity,
			cartData: cartData
		});
	},

	trackUpdateCart: function(productCode, initialCartQuantity, newCartQuantity,cartData)
	{
		window.mediator.publish('trackUpdateCart',{
			productCode: productCode,
			initialCartQuantity: initialCartQuantity,
			newCartQuantity: newCartQuantity,
			cartData: cartData
		});
	}
	

};

