ACC.cartremoveitem = {

	bindAll: function ()
	{
		this.bindCartRemoveProduct();
	},
	bindCartRemoveProduct: function ()
	{

		$('.submitRemoveProduct').on("click", function ()
		{
			var prodid = $(this).attr('id').split("_")
			var form = $('#updateCartForm' + prodid[1]);
			var productCode = form.find('input[name=productCode]').val(); 
			var initialCartQuantity = form.find('input[name=initialQuantity]');
			var cartQuantity = form.find('input[name=quantity]');

			ACC.track.trackRemoveFromCart(productCode, initialCartQuantity.val());
			
			cartQuantity.val(0);
			initialCartQuantity.val(0);
			form.submit();
		});

		$('.updateQuantityProduct').on("click", function ()
		{
			var prodid = $(this).attr('id').split("_")
			var form = $('#updateCartForm' + prodid[1]);
			var productCode = form.find('input[name=productCode]').val(); 
			var initialCartQuantity = form.find('input[name=initialQuantity]').val();
			var newCartQuantity = form.find('input[name=quantity]').val();
			
			if(initialCartQuantity != newCartQuantity)
			{
				ACC.track.trackUpdateCart(productCode, initialCartQuantity, newCartQuantity);
				form.submit();
			}

		});
	}
}

$(document).ready(function ()
{
	ACC.cartremoveitem.bindAll();
});
