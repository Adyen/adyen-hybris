ACC.carousel = {

	addthis_config: {
		ui_click: true
	},

	bindAll: function ()
	{
		this.bindJCarousel();
	},
	bindJCarousel: function ()
	{
		jQuery('.yCmsContentSlot.span-4 .jcarousel-skin').jcarousel({
			vertical: true
		});
		
		
		
	
	

		jQuery('.yCmsContentSlot.span-24 > .scroller .jcarousel-skin').jcarousel({

		});


		$(".modal").colorbox({
			onComplete: function ()
			{
				ACC.common.refreshScreenReaderBuffer();
			},
			onClosed: function ()
			{
				ACC.common.refreshScreenReaderBuffer();
			}
		});
		$('#homepage_slider').waitForImages(function ()
		{
			$(this).slideView({toolTip: true, ttOpacity: 0.6, autoPlay: true, autoPlayTime: 8000});
		});
	}
};

$(document).ready(function ()
{
	ACC.carousel.bindAll();
});

