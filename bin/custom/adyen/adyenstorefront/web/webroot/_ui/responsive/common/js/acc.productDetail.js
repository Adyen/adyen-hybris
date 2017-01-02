ACC.productDetail = {

	_autoload: [
		"initPageEvents",
		"bindVariantOptions"
	],


	checkQtySelector:function(self,mode){
		var input= $(self).parents(".js-qty-selector").find(".js-qty-selector-input");
		var inputVal = parseInt(input.val());
		var max= input.data("max");


		var minusBtn= $(self).parents(".js-qty-selector").find(".js-qty-selector-minus");
		var plusBtn= $(self).parents(".js-qty-selector").find(".js-qty-selector-plus");


		$(self).parents(".js-qty-selector").find(".btn").removeAttr("disabled");

		if(mode=="minus"){
			if(inputVal != 1){
				ACC.productDetail.updateQtyValue(self,inputVal-1)
				if(inputVal-1 == 1){
					minusBtn.attr("disabled","disabled")
				}

			}else{
				minusBtn.attr("disabled","disabled")
			}
		}else if(mode=="reset"){
			ACC.productDetail.updateQtyValue(self,1)
	
		}else if(mode=="plus"){
			if(inputVal != max){
				ACC.productDetail.updateQtyValue(self,inputVal + 1)
				if(inputVal+1 == max){
					plusBtn.attr("disabled","disabled")
				}
			}else{
				plusBtn.attr("disabled","disabled")
			}
		}else if(mode=="input"){
			if(inputVal == 1){
				$(self).parents(".js-qty-selector").find(".js-qty-selector-minus").attr("disabled","disabled")
			}else if(inputVal == max){
				$(self).parents(".js-qty-selector").find(".js-qty-selector-plus").attr("disabled","disabled")
			}else if(inputVal<1){
				ACC.productDetail.updateQtyValue(self,1)
				$(self).parents(".js-qty-selector").find(".js-qty-selector-minus").attr("disabled","disabled")
			}else if(inputVal>max){
				ACC.productDetail.updateQtyValue(self,max)
				$(self).parents(".js-qty-selector").find(".js-qty-selector-plus").attr("disabled","disabled")
			}
		}

	},
	updateQtyValue: function(self,value){
		var input= $(self).parents(".js-qty-selector").find(".js-qty-selector-input");
		var addtocartQty= $(self).parents(".addtocart-component").find("#addToCartForm").find(".js-qty-selector-input");;

		input.val(value);
		addtocartQty.val(value);
	},
	initPageEvents: function ()
	{


		$(document).on("click",'.js-qty-selector .js-qty-selector-minus',function(){
			ACC.productDetail.checkQtySelector(this,"minus");
		})


		$(document).on("click",'.js-qty-selector .js-qty-selector-plus',function(){
			ACC.productDetail.checkQtySelector(this,"plus");
		})

		$(document).on("keydown",'.js-qty-selector .js-qty-selector-input',function(e){

			if(($(this).val() != " " && ((e.which >= 48 && e.which <= 57 ) ||(e.which >= 96 && e.which <= 105 ))  ) || e.which == 8 || e.which == 46 || e.which == 37 || e.which == 39 || e.which == 9 ){}
			else if(e.which == 38 ){
				ACC.productDetail.checkQtySelector(this,"plus");
			}
			else if(e.which == 40 ){
				ACC.productDetail.checkQtySelector(this,"minus");
			}
			else{
				e.preventDefault();
			}
		})

		$(document).on("keyup",'.js-qty-selector .js-qty-selector-input',function(e){
			ACC.productDetail.checkQtySelector(this,"input");
			ACC.productDetail.updateQtyValue(this,$(this).val());
		
		})


		$("#Size").change(function () {
			var url = "";
			var selectedIndex = 0;
			$("#Size option:selected").each(function () {
				url = $(this).attr('value');
				selectedIndex = $(this).attr("index");
			});
			if (selectedIndex != 0) {
				window.location.href=url;
			}
		});

		$("#variant").change(function () {
			var url = "";
			var selectedIndex = 0;
			$("#variant option:selected").each(function () {
				url = $(this).attr('value');
				selectedIndex = $(this).attr("index");
			});
			if (selectedIndex != 0) {
				window.location.href=url;
			}
		});

	},
	
	bindVariantOptions: function()
	{
		ACC.productDetail.bindCurrentStyle();
		ACC.productDetail.bindCurrentSize();
		ACC.productDetail.bindCurrentType();
	},
	
	bindCurrentStyle: function(){
		var currentStyle = $("#currentStyleValue").data("styleValue");
		var styleSpan = $(".styleName");
		if (currentStyle != null) {
			styleSpan.text(": " + currentStyle);
		}
		
	},
	
	bindCurrentSize: function(){
		var currentSize = $("#currentSizeValue").data("sizeValue");
		var sizeSpan = $(".sizeName");
		if(currentSize != null){
			sizeSpan.text(": " + currentSize);
		}
		
	},
	
	bindCurrentType: function(){
		var currentSize = $("#currentTypeValue").data("typeValue");
		var sizeSpan = $(".typeName");
		if(currentSize != null){
			sizeSpan.text(": " + currentSize);
		}
		
	}


};