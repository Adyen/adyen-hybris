ACCMOB.cartremoveitem = {

	bindAll: function ()
	{
		this.bindCartRemoveProduct();
	},
	bindCartRemoveProduct: function ()
	{
		$('.submitRemoveProduct').click(function ()
		{
			var productCode = $('#updateCartForm' + $(this).attr('id')).get(0).productCode.value;
			var initialCartQuantity = $('#quantity' + $(this).attr('id')).val();

			ACCMOB.cartremoveitem.trackRemoveFromCart(productCode, initialCartQuantity);

			$('#quantity' + $(this).attr('id')).attr('value', '0');
			$('#updateCartForm' + $(this).attr('id')).submit();
		});
	},

	trackRemoveFromCart: function(productCode, initialCartQuantity)
	{
		window.mediator.publish('trackRemoveFromCart',{
			productCode: productCode,
			initialCartQuantity: initialCartQuantity
		});
	}
};

$(document).ready(function ()
{
	ACCMOB.cartremoveitem.bindAll();
});
