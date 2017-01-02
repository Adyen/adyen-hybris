<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- JS configuration --%>
	<script type="text/javascript">
		/*<![CDATA[*/
		<%-- Define a javascript variable to hold the content path --%>
		var ACC = { config: {} };
			ACC.config.contextPath = "${contextPath}";
			ACC.config.commonResourcePath = "${commonResourcePath}";
			ACC.config.themeResourcePath = "${themeResourcePath}";
			ACC.config.siteResourcePath = "${siteResourcePath}";
			ACC.config.language = "${language}";
			ACC.config.rootPath = "${siteRootUrl}";
			ACC.config.CSRFToken = "${CSRFToken}";
			ACC.pwdStrengthVeryWeak = '<spring:theme code="password.strength.veryweak" />';
			ACC.pwdStrengthWeak = '<spring:theme code="password.strength.weak" />';
			ACC.pwdStrengthMedium = '<spring:theme code="password.strength.medium" />';
			ACC.pwdStrengthStrong = '<spring:theme code="password.strength.strong" />';
			ACC.pwdStrengthVeryStrong = '<spring:theme code="password.strength.verystrong" />';
			ACC.pwdStrengthUnsafePwd = '<spring:theme code="password.strength.unsafepwd" />';
			ACC.pwdStrengthTooShortPwd = '<spring:theme code="password.strength.tooshortpwd" />';
			ACC.pwdStrengthMinCharText = '<spring:theme code="password.strength.minchartext"/>';
			ACC.accessibilityLoading = '<spring:theme code="aria.pickupinstore.loading"/>';
			ACC.accessibilityStoresLoaded = '<spring:theme code="aria.pickupinstore.storesloaded"/>';

			<c:forEach var="jsVar" items="${jsVariables}">
				<c:if test="${not empty jsVar.qualifier}" >
					ACC.${jsVar.qualifier} = '${jsVar.value}';
				</c:if>
			</c:forEach>
		/*]]>*/
	</script>
