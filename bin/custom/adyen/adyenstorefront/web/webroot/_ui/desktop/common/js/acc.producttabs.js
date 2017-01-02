ACC.productTabs = {

	bindAll: function ()
	{
		if($('#productTabs').length>0){
	
			// only load review one at init 
			ACC.productTabs.showReviewsAction("reviews");
		
			 ACC.productTabs.productTabs = $('#productTabs').accessibleTabs({
		        wrapperClass: 'content',
		        currentClass: 'current',
		        tabhead: '.tabHead',
		        tabbody: '.tabBody',
		        fx:'show',
		        fxspeed: null,
		        currentInfoText: 'current tab: ',
		        currentInfoPosition: 'prepend',
		        currentInfoClass: 'current-info',
				autoAnchor:true
		    });


			$(document).on("click", '#write_review_action_main, #write_review_action', function(e){
				e.preventDefault();
				ACC.productTabs.scrollToReviewTab('#write_reviews')
				$('#reviewForm input[name=headline]').focus();
			});
		
			$('#based_on_reviews, #read_reviews_action').bind("click", function(e) {
				e.preventDefault();
				ACC.productTabs.scrollToReviewTab('#reviews')
			});
		
		
			$(document).on("click", '#show_all_reviews_top_action, #show_all_reviews_bottom_action', function(e){
				e.preventDefault();
				ACC.productTabs.showReviewsAction("allreviews");
				$(this).hide();
			});
		
		}

	},
	
	scrollToReviewTab: function (pane)
	{
		$.scrollTo('#productTabs', 300, {axis: 'y'});
		ACC.productTabs.productTabs.showAccessibleTabSelector('#tab-reviews');
		$('#write_reviews,#reviews').hide();
		$(pane).show();
	},
	
	showReviewsAction: function (s)
	{
		$.get($("#reviews").data(s), function (result){
			$('#reviews').html(result);
		});
	}
};

$(document).ready(function ()
{
	ACC.productTabs.bindAll();
});

