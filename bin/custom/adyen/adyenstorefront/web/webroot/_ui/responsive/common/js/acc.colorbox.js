ACC.colorbox = {
	config: {
		maxWidth:"100%",
		opacity:0.7,
		width:"auto",
		transition:"none",
		close:'<span class="glyphicon glyphicon-remove"></span>',
		title:'<div class="headline"><span class="headline-text">{title}</span></div>',
		onComplete: function() {
			$.colorbox.resize();
			ACC.common.refreshScreenReaderBuffer();
		},
		onClosed: function() {
			ACC.common.refreshScreenReaderBuffer();
		}
	},

	open: function(title,config){
		var config = $.extend({},ACC.colorbox.config,config);
		config.title = config.title.replace(/{title}/g,title);
		return $.colorbox(config);
	},

	resize: function(){
		$.colorbox.resize();
	},

	close: function(){
		$.colorbox.close();
		$.colorbox.remove();
	}
};