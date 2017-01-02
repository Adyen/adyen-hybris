ACCMOB.pickupinstore = {
	currentPopupForm: 'undefined',
	pickUpInStoreResultsList: [],

	bindPickupInStoreButton: function (button)
	{
		$(document.body).on('click', button, function (e)
		{
			e.preventDefault();

			ACCMOB.common.showPageLoadingMsg();
			var productCode = $(this).data('productcode');
			var popupFormId = '#popup_store_pickup_form_product_' + productCode;

			// Update quantity
			$(popupFormId).find('#qty').val($(this).parents('form').find('#qty').val());

			// If cross-sell pop up is currently opened, close it before opening dialog box
			
			$('.ui-easydialog-close').trigger('click');

			$.mobile.changePage($(popupFormId), {changeHash: false});
			$(popupFormId).find('#qty').selectmenu('refresh');
		});
	},

	bindChangeStoreLinks: function (link)
	{
		link.click(function (e)
		{
			e.preventDefault();
			var productCode = $(this).data('productcode');
			var entryNumber = $(this).data('entrynumber');
			var popupFormId = '#popup_store_pickup_form_product_' + productCode + "_entry_" + entryNumber;

			$.mobile.changePage($(popupFormId), {changeHash: false});
		});
	},

	bindClosePickupInStorePopupLinks: function (link)
	{
		link.click(function (e)
		{
			$.mobile.changePage($('#body'), {changeHash: false});
		});
	},

	bindPickupPopupForm: function (form)
	{
		$(document.body).on('pageshow', form, function ()
		{
			ACCMOB.pickupinstore.currentPopupForm = $(this);
			ACCMOB.pickupinstore.rememberLocationSearch($(this).find('#locationQueryStorefinderForm'));
		});
		$(form).one('pageshow', function ()
		{
			ACCMOB.pickupinstore.bindPickUpInStoreResultsList($(this).find('#pickUpInStoreResultsList'));
		});
	},

	bindToPickUpAtThisStoreButton: function (button)
	{
		$(document.body).on('click', button, function ()
		{
			$(this).parents('form').find('.hiddenPickupQty').val($(this).parents('div[data-role=content]').find('select#qty').val());
			$.mobile.changePage($('#body'), {changeHash: false});
		});
	},

	bindAddToCartForPickUpInStore: function ()
	{
		var addToCartForm = $('.add_to_cart_storepickup_form');
		addToCartForm.ajaxForm({success: ACCMOB.product.displayAddToCartOverlay});
	},

	bindFindByLocationQuery: function (button, searchQuery)
	{
		button.click(function (event)
		{
			event.preventDefault();
			ACCMOB.pickupinstore.loadPointsOfService($(this).parents("form"));
		});

		searchQuery.keypress(function (event)
		{
			if (event.keyCode === 13)
			{
				event.preventDefault();
				ACCMOB.pickupinstore.loadPointsOfService($(this).parents("form"));
			}
		});
	},

	bindFindByLocationPositionClick: function (button)
	{
		button.click(function (event)
		{
			event.preventDefault();

			var form = $(this).parent("form");

			if (navigator.geolocation)
			{
				navigator.geolocation.getCurrentPosition(
						function (position)
						{
							$(form).find("#latitude").val(position.coords.latitude);
							$(form).find("#longitude").val(position.coords.longitude);

							ACCMOB.pickupinstore.loadPointsOfService(form);
						},
						ACCMOB.pickupinstore.handlePickUpInStoreError
				);
			}
		});
	},

	bindChangeLocationClick: function (link)
	{
		$(document.body).on('click', link, function (event)
		{
			event.preventDefault();

			ACCMOB.common.showPageLoadingMsg();
			ACCMOB.pickupinstore.pickUpInStoreResultsList.empty();
			ACCMOB.pickupinstore.togglePageMode();
			ACCMOB.common.hidePageLoadingMsg();
		});
	},

	bindPickUpInStoreResultsList: function (resultsList)
	{
		ACCMOB.pickupinstore.pickUpInStoreResultsList = resultsList;

		$(document.body).on('change', resultsList, function ()
		{
			var currentPage = parseInt($(this).find('input#next_page_value').val());

			$(this).find('input#next_page_value').val(currentPage + 1);
			$(this).find("button").button();
		});
	},

	bindChangeToShippingTypeSelection: function (button)
	{
		button.change(function ()
		{
			if (ACCMOB.pickupinstore.checkIfPointOfServiceIsEmpty($(this)))
			{
				ACCMOB.common.showPageLoadingMsg();
				var updateShippingModeForm = $(this).parent().parent('form').attr('id');
				$('#' + updateShippingModeForm).submit();
			}
			else
			{
				$(this).parent('div').parent('form').find('.cart-changeStore').hide();
				$(this).parents('fieldset').removeAttr('style', 'border:3px solid red');
			}
		});
	},

	bindPickupRadioButtonSelected: function (button)
	{
		button.change(function ()
		{
			$(this).parent('div').parent('span').find('.cart-changeStore').show();
		});
	},

	bindToCheckoutButton: function (button)
	{
		button.click(function (e)
		{
			var error = false;

			$('.cartForm input[type=radio].showStoreFinderLink').each(function (i)
			{
				if ($(this).is(':checked') && !ACCMOB.pickupinstore.checkIfPointOfServiceIsEmpty($(this)))
				{
					$(this).parents('fieldset').attr('style', 'border:3px solid red');
					error = true;
				}
			});

			if (error)
			{
				$(window).scrollTop(0);

				var accErrorMsgs = $("#noStoreSelected");
				
				$.mobile.easydialog({
					content: accErrorMsgs.html(),
					header: accErrorMsgs.data('headertext'),
					type: 'error'
				});
				
			}

			return !error;
		});

	},

	loadPointsOfService: function (form)
	{
		var request = {
			url: form.attr('action'),
			type: "POST",
			data: form.serialize()
		}

		ACCMOB.pickupinstore.pickUpInStoreResultsList.empty();
		ACCMOB.pickupinstore.updatePickUpInStoreResultsList(request, ACCMOB.pickupinstore.populatePickUpInStoreResultsList);

		$.extend(ACCMOB.storelisting.scrollingConfig, {onlyOnScroll: false});
		ACCMOB.storelisting.bindShowMoreStores($('.backFooterButton'), ACCMOB.pickupinstore.showMoreStoresScroll);
		ACCMOB.pickupinstore.togglePageMode();
	},

	updatePickUpInStoreResultsList: function (request, successHandler, errorHandler)
	{
		ACCMOB.common.showPageLoadingMsg();

		$.extend(request, {
			async: false,
			success: function (data)
			{
				successHandler(data);
				ACCMOB.pickupinstore.bindAddToCartForPickUpInStore();
				ACCMOB.common.hidePageLoadingMsg();
			},
			error: errorHandler || ACCMOB.pickupinstore.handlePickUpInStoreError
		});
		$.ajax(request);
	},

	populatePickUpInStoreResultsList: function (data)
	{
		ACCMOB.pickupinstore.pickUpInStoreResultsList
				.append(data)
				.show()
				.find("ul")
				.trigger("change");
	},

	appendToPickUpInStoreResultsList: function (data)
	{
		var nextResults = $($(data)[2]).find("li");

		if (nextResults.size() !== 0)
		{
			ACCMOB.pickupinstore.pickUpInStoreResultsList
					.find("ul")
					.append($($(data)[2]).find("li"))
					.trigger("change");
		}
		else
		{
			ACCMOB.storelisting.showMoreStoresArea.waypoint('destroy');
		}
	},

	handlePickUpInStoreError: function (error)
	{
		console.log("An error occurred. \nThe error code and message are: \n" + error.code + "\n" + error.message);
	},

	togglePageMode: function ()
	{
		ACCMOB.pickupinstore.currentPopupForm.find('#pickUpInStoreSearchForms').toggle();
	},

	locationSearchSubmit: function (cartPage, entryNumber, formUrl, rememberMe)
	{
		ACCMOB.common.showPageLoadingMsg();
		$.ajax({
			url: formUrl,
			data: {
				cartPage: cartPage,
				entryNumber: entryNumber
			},
			type: 'GET',
			success: function (response)
			{
				ACCMOB.pickupinstore.pickUpInStoreResultsList.empty();
				ACCMOB.pickupinstore.populatePickUpInStoreResultsList(response);
				ACCMOB.pickupinstore.bindAddToCartForPickUpInStore();

				$.extend(ACCMOB.storelisting.scrollingConfig, {onlyOnScroll: false});
				ACCMOB.storelisting.bindShowMoreStores($('.backFooterButton'), ACCMOB.pickupinstore.showMoreStoresScroll);

				ACCMOB.pickupinstore.currentPopupForm.find('#pickUpInStoreSearchForms').show();

				if (rememberMe && !ACCMOB.pickupinstore.currentPopupForm.find('.pickup_store_results-list').data('resultsfound'))
				{
					ACCMOB.pickupinstore.currentPopupForm.find('#pickUpInStoreResultsList').hide();
				}
				else
				{
					// Did not use togglePageMode here since reopening the popup would toggle the pickUpInStoreSearchForms
					ACCMOB.pickupinstore.currentPopupForm.find('#pickUpInStoreSearchForms').hide();
				}

				ACCMOB.common.hidePageLoadingMsg();
			}
		});
	},

	showMoreStoresScroll: function ()
	{
		var form = ACCMOB.pickupinstore.currentPopupForm.find('#next_results_storepickup_form');

		var request = {
			url: form.attr("action"),
			type: "GET",
			data: form.serialize()
		}

		ACCMOB.pickupinstore.updatePickUpInStoreResultsList(request, ACCMOB.pickupinstore.appendToPickUpInStoreResultsList);
	},

	checkIfPointOfServiceIsEmpty: function (radioButton)
	{
		return radioButton.parent('div').parent().find('.basket-page-shipping-pickup').text().trim().length > 1;
	},

	rememberLocationSearch: function (locationQueryForm)
	{
		var lineItem = locationQueryForm.find('input[name=entryNumber]').val();
		ACCMOB.pickupinstore.locationSearchSubmit(locationQueryForm.find('input[name=cartPage]').val(), lineItem, locationQueryForm.attr('action'), true);
	},

	initialize: function ()
	{
		with (ACCMOB.pickupinstore)
		{
			bindFindByLocationQuery($('button.storeSearchButton'), $('input.storeSearchBox'));
			bindFindByLocationPositionClick($('a.findStoresNearMeButton'));
			bindChangeLocationClick('#changeLocation');
			bindChangeToShippingTypeSelection($('.updateToShippingSelection'));
			bindPickupRadioButtonSelected($('.showStoreFinderLink'));
			bindToCheckoutButton($('#checkoutButton'));
			bindToPickUpAtThisStoreButton('.pickup_here_instore_button');
			bindPickupPopupForm('.pickupPopup');
			bindPickupInStoreButton('.pickUpInStoreButton');
			bindClosePickupInStorePopupLinks($('div.accmobBackLink.accmobBackLinkSingle'));
			bindChangeStoreLinks($('.cart-changeStore > a'));
		}
	}
};

$(document).ready(function ()
{
	ACCMOB.pickupinstore.initialize();
});
