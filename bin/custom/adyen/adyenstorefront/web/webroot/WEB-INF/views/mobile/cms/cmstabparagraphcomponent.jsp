<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div data-role="collapsible" data-theme="e" data-content-theme="c" data-collapsed="true">
	<h3>${component.title}</h3>
	<div class="prod_content" id="tab-${component.uid}">${component.content}</div>
</div>
