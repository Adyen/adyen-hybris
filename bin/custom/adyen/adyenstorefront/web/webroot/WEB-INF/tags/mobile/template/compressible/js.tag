<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="cms" tagdir="/WEB-INF/tags/mobile/template/cms" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/mobile/template" %>

<%-- jQuery --%>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.1.7.1.min.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.tmpl.1.0.0pre.min.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/jquery-ui-1.11.2.min.js"></script>

<script type="text/javascript">
	/*<![CDATA[*/
	$(document).bind("mobileinit", function() {
		$.mobile.ajaxEnabled = false;
		$.mobile.ajaxLinksEnabled = false;
		$.mobile.selectmenu.prototype.options.nativeMenu = true;
		$.mobile.defaultPageTransition = "none";
		$.mobile.defaultDialogTransition = "none";
		$.mobile.minScrollBack = 250;
		$.mobile.loadingMessageTheme = "f";
	});
	/*]]>*/
</script>

<script type="text/javascript" src="${commonResourcePath}/js/jquery.mobile-1.3.0.min.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.mobile.easydialog.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.mobile.autocomplete.custom.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.mobile.collapsiblelistview.hybris.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.ui.stars.min.3.0.1.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.form.2.67.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.colorbox-1.3.16.js"></script>

<script type="text/javascript" src="${commonResourcePath}/js/jquery.imagesloaded.min.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.cycle.all.2.9999.5.js"></script>

<%-- ACCMOB --%>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.common.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.menu.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.autocomplete.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.address.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.facets.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.account.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.forgotpassword.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.cart.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.cartremoveitem.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.pickupinstore.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.storelisting.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.product.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.userlocation.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.productlisting.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/waypoints.min.1.1.5.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.placeorder.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.messages.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.hopdebug.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.pstrength.custom-1.2.0.js"></script>

<script type="text/javascript">
    /*<![CDATA[*/
    $(function() {
        $('.strength').pstrength({ verdicts:["<spring:theme code="password.strength.veryweak" />",
                                             "<spring:theme code="password.strength.weak" />",
                                             "<spring:theme code="password.strength.medium" />",
                                             "<spring:theme code="password.strength.strong" />",
                                             "<spring:theme code="password.strength.verystrong" />"],
                                   tooShort: "<spring:theme code="password.strength.tooshortpwd" />",
                                   minCharText: "<spring:theme code="password.strength.minchartext"/>" });
    });
    /*]]>*/
</script>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.password.js"></script>

<%-- AddOn JavaScript files --%>
<c:forEach items="${addOnJavaScriptPaths}" var="addOnJavaScript">
    <script type="text/javascript" src="${addOnJavaScript}"></script>
</c:forEach>

<%-- AddOn JavaScript files --%>
<c:forEach items="${addOnJavaScriptPaths}" var="addOnJavaScript">
    <script type="text/javascript" src="${addOnJavaScript}"></script>
</c:forEach>

<%-- Fix for SkipLinks (Needs to be loaded last)  --%>
<script type="text/javascript" src="${commonResourcePath}/js/accmob.skiplinks.js"></script>

<script type="text/javascript" src="${commonResourcePath}/js/jquery.ui.touch-punch.min.0.2.2.js"></script>
