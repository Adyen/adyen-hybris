ACCMOB.carousel = {

	initialize: function ()
	{
		this.bindJCarousel();
	},
	bindJCarousel: function ()
	{
		
	}
}

$(window).load(function ()
{
	 $(".owl-carousel").owlCarousel({
		navigation : true, 
		slideSpeed : 300,
		paginationSpeed : 400,
		singleItem:true,
		pagination:true
	 
	});
});

