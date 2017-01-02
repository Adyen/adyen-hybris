(function ($)
{

	$.fn.autocomplete = function (o)
	{
		var timeout;
		o = $.extend({}, $.fn.autocomplete.defaults, o);
		return this.each(function (i, el)
		{
			// this
			var $e = $(el);
			// TODO: make sure that o.target is a jQuery object
			var $target = o.target;

			$e.bind('input', function (e)
			{

				// get the current text of the input field
				var text = $e.val();

				if (text.length >= o.minLength)
				{
					// are we looking at a source array or remote data?
					if ($.isArray(o.source))
					{
						var data = o.source.sort().filter(function (element, index, array)
						{
							var re = new RegExp('^' + text, 'i');
							if ($.isPlainObject(element))
							{
								element_text = element.label;
							}
							else
							{
								element_text = element;
							}
							return re.test(element_text);
						});
						buildItems(data, false);
					}
					else
					{
						clearTimeout(timeout);
						timeout = setTimeout(function ()
						{
							//Fetch directly from json response as we don't cache by building an array for regex match with entered text.
							$.get(o.source, {term: text}, function (data)
							{
								buildItems(data, true);
							}, "json")
						}, o.waitTime);
					}
				}
				if (text.length <= o.minLength)
				{
					$target.html('').listview('refresh');
				}
			});

			function buildItems(data, withProductSearchResults)
			{
				var str = [];
				buildSuggestions(data, str);
				if (withProductSearchResults)
				{
					buildProductResults(data, str);
				}
				$target.html(str.join('')).listview('refresh');
				clearTarget();
			}

			function buildSuggestions(data, str)
			{
				$(data.suggestions).each(function (index, value)
				{
					// are we working with objects or strings?
					if ($.isPlainObject(value))
					{
						str.push('<li><a href="' + o.link + value.term + '">' + value.term + '</a></li>');
					}
					else
					{
						str.push('<li><a href="' + o.link + value + '">' + value + '</a></li>');
					}
				});
			}

			function buildProductResults(data, str)
			{
				$(data.products).each(function (index, value)
				{
					var renderHTML = "<li><a href='" + ACCMOB.config.contextPath + value.url + "'>";
					if (o.displayProductImages && value.images[0] != null)
					{
						renderHTML += "<img class='ui-li-thumb' src='" + value.images[0].url + " ' />";
					}
					renderHTML += "<span class='ui-li-heading'>" + value.name +
							"</span><span class='ui-li-desc'>" + value.price.formattedValue + "</span>" +
							"</a></li>";
					str.push(renderHTML);
				});
			}

			function clearTarget()
			{
				$target.on("tap", "li", function (event)
				{
					$e.attr("value", text);
					$target.hide();
				});
			}
		});
	};

	$.fn.autocomplete.defaults = {
		target: $(),
		source: null,
		link: null,
		minLength: 0,
		waitTime: 0,
		displayProductImages: true
	};

})(jQuery);

