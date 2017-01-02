ACCMOB.productlisting = {

	infiniteScrollingConfig: {offset: '100%'},
	currentPage: 0,
	numberOfPages: Number.MAX_VALUE,
	baseQuery: $("#sort_form1 input[type='hidden']").val() || "",

	triggerLoadMoreResults: function ()
	{
		if (ACCMOB.productlisting.currentPage < ACCMOB.productlisting.numberOfPages)
		{
			// show the page loader
			ACCMOB.productlisting.loadMoreResults(parseInt(ACCMOB.productlisting.currentPage) + 1);

		}
	},

	scrollingHandler: function (event, direction)
	{
		if (direction === "down")
		{
			ACCMOB.productlisting.triggerLoadMoreResults();
		}
	},

	loadMoreResults: function (page)
	{
		ACCMOB.common.showPageLoadingMsg();
		var isProductListPage = $("ul#resultsList").length > 0;
		var isProductGridPage = $("#resultsGrid").length > 0;

		if (isProductListPage || isProductGridPage)
		{
			$.ajax({
				url: ACCMOB.common.currentPath + "/results?q=" + ACCMOB.productlisting.baseQuery + "&page=" + page,
				success: function (data)
				{
					if (data.pagination !== undefined)
					{
						if (isProductListPage)
						{ //Product List Page
							$("ul#resultsList").append($.tmpl($("#resultsListItemsTemplate"), data)).listview('refresh');
						}


						if (isProductGridPage)
						{ //Product Grid Page
							$("#resultsGrid").append($.tmpl($("#resultsGridItemsTemplate"), data));
						}

						ACCMOB.productlisting.updatePaginationInfos(data.pagination);
						ACCMOB.productlisting.showMoreResultsArea.waypoint(ACCMOB.productlisting.infiniteScrollingConfig); // reconfigure waypoint eventhandler
					}
				}
			});
		}
		ACCMOB.common.hidePageLoadingMsg();
	},

	updatePaginationInfos: function (paginationInfo)
	{
		ACCMOB.productlisting.currentPage = parseInt(paginationInfo.currentPage);
		ACCMOB.productlisting.numberOfPages = parseInt(paginationInfo.numberOfPages);
	},

	bindShowMoreResults: function (showMoreResultsArea)
	{
		this.showMoreResultsArea = showMoreResultsArea;

		ACCMOB.productlisting.showMoreResultsArea.waypoint(ACCMOB.productlisting.scrollingHandler, ACCMOB.productlisting.infiniteScrollingConfig);
	},

	bindSortingSelector: function ()
	{
		$('#sort_form1, #sort_form2').change(function ()
		{
			this.submit();
		});
	},

	initialize: function ()
	{
		with (ACCMOB.productlisting)
		{
			bindShowMoreResults($('#footer'));
			bindSortingSelector();
		}
	}
};

$(document).ready(function ()
{
	ACCMOB.productlisting.initialize();
});

