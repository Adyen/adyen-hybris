ACC.autocomplete = {

	bindAll: function ()
	{
		this.bindSearchAutocomplete();
	},

	bindSearchAutocomplete: function ()
	{
		$search = $("#search");
		var option = $search.data("options");
		var cache = {};

		$search.autocomplete({
			minLength: option.minCharactersBeforeRequest,
			delay: option.waitTimeBeforeRequest,
			appendTo: ".siteSearch",
			source: function (request, response)
			{
				var term = request.term.toLowerCase();
				if (term in cache)
				{
					return response(cache[term]);
				}

				$.getJSON(option.autocompleteUrl, {term: request.term}, function (data)
				{
					var autoSearchData = [];
					if(data.suggestions != null){
						$.each(data.suggestions, function (i, obj)
						{
							autoSearchData.push(
									{value: obj.term,
										url: ACC.config.contextPath + "/search?text=" + obj.term,
										type: "autoSuggestion"});
						});
					}
					if(data.products != null){
						$.each(data.products, function (i, obj)
						{
							autoSearchData.push(
									{value: obj.name,
										code: obj.code,
										desc: obj.description,
										manufacturer: obj.manufacturer,
										url: ACC.config.contextPath + obj.url,
										price: obj.price.formattedValue,
										type: "productResult",
										image: obj.images[0].url});
						});
					}
					cache[term] = autoSearchData;
					return response(autoSearchData);
				});
			},
			focus: function (event, ui)
			{
				return false;
			},
			select: function (event, ui)
			{
				window.location.href = ui.item.url;
			}
		}).data("autocomplete")._renderItem = function (ul, item)
		{
			if (item.type == "autoSuggestion")
			{
				renderHtml = "<a href='?q=" + item.value + "' class='clearfix'>" + item.value + "</a>";
				return $("<li class='suggestions'>")
						.data("item.autocomplete", item)
						.append(renderHtml)
						.appendTo(ul);
			}
			if (item.type == "productResult")
			{
				var renderHtml = "<a href='" + ACC.config.contextPath + item.url + "' class='product clearfix'>";
				if (option.displayProductImages &&  item.image != null)
				{
					renderHtml += "<span class='thumb'><img src='" + item.image + "' /></span><span class='desc clearfix'>";
				}
				renderHtml += "<span class='title'>" + item.value +
						"</span><span class='price'>" + item.price + "</span></span>" +
						"</a>";
				return $("<li class='product'>").data("item.autocomplete", item).append(renderHtml).appendTo(ul);
			}
		};
	}
};

$(document).ready(function ()
{
	ACC.autocomplete.bindAll();
});