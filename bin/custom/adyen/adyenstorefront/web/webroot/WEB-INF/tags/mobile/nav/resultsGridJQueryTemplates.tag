<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<script id="resultsGridItemsTemplate" type="text/x-jquery-tmpl">
	{{each(i, result) $data.results}}
		{{if i == 0}}
			<div class="ui-grid-a">
				<div class='ui-block-a left'>
					{{tmpl(result) "#resultsGridItemProductTemplate"}}
				</div>
			{{if i == $data.results.length - 1}}
			</div>
			{{/if}}
		{{else}}
			{{if ((i+1) % 2) == 0}}
				<div class='ui-block-b right'>
					{{tmpl(result) "#resultsGridItemProductTemplate"}}
				</div>
			</div>
			{{else}}
				<div class="ui-grid-a">
					<div class='ui-block-a left'>
						{{tmpl(result) "#resultsGridItemProductTemplate"}}
					</div>
				{{if i == $data.results.length - 1}}
				</div>
				{{/if}}
			{{/if}}
		{{/if}}
	{{/each}}
</script>
<script id="resultsGridItemProductTemplate" type="text/x-jquery-tmpl">
	<a href="<c:url value="/" />/{{= url}}" class="ui-link">
		<div class="productTitle">{{= name}}</div>

		<div class="prod_image_main">
			{{if $.isEmptyObject(images)}}
			<theme:image code="img.missingProductImage.thumbnail" alt="{{= name}}" title="{{= name}}"/>
			{{else}}
			<img class="primaryImage" id="primaryImage" src="{{= images[0].url}}" title="{{= images[0].name}}" alt="{{= images[0].name}}"/>
			{{/if}}
		</div>

		{{if !$.isEmptyObject(price)}}
		<span id="productPrice" class="mlist-price">{{= price.formattedValue || 0}}</span>
		{{/if}}

        {{if stock.stockLevelStatus.code=='outOfStock'}}
            <span class='listProductOutOfStock mlist-stock'><spring:theme code="product.variants.out.of.stock"/></span>
        {{else stock.stockLevelStatus.code=='lowStock' }}
            <span class='listProductLowStock mlist-stock'><spring:theme code="product.variants.only.left" arguments="{{= stock.stockLevel}}"/></span>
        {{/if}}

		<span class="mlist-rating">
			<span class="stars large" style="display: inherit;">
				<span style="width: {{= (averageRating > 0 ? averageRating: 0) * 17 }}px;"></span>
			</span>
			{{if !$.isEmptyObject(numberOfReviews)}}
			<div class="numberOfReviews">( {{= numberOfReviews}} )</div>
			{{/if}}
		</span>
	</a>
</script>
