<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/desktop/common/header"  %>

<ul class="languagecurrencycomponent">
	<li><header:languageSelector languages="${languages}" currentLanguage="${currentLanguage}" /></li>
	<li><header:currencySelector currencies="${currencies}" currentCurrency="${currentCurrency}" /></li>
</ul>

