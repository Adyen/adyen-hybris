(function($){$.widget("mobile.easydialog", $.mobile.widget, {
options:{
	header: "",
	content: "",
	type: "standard",
	openCB: false,
	closeCB: false,
	p:".",c:"ui-easydialog",ctn:"-container",bg:"-screen",close:"-close",h:"-hidden"
},
theme:{'standard':{header:'a',dialog:'d',input:'a'},'conf':{header:'i',dialog:'i',input:'i'},'info':{header:'h',dialog:'h',input:'i'},'error':{header:'g',dialog:'g',input:'i'}},
_create: function(){
	o=this.options;t=this.theme[o.type];$.mobile.easyDialog=this;
	$(o.p+o.c).remove();
	$($.mobile.activePage).append("<div class='"+o.c+" "+o.c+o.ctn+" "+o.c+o.h+" ui-body-"+t.dialog+"'><a class='"+o.c+o.close+"' rel='close' href='#'>Close</a><div class='ui-header ui-bar-"+t.header+"'>"+o.header +"</div>"+o.content+"</div><div class='"+o.c+" "+o.c+o.bg+" "+o.c+o.h+"'></div>");
	$(o.p+o.c+o.close).buttonMarkup({theme:t.input,icon:'delete',iconpos:'notext'});
	$(o.p+o.c+o.ctn).trigger('create').css("top", (($(window).height() - $(o.p+o.c+o.ctn).outerHeight()) / 2) + $(window).scrollTop() + "px");
	$(o.p+o.c).removeClass(o.c+o.h);
	this._oCB();
	if(typeof o.openCB == 'function') o.openCB();
},
close: function(){
	o=this.options;
	$(o.p+o.c).remove();
	delete $.mobile.easyDialog;
	this._cCB();
	if(typeof o.closeCB == 'function') o.closeCB();
},
_oCB: function(){
	$("body").css({overflow:"hidden"});
},
_cCB: function(){
	$("body").css({overflow:"visible"});
},
_init: function(){
	o=this.options;that=this;
	$(document).one("click",o.p+o.c+o.close,function(){that.close()});
}
});})(jQuery);