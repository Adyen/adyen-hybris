ACC.refinements = {

	bindAll: function ()
	{
		this.bindRefinementCategoryToggles();
		this.bindMoreLessToggles();
		this.bindMoreStoresToggles()
	},

	bindRefinementCategoryToggles: function ()
	{
		$('a.refinementToggle').each(function ()
		{
			$(this).attr('title', $(this).data('hideFacetText'));

			$(this).on("click", function ()
			{
				var content = $(this).parents('.facet').find('div.facetValues');
				$(this).attr('title', $(content).is(':visible') ? $(this).data('showFacetText') : $(this).data('hideFacetText'));
				$(this).toggleClass("close");
				$(content).slideToggle();
				return false;
			});

			$(this).next().click(function ()
			{
				$(this).prev().click();
			});
		});
	},
	
	bindMoreLessToggles: function ()
	{
		$("a.moreFacetValues").click(function(e){
			e.preventDefault();
				
			var eParent = $(this).parents(".facetValues");
			eParent.find(".topFacetValues").hide();
			eParent.find(".allFacetValues").show();
		})
		
		$("a.lessFacetValues").click(function(e){
			e.preventDefault();
				
			var eParent = $(this).parents(".facetValues");
			eParent.find(".topFacetValues").show();
			eParent.find(".allFacetValues").hide();
		})
	},
	
	bindMoreStoresToggles: function ()
	{
		$("a.moreStoresFacetValues").click(function(e){
			e.preventDefault();
				
			$(this).parents('div.allFacetValues').find('li.hidden').slice(0, 5).removeClass('hidden').first().find('input:[type=checkbox]').focus();
		})
		
	}
};

$(document).ready(function ()
{
	ACC.refinements.bindAll();
});
