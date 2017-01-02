<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>


<%-- j query 1.5.1 --%>
<script type="text/javascript" src="${commonResourcePath}/js/jquery-1.7.2.min.js"></script>

<template:javaScriptVariables/>

<%-- j query query 2.1.7 --%>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.query-2.1.7.js"></script>
<%-- jquery tabs dependencies --%>
<script type="text/javascript" src="${commonResourcePath}/js/jquery-ui-1.8.18.min.js"></script>
<%-- j carousel --%>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.jcarousel-0.2.8.min.js"></script>
<%-- j query templates --%>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.tmpl-1.0.0pre.min.js"></script>
<%-- j query block UI --%>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.blockUI-2.39.js"></script>
<%-- colorbox --%>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.colorbox.custom-1.3.16.js"></script>
<%-- Slide Viewer --%>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.slideviewer.custom.1.2.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.easing.1.3.js"></script>
<%-- Wait for images --%>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.waitforimages.min.js"></script>
<%-- Scroll to --%>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.scrollTo-1.4.2-min.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.ui.stars-3.0.1.min.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.form-3.09.js"></script>
<%-- BeautyTips  --%>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.bgiframe-2.1.2.min.js"></script>
<!--[if IE]><script type="text/javascript" src="${commonResourcePath}/js/excanvas-r3.compiled.js"></script>-->
<script type="text/javascript" src="${commonResourcePath}/js/jquery.bt-0.9.5-rc1.min.js"></script>
<%-- PasswordStrength  --%>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.pstrength.custom-1.2.0.js"></script>




<%-- Custom ACC JS --%>
<script type="text/javascript" src="${commonResourcePath}/js/acc.common.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.userlocation.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.track.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.cms.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.product.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.refinements.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.storefinder.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.carousel.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.autocomplete.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.pstrength.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.password.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.minicart.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.pickupinstore.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.userlocation.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.langcurrencyselector.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.paginationsort.js"></script>

<script type="text/javascript" src="${commonResourcePath}/js/acc.checkout.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.cartremoveitem.js"></script>

<script type="text/javascript" src="${commonResourcePath}/js/acc.address.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.refresh.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.stars.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.forgotpassword.js"></script>


<%-- accessible-tabs  --%>
<script type="text/javascript" src="${commonResourcePath}/js/jquery.accessible-tabs-1.9.7.min.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.productDetail.js"></script>
<script type="text/javascript" src="${commonResourcePath}/js/acc.producttabs.js"></script>	


<%-- AddOn JavaScript files --%>
<c:forEach items="${addOnJavaScriptPaths}" var="addOnJavaScript">
    <script type="text/javascript" src="${addOnJavaScript}"></script>
</c:forEach>

<%-- Fix for Webkit Browsers (Needs to be loaded last)  --%>
<script type="text/javascript" src="${commonResourcePath}/js/acc.skiplinks.js"></script>
