ACC.deliverydescription = {

	bindAll: function ()
	{
		this.bindDeliveryModeDes();
	},
	bindDeliveryModeDes: function ()
	{

		var str = $(".deliverymode-description").text();
		if (str.length > 180)
		{
			str = str.substr(0, 180) + '&hellip;';
		}
		$(".deliverymode-description").html(str);

	}

};

$(document).ready(function ()
{
	ACC.deliverydescription.bindAll();
});
