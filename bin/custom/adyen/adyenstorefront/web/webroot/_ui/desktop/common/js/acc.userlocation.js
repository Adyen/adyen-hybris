ACC.userlocation = {

	bindAll: function ()
	{
		ACC.userlocation.bindUserLocationSearchButtonClick();
		ACC.userlocation.bindUserLocationEnterPress();
		ACC.userlocation.bindAutoLocationSearchButtonClick();
	},

	bindUserLocationEnterPress: function ()
	{
		$('#user_location_query').keypress(function (e)
		{
			var code = null;
			code = (e.keyCode ? e.keyCode : e.which);
			if (code == 13)
			{
				$.ajax({
					url: searchUserLocationUrl,
					type: 'GET',
					data: {q: $('#user_location_query').attr("value")},
					success: function (data)
					{
						location.reload();
					}
				});
				return false;
			}
			;
		});
	},

	bindUserLocationSearchButtonClick: function ()
	{
		$('#user_location_query_button').click(function (e)
		{
			$.ajax({
				url: searchUserLocationUrl,
				type: 'GET',
				data: {q: $('#user_location_query').attr("value")},
				success: function (data)
				{
					location.reload();
				}
			});
			return false;
		});
	},

	bindAutoLocationSearchButtonClick: function ()
	{
		$(document).on("click", "#findStoresNearMeAjax", function (e)
		{
			e.preventDefault();
			try
			{
				var gps = navigator.geolocation;
				gps.getCurrentPosition(ACC.userlocation.positionSuccessStoresNearMe, function (error)
				{
					console.log("An error occurred... The error code and message are: " + error.code + "/" + error.message);
				});
			}
			catch (error)
			{
				console.log("An error occurred... ");
			}
		});
	},

	positionSuccessStoresNearMe: function (position)
	{
		if (typeof autoUserLocationUrl !== 'undefined')
		{
			$.ajax({
				url: autoUserLocationUrl,
				type: 'POST',
				data: {latitude: position.coords.latitude, longitude: position.coords.longitude},
				success: function (data)
				{
					location.reload();
				}
			});
		}

		return false;
	}

};

$(document).ready(function ()
{
	ACC.userlocation.bindAll();
});
