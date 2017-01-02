ACCMOB.storefinder = {
	storeLatitude: null,
	storeLongitude: null,
	mapOptions: {},

	positionSuccessStoresNearMe: function (position)
	{
		$("#latitude").val(position.coords.latitude);
		$("#longitude").val(position.coords.longitude);
		$("#nearMeStorefinderForm").submit();

		return false;
	},

	setStoreCoordinates: function (latitude, longitude)
	{
		ACCMOB.storefinder.storeLatitude = latitude;
		ACCMOB.storefinder.storeLongitude = longitude;
	},

	positionSuccessNavigateTo: function (position)
	{
		window.location.href = "https://maps.google.com/maps?saddr=" +
				position.coords.latitude + "," +
				position.coords.longitude + "&daddr=" +
				ACCMOB.storefinder.storeLatitude + "," +
				ACCMOB.storefinder.storeLongitude;

		return false;
	},

	windowHeight: function ()
	{
		return window.innerHeight ? window.innerHeight : $(window).height();
	},

	bindToNavigateTo: function (button)
	{
		if (button.length > 0)
		{
			button.click(function (event)
			{
				ACCMOB.common.preventDefault(event);
				var gps = navigator.geolocation;
			
				if (gps)
				{
					gps.getCurrentPosition(ACCMOB.storefinder.positionSuccessNavigateTo, function (error)
					{
						console.log("An error occurred... The error code and message are: " + error.code + "/" + error.message);
					});
				}
			});
		}
	},

	bindToFindStoresNearMe: function (findStoresNearMe)
	{
		if (findStoresNearMe.length > 0)
		{
			findStoresNearMe.click(function (event)
			{
				ACCMOB.common.preventDefault(event);
				ACCMOB.storefinder.findStoresNearMe();
			});
		}
	},

	findStoresNearMe: function ()
	{
		var gps = navigator.geolocation;
		if (gps)
		{
			gps.getCurrentPosition(
					ACCMOB.storefinder.positionSuccessStoresNearMe,
					function (error)
					{
						console.log("An error occurred... The error code and message are: " + error.code + "/" + error.message);
					}
			);
		}
	},

	setStoreCoordinatesOfNavigateToButton: function (button)
	{
		if (button.length > 0)
		{
			ACCMOB.storefinder.setStoreCoordinates(button.data('latitude'), button.data('longitude'));
		}
	},

	//** DRAW MAPS ***************************************************************************************************//

	drawStoresMap: function ()
	{
		var markers = [];
		var mapContainer = $("#maps_canvas");

		if (mapContainer.length > 0)
		{
			mapContainer.height(Math.round(this.windowHeight() * 0.4));

			var center = new google.maps.LatLng(mapContainer.data('latitude'), mapContainer.data('longitude'));

			$.extend(ACCMOB.storefinder.mapOptions, {center: center});
			var map = new google.maps.Map(document.getElementById("maps_canvas"), ACCMOB.storefinder.mapOptions);

			// render stores from results list
			if (mapContainer.data('markers') !== undefined)
			{
				var markersData = $.parseJSON(new String(mapContainer.data('markers')).replace(/'/g, '"'));
			}
			// render a single store
			else
			{
				var content = '';
				$.each(['storename', 'line1', 'line2', 'town', 'postalCode', 'country'], function (index, key)
				{
					content += '<div>' + mapContainer.data(key) + '</div>';
				});

				var markersData = [
					{
						latitude: mapContainer.data('latitude'),
						longitude: mapContainer.data('longitude'),
						title: mapContainer.data('storename')
					}
				];
			}

			for (var i = 0; i < markersData.length; i++)
			{
				var coordinates = new google.maps.LatLng(markersData[i].latitude, markersData[i].longitude);
				if (coordinates !== center)
				{
					markers.push(ACCMOB.storefinder.createStoreMarker(coordinates, markersData[i].title, i, map, content));
				}
			}

			ACCMOB.storefinder.zoomMapToFitMarkersAndCurrentLocation(map, markers, center);
		}
	},

	createStoreMarker: function (coordinates, name, storeNumber, map, content)
	{
		content = typeof content !== 'undefined' ? content : name; // set content to name if not set

		var marker = new google.maps.Marker({
			position: coordinates,
			title: name,
			map: map,
			icon: "https://maps.google.com/mapfiles/marker" + String.fromCharCode(storeNumber + 65) + ".png"
		});

		var infowindow = new google.maps.InfoWindow({
			content: content,
			disableAutoPan: true
		});

		google.maps.event.addListener(marker, 'click', function ()
		{
			infowindow.open(map, marker)
		});

		return marker;
	},

	zoomMapToFitMarkersAndCurrentLocation: function (map, markers, currentLocation)
	{
		var bounds = new google.maps.LatLngBounds();
		bounds.extend(currentLocation);

		if (markers.length > 1)
		{
			for (var i = 0; i < markers.length; i++)
			{
				bounds.extend(markers[i].getPosition());
			}
		}
		else
		{
			var boundsOffset = 0.000001 * this.windowHeight();
			bounds.extend(markers[0].getPosition());
			// change bounds.ca to bounds.Z to fix the map 5/28/2013 
			bounds.Z.b -= boundsOffset;
			bounds.Z.d += boundsOffset;
		}

		map.fitBounds(bounds);
	},

	renderListsMapMarker: function (mapMarkerImages)
	{
		mapMarkerImages.each(function ()
		{
			var imageSrc = 'https://maps.google.com/mapfiles/marker' + String.fromCharCode($(this).data('index') + 64) + '.png';
			if ($(this).attr('src') != imageSrc)
			{ // don't get stuck in an endless loop
				$(this).attr('src', imageSrc);
			}
		});
	},

	initialize: function ()
	{
		with (ACCMOB.storefinder)
		{
			bindToNavigateTo($("#navigateTo"));
			bindToFindStoresNearMe($("#findStoresNearMe"));
			initializeMapOptions();
			drawStoresMap();
			setStoreCoordinatesOfNavigateToButton($("#navigateTo"));
			renderListsMapMarker($(".mapMarker"));
		}
	},

	initializeMapOptions: function ()
	{
		ACCMOB.storefinder.mapOptions = {
			zoom: 1,
			zoomControl: true,
			panControl: false,
			streetViewControl: false,
			disableDefaultUI: true
		};

		try
		{
			ACCMOB.storefinder.mapOptions.mapTypeId = google.maps.MapTypeId.ROADMAP;
		}
		catch (error)
		{
			ACCMOB.storefinder.mapOptions.mapTypeId = "roadmap";
			console.log(error);
		}
	}
};

$(document).ready(function ()
{
	ACCMOB.storefinder.initialize();
});
