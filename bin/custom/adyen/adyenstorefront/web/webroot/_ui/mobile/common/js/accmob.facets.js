ACCMOB.facets = {
	params: {},
	cachedJSONResponse: {},
	currentSelection: {},
	currentSearchQuery: "",

	// begin DATA -------------------------------------------------------------------
	setCurrentSearchQuery: function (untouched)
	{
		var pageQuery = $("#facetRefinements-page").data("searchquery");
		if (pageQuery !== undefined) {
		
			if (untouched == null)
			{  // set default value
				var untouched = false;
			}
	
			if (typeof pageQuery !== 'undefined')
			{
				if (untouched)
				{
					ACCMOB.facets.currentSearchQuery = pageQuery
				}
				else
				{
					ACCMOB.facets.currentSearchQuery = pageQuery.split(":").slice(0, 2).join(":"); // take only searchterm and sorting
				}
			}
		}
	},

	createQueryParams: function (initialCreation)
	{
		if (initialCreation == null)
		{  // set default value
			var initialCreation = false;
		}

		ACCMOB.facets.params = {};
		var selectedOptions = $("fieldset.facetValueList input:checked");
		var facets = selectedOptions.map(function ()
		{
			return $(this).data("query");
		}).get().join(":");

		if (facets !== null && facets !== [] && facets !== "")
		{
			ACCMOB.facets.params = {q: [ACCMOB.facets.currentSearchQuery, facets].join(":")};
		}
		else
		{
			if (initialCreation)
			{
				ACCMOB.facets.setCurrentSearchQuery(true);
			}
			ACCMOB.facets.params = {q: ACCMOB.facets.currentSearchQuery};
			if (initialCreation)
			{
				ACCMOB.facets.setCurrentSearchQuery();
			}
		}
		;
	},

	getFacetData: function ()
	{
		ACCMOB.common.showPageLoadingMsg();
		$.ajax({
			url: ACCMOB.common.currentPath + "/facets",
			dataType: "json",
			async: false,
			data: ACCMOB.facets.params,
			success: function (data)
			{
				ACCMOB.facets.cachedJSONResponse = data;
				ACCMOB.common.hidePageLoadingMsg();
			} // cache data
		});

	},
	// end DATA ---------------------------------------------------------------------

	// begin BINDINGS ---------------------------------------------------------------
	bindUpdateFacet: function (trigger, eventName)
	{
		$(document.body).on(eventName, trigger, function (event)
		{
			ACCMOB.facets.updateRefinementsList();
			$("#facetRefinements-page ul").listview();
			ACCMOB.facets.updateFacetContents();
			ACCMOB.facets.updateFormElements();
		});
	},

	bindClearFacetSelections: function (clearFacetSelectionsButton)
	{
		ACCMOB.facets.setCurrentSearchQuery();

		$(document.body).on('click', clearFacetSelectionsButton, function ()
		{
			$(this).attr("href", "?" + $.param({q: ACCMOB.facets.currentSearchQuery}));
		});

	},

	bindApplyFilter: function (applyFilterButton)
	{
		$(document.body).on('click', applyFilterButton, function ()
		{
			ACCMOB.facets.createQueryParams();
			$(this).attr("href", "?" + $.param({q: ACCMOB.facets.params.q}));
		});
	},

	bindAddFilterButton: function (addFilterButton)
	{
		$(document.body).on('click', addFilterButton, function ()
		{
			with (ACCMOB.facets)
			{
				updateRefinementsList(true);
				renderFacetPages();
			}
		});
		// set status icon
		var pageQuery = $("#facetRefinements-page").data("searchquery");
		if (pageQuery !== undefined) {
			if (pageQuery.split(":")[2] !== undefined)
			{
				$(addFilterButton).find(".ui-btn-inner span:last").removeClass("ui-icon-checkbox-off").addClass("ui-icon-checkbox-on");
			}
		}
	},

	setTemplates: function ()
	{
		this.refinementsListTemplate = $("#refinementsListTemplate");
		this.refinementFacetPageTemplate = $("#refinementFacetPageTemplate");
		this.refinementFacetContentTemplate = $("#refinementFacetContentTemplate");
	},
	// end BINDINGS ---------------------------------------------------------------

	// begin RENDER ---------------------------------------------------------------

	renderFacetPages: function ()
	{
		$.each(ACCMOB.facets.cachedJSONResponse.facets, function (index, facet)
		{
			ACCMOB.facets.renderFacetPage(facet);
			ACCMOB.facets.renderFacetLink(facet);
		});
	},

	renderFacetLink: function (facet)
	{
		// collect all selected values of facet
		var facetNames = [];
		$.each(facet.values, function (i, facetValue)
		{
			if (facetValue.selected)
			{
				facetNames.push(facetValue.name);
			}
		});
		// merge names of selected facetvalues and append to button
		$("#" + facet.name + "-button").append("<span class='refinementSetFilter'>" + facetNames.join("<span class='refinementFilterDelimiter'>|</span>") + "</span>");
	},

	renderFacetPage: function (facet)
	{
		$.tmpl(ACCMOB.facets.refinementFacetPageTemplate, facet).appendTo("body");
	},

	renderRefinementsList: function ()
	{
		$("div#facetRefinements-page div[data-role='content'] ul").replaceWith($.tmpl(ACCMOB.facets.refinementsListTemplate, ACCMOB.facets.cachedJSONResponse));
	},
	// end RENDER -----------------------------------------------------------------

	// begin UPDATE ---------------------------------------------------------------
	updateFacetContents: function ()
	{
		$.each(ACCMOB.facets.cachedJSONResponse.facets, function (index, facet)
		{
			if ($("#" + facet.name + "-page").length <= 0)
			{
				ACCMOB.facets.renderFacetPage(facet);
			}
			ACCMOB.facets.renderFacetLink(facet);
		});
	},

	updateRefinementsList: function (initialCreation)
	{
		if (initialCreation == null)
		{
			var initialCreation = false; // set default value
		}

		ACCMOB.facets.createQueryParams(initialCreation);
		ACCMOB.facets.getFacetData();
		ACCMOB.facets.renderRefinementsList();
	},

	updateFormElements: function ()
	{
		$("div.item_container_holder input[type='checkbox']").checkboxradio();
		$("div.item_container_holder input[data-type='search']").textinput();
		$("div.item_container_holder [data-role=button]").button();
		$("div.item_container_holder fieldset").controlgroup();
		$("#changeLocationLink").addClass("ui-link");
		$("div.item_container_holder").addClass("ui-content ui-body-d");
	},
	// end UPDATE ---------------------------------------------------------------

	redirectIfPageWasReloaded: function ()
	{
		var currentUrl = $.mobile.path.parseUrl(window.location.href);

		if (currentUrl.hash !== "" && currentUrl.hash.match(/-page$/))
		{

			if (currentUrl.hash === "#facetRefinements-page")
			{
				history.go(-1);
			}
			else
			{
				history.go(-2);
			}
			return false;
		}
		return true;
	},

	initialize: function ()
	{
		with (ACCMOB.facets)
		{
			bindAddFilterButton("#addFilters");
			setTemplates();
			setCurrentSearchQuery(null);
			bindUpdateFacet(".facetPage input", "change");
			bindUpdateFacet(".multiSelectFacetPage a.backToFacets", "click");
			bindApplyFilter("#applyFilter");
			bindClearFacetSelections("#clearFacetSelections");
		}
	}
};

$(document).ready(function ()
{
	ACCMOB.facets.redirectIfPageWasReloaded();
	ACCMOB.facets.initialize();
});
