<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>

<!-- Verified that there's a pre-existing bug regarding the setting of showTax; created issue  -->
<cart:cartTotals cartData="${cartData}" showTax="false"/>
