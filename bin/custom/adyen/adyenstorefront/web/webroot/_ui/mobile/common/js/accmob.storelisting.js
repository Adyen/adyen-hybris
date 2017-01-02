ACCMOB.storelisting = {

	scrollingConfig: {offset: '100%'},

	bindShowMoreStores: function (showMoreStoresArea, showMoreStoresFunction)
	{
		this.showMoreStoresArea = showMoreStoresArea;
		this.showMoreFunction = showMoreStoresFunction;
		ACCMOB.storelisting.showMoreStoresArea.waypoint(ACCMOB.storelisting.scrollingHandler, ACCMOB.storelisting.scrollingConfig);
	},

	scrollingHandler: function (event, direction)
	{
		if (direction === 'down' && ACCMOB.storelisting.showMoreFunction !== 'undefined')
		{
			ACCMOB.storelisting.showMoreFunction();

			// do not refresh page unless waypoint was scrolled
			$.extend(ACCMOB.storelisting.scrollingConfig, {onlyOnScroll: true});
			// redefine waypoint
			ACCMOB.storelisting.showMoreStoresArea.waypoint('remove');
			ACCMOB.storelisting.showMoreStoresArea.waypoint(ACCMOB.storelisting.scrollingConfig);
		}
	}
}
