ACCMOB.userlocation = {

	bindRefinementsStoresPage: function (page)
	{
		$(document.body).on('pageshow', page, function ()
		{
			ACCMOB.common.showPageLoadingMsg();

			// There is a timing issue when jQuery shows stores first before loading msg
			// The setTimeout was added here to force loading msg to always show first
			setTimeout(function ()
			{
				ACCMOB.storelisting.bindShowMoreStores($("#Stores-page div.ui-li-has-count-checkbox > fieldset input").last(),
						ACCMOB.userlocation.showMoreStoresScroll);
				$(document.body).off('pageshow', page);
				ACCMOB.common.hidePageLoadingMsg();
			}, 1)
		});
	},

	bindStoreLocationSearch: function (button, searchQuery)
	{
		$(document.body).on('click', button, function (e)
		{
			ACCMOB.userlocation.userSearchSubmit();
			return false;
		});

		$(document.body).on('keypress', searchQuery, function (e)
		{
			var code = (e.keyCode ? e.keyCode : e.which);
			if (code === 13)
			{
				ACCMOB.userlocation.userSearchSubmit();
				return false;
			}
		});
	},

	bindFindStoresNearMeButton: function (button)
	{
		$(document.body).on('click', button, function (e)
		{
			e.preventDefault();
			var gps = navigator.geolocation;
			if (gps)
			{
				gps.getCurrentPosition(ACCMOB.userlocation.positionSuccessStoresNearMe, function (error)
				{
					console.log("An error occurred... The error code and message are: " + error.code + "/" + error.message);
				});
			}
		});
	},

	bindChangeLocationLink: function (initialLimit)
	{
		$(document.body).on('click', '#changeLocationLink', function (e)
		{
			userLocation = "";
			showStoreLimit = initialLimit;

			// Remember which stores were selected after clicking change location
			ACCMOB.facets.updateRefinementsList();

			ACCMOB.userlocation.refreshStoreFacetPage();
			ACCMOB.common.hidePageLoadingMsg();
		});
	},

	showMoreStoresScroll: function ()
	{
		showStoreLimit += 3;

		// Remember which stores were selected after clicking show more stores
		ACCMOB.facets.updateRefinementsList();

		var beforeLastStoreListed = $("#Stores-page div.ui-li-has-count-checkbox > fieldset input").last();
		beforeLastStoreListed.waypoint('destroy');

		ACCMOB.userlocation.refreshStoreFacetPage();

		var currentLastStoreListed = $("#Stores-page div.ui-li-has-count-checkbox > fieldset input").last();

		// Generate a new waypoint if last store listed is not the same as the current last store listed
		if (beforeLastStoreListed.data('query') !== currentLastStoreListed.data('query'))
		{
			ACCMOB.storelisting.bindShowMoreStores(currentLastStoreListed,
					ACCMOB.userlocation.showMoreStoresScroll);
		}
	},
	// end BINDINGS -----------------------------------------------------------------

	// begin STORE_SEARCH ---------------------------------------------------------------
	positionSuccessStoresNearMe: function (position)
	{
		ACCMOB.common.showPageLoadingMsg();
		$("#latitude").val(position.coords.latitude);
		$("#longitude").val(position.coords.longitude);
		$.ajax({
			url: autoUserLocationUrl,
			type: 'POST',
			data: {latitude: position.coords.latitude, longitude: position.coords.longitude},
			success: function (data)
			{
				userLocation = true;
				ACCMOB.facets.getFacetData();
				ACCMOB.userlocation.refreshStoreFacetPage();

				$.extend(ACCMOB.storelisting.scrollingConfig, {onlyOnScroll: false});
				ACCMOB.storelisting.bindShowMoreStores($("#Stores-page div.ui-li-has-count-checkbox > fieldset input").last(),
						ACCMOB.userlocation.showMoreStoresScroll);
			}
		});

		return false;
	},

	userSearchSubmit: function ()
	{
		ACCMOB.common.showPageLoadingMsg();
		$.ajax({
			url: searchUserLocationUrl,
			type: 'GET',
			data: {q: $('#storelocator-query').val()},
			success: function (data)
			{
				userLocation = $('#storelocator-query').val().trim();
				ACCMOB.facets.getFacetData();
				ACCMOB.userlocation.refreshStoreFacetPage();

				$.extend(ACCMOB.storelisting.scrollingConfig, {onlyOnScroll: false});
				ACCMOB.storelisting.bindShowMoreStores($("#Stores-page div.ui-li-has-count-checkbox > fieldset input").last(),
						ACCMOB.userlocation.showMoreStoresScroll);
			}
		});
	},
	// end STORE_SEARCH ---------------------------------------------------------------

	// begin UPDATE ---------------------------------------------------------------
	refreshStoreFacetPage: function ()
	{
		$.each(ACCMOB.facets.cachedJSONResponse.facets, function (i, facetData)
		{
			if (facetData.code === "availableInStores")
			{
				var storeContent = $("#" + facetData.name + "-page > div.accmob-storeSearch.accmob-storeSearch-filter");

				var refinementContent = $("#refinementFacetContentTemplate");

				storeContent.replaceWith($.tmpl(refinementContent, facetData));
				ACCMOB.facets.updateFormElements();

				return;
			}
		});
	},
	// end UPDATE ---------------------------------------------------------------

	initialize: function ()
	{
		with (ACCMOB.userlocation)
		{
			bindRefinementsStoresPage('#Stores-page');
			bindStoreLocationSearch('#user_location_query_button', '#storelocator-query');
			bindFindStoresNearMeButton('#findStoresNearMeButton');
			bindChangeLocationLink(5);
		}
	}
};

$(document).ready(function ()
{
	ACCMOB.userlocation.initialize();
	// Remember nearby store search
	if (ACCMOB.userLocation === "" && longitude !== "" && latitude !== "")
	{
		ACCMOB.userLocation = true;
	}
});
