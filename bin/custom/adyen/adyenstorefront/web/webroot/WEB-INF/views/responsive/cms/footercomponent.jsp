<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/responsive/common/footer"  %>

<div class="container">
	<div class="row">
		<c:forEach items="${navigationNodes}" var="node">
			<c:if test="${node.visible}">
				<c:forEach items="${node.links}" step="${component.wrapAfter}"
					varStatus="i">

					<div class="links">
						<c:if test="${component.wrapAfter > i.index}">
							<div class="title">${node.title}</div>
						</c:if>
						<ul>
							<c:forEach items="${node.links}" var="childlink"
								begin="${i.index}" end="${i.index + component.wrapAfter - 1}">
								<cms:component component="${childlink}"
									evaluateRestriction="true" element="li" />
							</c:forEach>
						</ul>

					</div>
				</c:forEach>
			</c:if>
		</c:forEach>
	</div>


	<c:if test="${showLanguageCurrency}">
		<div class="pull-right">
			<footer:languageSelector languages="${languages}"
				currentLanguage="${currentLanguage}" />
			<footer:currencySelector currencies="${currencies}"
				currentCurrency="${currentCurrency}" />
		</div>
	</c:if>

</div>

<div class="copyright">
	<div class="container">${notice}</div>
</div>