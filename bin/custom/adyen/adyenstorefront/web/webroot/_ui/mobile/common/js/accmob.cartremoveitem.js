ACCMOB.cartremoveitem = {

	bindAll: function ()
	{
		this.bindCartRemoveProduct();
	},
	bindCartRemoveProduct: function ()
	{
		$('.submitRemoveProduct').click(function ()
		{
			var form = $('#updateCartForm' + $(this).attr('id'));
			var productCode = form.get(0).productCode.value;
			var initialCartQuantity = $('#quantity' + $(this).attr('id')).val();
			var cartData = form.data("cart");
			ACCMOB.cartremoveitem.trackRemoveFromCart(productCode, initialCartQuantity,cartData);

			$('#quantity' + $(this).attr('id')).attr('value', '0');
			$('#updateCartForm' + $(this).attr('id')).submit();
		});
	},

	trackRemoveFromCart: function(productCode, initialCartQuantity, data)
	{
		window.mediator.publish('trackRemoveFromCart',{
			productCode: productCode,
			initialCartQuantity: initialCartQuantity,
			cartData:data
		});
	}
};

$(document).ready(function ()
{
	ACCMOB.cartremoveitem.bindAll();
});
