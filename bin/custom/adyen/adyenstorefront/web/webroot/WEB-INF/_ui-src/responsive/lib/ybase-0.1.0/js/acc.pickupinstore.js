ACC.pickupinstore = {

	_autoload: [
		"bindClickPickupInStoreButton",
		"bindPickupButton",
		"bindPickupClose",
		"bindPickupInStoreSearch"
	],

	storeId:"",

	unbindPickupPaginationResults:function ()
	{
		$(document).off("click","#colorbox .js-pickup-store-pager-prev")
		$(document).off("click","#colorbox .js-pickup-store-pager-next")
	},

	bindPickupPaginationResults:function ()
	{
		var listHeight=  $("#colorbox .js-pickup-store-list").height();
		var $listitems= $("#colorbox .js-pickup-store-list > li");
		var listItemHeight = $listitems.height();
		var displayCount = 5;
		var totalCount= $listitems.length;
		var curPos=0
		var pageEndPos = (((totalCount/displayCount)-1) * (displayCount*listItemHeight)) * -1;


		$("#colorbox .js-pickup-store-pager-item-all").html(totalCount);

		$("#colorbox .store-navigation-pager").show();



		checkPosition()

		$(document).on("click","#colorbox .js-pickup-store-pager-prev",function(e){
			e.preventDefault();
			$listitems.css("transform","translateY("+(curPos+listHeight)+"px)")
			curPos = curPos+listHeight;
			checkPosition("prev");
		})

		$(document).on("click","#colorbox .js-pickup-store-pager-next",function(e){
			e.preventDefault();
			$listitems.css("transform","translateY("+(curPos-listHeight)+"px)")
			curPos = curPos-listHeight;
			checkPosition("next");
		})

		function checkPosition(){

			var curPage = Math.ceil((curPos/(displayCount*listItemHeight))*-1)+1;
			$("#colorbox .js-pickup-store-pager-item-from").html(curPage*displayCount-4);

			var tocount = (curPage*displayCount > totalCount)? totalCount :curPage*displayCount;

			if(curPage*displayCount-4 == 1){
				$("#colorbox .js-pickup-store-pager-prev").hide()
			}else{
				$("#colorbox .js-pickup-store-pager-prev").show()
			}

			if(curPage*displayCount > totalCount){
				$("#colorbox .js-pickup-store-pager-next").hide()
			}else{
				$("#colorbox .js-pickup-store-pager-next").show()
			}


			$("#colorbox .js-pickup-store-pager-item-to").html(tocount);
		}
	},



	bindPickupInStoreQuantity:function(){
		$('.pdpPickupQtyPlus').click(function(e){
			e.preventDefault();

			var inputQty = $('.js-add-pickup-cart #pdpPickupAddtoCartInput');
			var currentVal = parseInt(inputQty.val());
			var maxVal = inputQty.data('max');

			if (!isNaN(currentVal) && currentVal < maxVal) {
				inputQty.val(currentVal + 1);
				inputQty.change();
			}
		});

		$('.pdpPickupQtyMinus').click(function(e){
			e.preventDefault();
			var inputQty = $('.js-add-pickup-cart #pdpPickupAddtoCartInput');
			var currentVal = parseInt(inputQty.val());
			var minVal = inputQty.data('min');

			if (!isNaN(currentVal) && currentVal > minVal) {
				inputQty.val(currentVal - 1);
				inputQty.change();
			}
		});

		$("body").on("keyup", ".js-add-pickup-cart #pdpPickupAddtoCartInput", function(event) {
			var input = $(event.target);
			input.val(this.value.match(/[0-9]*/));
			var value = input.val();
		});
	},

	bindPickupInStoreSearch: function ()
	{
		$(document).on('click', '#pickupstore_location_search_button', function (e)
		{
			ACC.pickupinstore.locationSearchSubmit($('#locationForSearch').val(), $('#atCartPage').val(), $('#entryNumber').val(), $(this).parents('form').attr('action'));
			return false;
		});

		$(document).on('keypress', '#locationForSearch', function (e)
		{
			if (e.keyCode === 13)
			{
				e.preventDefault();
				ACC.pickupinstore.locationSearchSubmit($('#locationForSearch').val(), $('#atCartPage').val(), $('input.entryNumber').val(), $(this).parents('form').attr('action'));
				return false;
			}
		});
	},

	bindPickupHereInStoreButtonClick: function ()
	{
		$(document).on('click','.pickup_add_to_bag_instore_button', function (e){
			$(this).prev('.hiddenPickupQty').val($('#pickupQty').val());
		});

		$(document).on('click','.pickup_here_instore_button', function (e){
			$(this).prev('.hiddenPickupQty').val($('#pickupQty').val());
			ACC.colorbox.close();
		});
	},

	locationSearchSubmit: function (location, cartPage, entryNumber, productCode, latitude, longitude)
	{
		$("#colorbox .js-add-to-cart-for-pickup-popup, #colorbox .js-qty-selector-minus, #colorbox .js-qty-selector-input, #colorbox .js-qty-selector-plus").attr("disabled","disabled");

		$.ajax({
			url: productCode,
			data: {locationQuery: location, cartPage: cartPage, entryNumber: entryNumber, latitude: latitude, longitude: longitude},
			type: "post",
			success: function (response)
			{
				ACC.pickupinstore.refreshPickupInStoreColumn(response);
			}
		});
	},

	createListItemHtml: function (data,id){

		var item="";
		item+='<li class="pickup-store-list-entry">';
		item+='<input type="radio" name="storeNamePost" value="'+data.displayName+'" id="pickup-entry-'+id+'" class="js-pickup-store-input" data-id="'+id+'">';
		item+='<label for="pickup-entry-'+id+'" class="js-select-store-label">';
		item+='<span class="pickup-store-info">';
		item+='<span class="pickup-store-list-entry-name">'+data.displayName+'</span>';
		item+='<span class="pickup-store-list-entry-address">'+data.line1+' '+data.line2+'</span>';
		item+='<span class="pickup-store-list-entry-city">'+data.town+'</span>';
		item+='</span>';
		item+='<span class="store-availability">';
		item+='<span class="available">'+data.formattedDistance+'<br>'+data.stockPickup+'</span>';
		item+='</span>';
		item+='</label>';
		item+='</li>';

		return item;
	},

	refreshPickupInStoreColumn: function (data){
		data = $.parseJSON(data);
		var listitems = "";

		$("#colorbox .js-pickup-component").data("data",data);

		for(i = 0;i < data["data"].length;i++){
			listitems += ACC.pickupinstore.createListItemHtml(data["data"][i],i)
		}

		$('#colorbox .js-pickup-store-list').html(listitems);
		ACC.pickupinstore.unbindPickupPaginationResults()
		ACC.pickupinstore.bindPickupPaginationResults()

		// select the first store
		var firstInput= $("#colorbox .js-pickup-store-input")[0];
		$(firstInput).click();


		$("#colorbox .js-add-to-cart-for-pickup-popup, #colorbox .js-qty-selector-minus, #colorbox .js-qty-selector-input, #colorbox .js-qty-selector-plus").removeAttr("disabled");


	},

	bindClickPickupInStoreButton :function()
	{


		$(document).on("click",".js-pickup-in-store-button",function(e){
			e.preventDefault();
			var ele = $(this);
			var productId = "pickupModal_" + $(this).attr('id');
			var cartItemProductPostfix = '';
			var productIdNUM = $(this).attr('id');
			productIdNUM = productIdNUM.split("_");
			productIdNUM = productIdNUM[1];

			if (productId !== null)
			{
				cartItemProductPostfix = '_' + productId;
			}

			var boxContent =  $("#popup_store_pickup_form > #pickupModal").clone();
			var titleHeader = $('#pickupTitle > .pickup-header').html();


			ACC.colorbox.open(titleHeader,{
				html:boxContent,
				width:"870px",
				onComplete: function(){

					$("#colorbox .js-add-to-cart-for-pickup-popup, #colorbox .js-qty-selector-minus, #colorbox .js-qty-selector-input, #colorbox .js-qty-selector-plus").attr("disabled","disabled");


					boxContent.show();
					ACC.pickupinstore.pickupStorePager();
					var tabs = $("#colorbox .js-pickup-tabs").accessibleTabs({
						tabhead:'.tabhead',
						tabbody: '.tabbody',
						fx:'show',
						fxspeed: 0,
						currentClass: 'active',
						autoAnchor:true,
						cssClassAvailable:true
					});

					$("#colorbox #pickupModal *").each(function ()
					{
						if($(this).attr("data-id")!= undefined)
						{
							$(this).attr("id", $(this).attr("data-id"));
							$(this).removeAttr("data-id");
						}
					});

					$("#colorbox input#locationForSearch").focus();

					// set a unique id
					$("#colorbox #pickupModal").attr("id", productId);

					// insert the product image
					$("#colorbox #" + productId + " .thumb").html(ele.data("img"));

					// insert the product cart details
					$("#colorbox #" + productId + " .js-pickup-product-price").html(ele.data("productcart"));


					var variants=ele.data("productcartVariants");
					var variantsOut="";

					$.each(variants,function(key,value){
						console.log(value)

						variantsOut += "<span>"+value+"</span>";

					})

					$("#colorbox #" + productId + " .js-pickup-product-variants").html(variantsOut);


					// insert the product name
					$("#colorbox  #" + productId + " .js-pickup-product-info").html(ele.data("productname"))

					// insert the form action
					$("#colorbox #" + productId + " form.searchPOSForm").attr("action", ele.data("actionurl"));

					// set a unique id for the form
					$("#colorbox #" + productId + " form.searchPOSForm").attr("id", "pickup_in_store_search_form_product_" + productIdNUM);

					// set the quantity, if the quantity is undefined set the quantity to the data-value defined in the jsp
					$("#colorbox #" + productId + " #pdpPickupAddtoCartInput").attr("value", ($('#pdpPickupAddtoCartInput').val() !== undefined ? $('#pdpPickupAddtoCartInput').val() : ele.data("value")));
					// set the entry Number
					$("#colorbox #" + productId + " input#entryNumber").attr("value", ele.data("entrynumber"));
					// set the cartPage bolean
					$("#colorbox #" + productId + " input#atCartPage").attr("value", ele.data("cartpage"));

					
					if(navigator.geolocation){
						navigator.geolocation.getCurrentPosition(
							function (position){
								ACC.pickupinstore.locationSearchSubmit('', $('#atCartPage').val(),  ele.data("entrynumber"), ele.data("actionurl"),position.coords.latitude, position.coords.longitude);
							},
							function (error){
									console.log("An error occurred... The error code and message are: " + error.code + "/" + error.message);
							}
						);
					}
					
					ACC.product.bindToAddToCartStorePickUpForm();

					
				}

			});

		})
	},

	pickupStorePager:function()
	{
		$(document).on("change","#colorbox .js-pickup-store-input",function(e){
			e.preventDefault();


			$("#colorbox .js-pickup-tabs li.first a").click();

			var storeData=$("#colorbox .js-pickup-component").data("data");
			storeData=storeData["data"];

			var storeId=$(this).data("id");

			var $ele = $("#colorbox .display-details");


			$.each(storeData[storeId],function(key,value){
				if(key=="url"){
					if(value!=""){
						$ele.find(".js-store-image").html('<img src="'+value+'" alt="" />');
					}else{
						$ele.find(".js-store-image").html('');
					}
				}else if(key=="productcode"){
					$ele.find(".js-store-productcode").val(value);
				}
				else if(key=="openings"){
					if(value!=""){
						var $oele = $ele.find(".js-store-"+key);
						var openings = "";
						$.each(value,function(key2,value2){
							openings += "<dt>"+key2+"</dt>";
							openings += "<dd>"+value2+"</dd>";
						});

						$oele.html(openings);

					}else{
						$ele.find(".js-store-"+key).html('');
					}

				}
				else if(key=="specialOpenings"){}
				else{
					if(value!=""){
						$ele.find(".js-store-"+key).html(value);
					}else{
						$ele.find(".js-store-"+key).html('');
					}
				}

			})





			$(document).one("click", "#colorbox .js-pickup-map-tab",function(){
				ACC.pickupinstore.storeId = storeData[storeId];
				ACC.global.addGoogleMapsApi("ACC.pickupinstore.drawMap");
			})

			
			

			var e=$("#colorbox .pickup-store-list-entry input:checked");


			$("#add_to_cart_storepickup_form .js-store-id").attr("id",e.attr("id"))
			$("#add_to_cart_storepickup_form .js-store-id").attr("name",e.attr("name"))
			$("#add_to_cart_storepickup_form .js-store-id").val(e.val())

			if(storeData[storeId]["stockLevel"] > 0 || storeData[storeId]["stockLevel"] == "")
			{
				var input = $("#add_to_cart_storepickup_form .js-qty-selector-input");
				input.data("max",storeData[storeId]["stockLevel"]); 
				ACC.productDetail.checkQtySelector(input, "reset");
				$("#add_to_cart_storepickup_form").show()
				
			} else{
				$("#add_to_cart_storepickup_form").hide()
			}


		})

		$(document).on("click",".js-select-store-label",function(e){
			$("#colorbox .js-pickup-component").addClass("show-store")
			$("#colorbox #cboxTitle .headline-inner").hide()
			$("#colorbox #cboxTitle .back-to-storelist").show()
		})

		$(document).on("click",".js-back-to-storelist",function(e){
			$("#colorbox .js-pickup-component").removeClass("show-store")
			$("#colorbox #cboxTitle .headline-inner").show()
			$("#colorbox #cboxTitle .back-to-storelist").hide()
		})

	},


	bindPickupButton : function(){
		$(document).on("click",".js-pickup-button",function(e){
			e.preventDefault();
			$e = $(this).parent().nextAll(".js-inline-layer")
			$e.addClass("open")

			//$e.height($e.height())
			var h= $e.height()
			$e.removeClass("open")

			$e.animate({
				height: h
			})
		})
	},


	bindPickupClose : function(){
		$(document).on("click",".js-close-inline-layer",function(e){
			e.preventDefault();
			$e = $(this).parents(".js-inline-layer")

			$e.animate({
				height: 0
			})
		})
	},
	
	checkIfPointOfServiceIsEmpty: function (cartEntryDeliveryModeForm)
	{
		return (!cartEntryDeliveryModeForm.find('.pointOfServiceName').text().trim().length);
	},
	
	validatePickupinStoreCartEntires: function ()
	{
		var validationErrors = false;
		$("form.cartEntryShippingModeForm").each(function ()
		{
			var formid = "#" + $(this).attr('id');
			if ($(formid + ' input[value=pickUp][checked]').length && ACC.pickupinstore.checkIfPointOfServiceIsEmpty($(this)))
			{
				$(this).addClass("shipError");
				validationErrors = true;
			}
		});

		if (validationErrors)
		{
			$('div#noStoreSelected').show().focus();
			$(window).scrollTop(0);
		}
		return validationErrors;
	},

	
	drawMap: function(){

		storeInformation = ACC.pickupinstore.storeId;

		if($("#colorbox .js-map-canvas").length > 0)
		{			


			$("#colorbox .js-map-canvas").attr("id","pickup-map")
			var centerPoint = new google.maps.LatLng(storeInformation["storeLatitude"], storeInformation["storeLongitude"]);
			
			var mapOptions = {
				zoom: 13,
				zoomControl: true,
				panControl: true,
				streetViewControl: false,
				mapTypeId: google.maps.MapTypeId.ROADMAP,
				center: centerPoint
			}
			
			var map = new google.maps.Map(document.getElementById("pickup-map"), mapOptions);
			
			var marker = new google.maps.Marker({
				position: new google.maps.LatLng(storeInformation["storeLatitude"], storeInformation["storeLongitude"]),
				map: map,
				title: storeInformation["name"],
				icon: "https://maps.google.com/mapfiles/marker" + 'A' + ".png"
			});
			var infowindow = new google.maps.InfoWindow({
				content: storeInformation["name"],
				disableAutoPan: true
			});
			google.maps.event.addListener(marker, 'click', function (){
				infowindow.open(map, marker);
			});
		}
		
	}

};