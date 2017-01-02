<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>

<script id="resultsListItemsTemplate" type="text/x-jquery-tmpl">
	{{each(i, result) $data.results}}
	<li class="mlist-listItem" data-theme="d">
		<a href="<c:url value="/" />/{{= result.url}}">
			<div class="ui-grid-a">
				<div class="ui-block-a">
					{{tmpl(result) "#resultsListItemImageTemplate"}}
				</div>
				<div class="ui-block-b">
					<h2>{{= result.name}}</h2>
					{{if !$.isEmptyObject(result.price)}}
					<span class="mlist-price">{{= result.price.formattedValue || 0}}</span>
					{{/if}}

                    {{if result.stock.stockLevelStatus.code=='outOfStock'}}
                    <span class='listProductOutOfStock mlist-stock'><spring:theme code="product.variants.out.of.stock"/></span>
                    {{/if}}
                    {{if result.stock.stockLevelStatus.code=='lowStock' }}
                    <span class='listProductLowStock mlist-stock'><spring:theme code="product.variants.only.left" arguments="{{= result.stock.stockLevel}}"/></span>
                    {{/if}}

					<span class="mlist-rating">{{tmpl(result) "#resultsListItemProductRatingTemplate"}}</span>
				</div>
			</div>
		</a>
	</li>
	{{/each}}
</script>
<script id="resultsListItemImageTemplate" type="text/x-jquery-tmpl">
	<div class="prod_image_main">
		{{if $.isEmptyObject(images)}}
		<theme:image code="img.missingProductImage.thumbnail" alt="{{= name}}" title="{{= name}}"/>
		{{else}}
		<img class="primaryImage" id="primaryImage" src="{{= images[0].url}}" title="{{= images[0].name}}" alt="{{= images[0].name}}"/>
		{{/if}}
	</div>
</script>
<script id="resultsListItemProductRatingTemplate" type="text/x-jquery-tmpl">
	<div>
		<span class="stars large" style="display: inherit;">
			<span style="width: {{= (averageRating > 0 ? averageRating: 0) * 17 }}px;"></span>
		</span>
		{{if !$.isEmptyObject(numberOfReviews)}}
		<div class="numberOfReviews">( {{= numberOfReviews}} )</div>
		{{/if}}
	</div>
</script>
