<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

	<c:if test="${expressCheckoutAllowed}">
		<div class="expressCheckoutCheckbox clearfix right ui-grid-a">
			<div class="ui-block-a">
			<label for="expressCheckoutCheckbox" >
				<input id="expressCheckoutCheckbox" name="expressCheckoutEnabled" type="checkbox" data-express-checkout-url="<c:url value='/checkout/multi/express'/>" class="doExpressCheckout"/>
				<spring:theme code="cart.expresscheckout.checkbox"/>
			</label>
			</div>
			<div class="expressCheckoutHelp ui-block-b">
	            <a href="#" id='expressCheckoutHelpLink'/>
	            	<spring:theme code="text.help"/>
	            </a>
	        </div>
		</div>


		

        <div class="hidden">
            <div class="item_container_holder" id="checkoutInfo">
                <div class="expressCheckoutHelpContainer">
    					<p><spring:theme text="" code="text.expresscheckout.title"/></p>
						<ul type="square">
							<li><spring:theme code="text.expresscheckout.line1"/></li>
	       					<li><spring:theme code="text.expresscheckout.line2"/></li>
          					<li><spring:theme code="text.expresscheckout.line3"/></li>
        				</ul>
						<p><spring:theme code="text.expresscheckout.info1"/></p>
						<p><spring:theme code="text.expresscheckout.info2"/></p>
						<p><spring:theme code="text.expresscheckout.info3"/></p>
    			</div>
    		</div>
        </div>
	</c:if>