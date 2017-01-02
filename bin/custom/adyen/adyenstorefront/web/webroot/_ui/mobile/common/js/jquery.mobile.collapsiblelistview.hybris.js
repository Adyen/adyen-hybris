(function($, undefined ) {
	// component definiton
	$.widget("mobile.collapsiblelistview", $.mobile.widget, {
		options: {
			expandCueText: " click to expand contents",
			collapseCueText: " click to collapse contents",
			collapsed: true,
			heading: "h1,h2,h3,h4,h5,h6,legend",
			theme: null,
			contentTheme: null,
			iconTheme: "d",
			mini: false,
			initSelector: ":jqmData(role='collapsiblelistview')"
		},

		_create: function() {
			var $el = this.element,
				o = this.options;
			o.contentTheme = $el.data("theme") || $el.parent("li").parent("ul").data("theme") || 'e';

			$el.addClass( "ui-btn-inner ui-li ul-li-collapsiblelistview" );
			$el.parent("li").removeClass("ui-body-e ui-li ui-li-static").addClass("auto ui-btn ui-btn-up-"+o.contentTheme+" ui-btn-icon-right ui-li-has-arrow ui-li");
			$el.children( o.heading ).first().find("a").addClass("ui-link-inherit").wrap( "<div class='ui-btn-text'></div>");
			var lineItem = $el.children( o.heading ).first().find(".ui-btn-text");
				lineItem.find("a").removeClass("ui-link")
			$el.children( o.heading ).first().replaceWith(lineItem);

			var collapsible = $el.find( "ul" ).first().addClass( "ui-collapsible" ),
				collapsibleIcon = $el.children( "a" ).first(),
				collapsibleContent = collapsible.wrapInner( "<div class='ui-collapsible-content'></div>" ).find( ".ui-collapsible-content" );

			collapsibleIcon
				.wrap( "<span class='ui-icon ui-icon-plus-r ui-icon-shadow'></span>" )
				.empty()
				.addClass( "ui-collapsible-heading");

			//events
			collapsible.bind( "expand collapse", function( event ) {

				if ( !event.isDefaultPrevented() ) {

					event.preventDefault();

					var $this = $( this ),
						isCollapse = ( event.type === "collapse" );

					collapsibleIcon
						.toggleClass( "ui-collapsible-heading-collapsed", isCollapse)
						.find( ".ui-collapsible-heading-status" )
						.text( isCollapse ? o.expandCueText : o.collapseCueText )
						.end()
						.parent( ".ui-icon" )
						.toggleClass( "ui-icon-minus", !isCollapse )
						.toggleClass( "ui-icon-plus", isCollapse );

					$this.toggleClass( "ui-collapsible-collapsed", isCollapse );

					collapsibleContent.toggleClass( "ui-collapsible-content-collapsed", isCollapse ).attr( "aria-hidden", isCollapse );
					collapsibleContent.trigger( "updatelayout" );
				}
			}).trigger( o.collapsed ? "collapse" : "expand" );

			collapsibleIcon
				.bind( "click", function( event ) {
					var type = collapsibleIcon.is( ".ui-collapsible-heading-collapsed" ) ? "expand" : "collapse";
					collapsible.trigger( type );
					event.preventDefault();
				});
		}
	});

	// binding to element
	$(document).bind("pagecreate create", function (e)
	{
		$(":jqmData(role=collapsiblelistview)", e.target).collapsiblelistview();
	});

}) (jQuery);