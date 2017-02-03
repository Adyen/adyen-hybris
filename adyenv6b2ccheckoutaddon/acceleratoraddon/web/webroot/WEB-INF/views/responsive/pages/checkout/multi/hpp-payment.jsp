<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Adyen Hosted Payment Pages</title>
	</head>
	<body onload="document.getElementById('hppForm').submit();">
		<form method="POST" action="${hppUrl}" id="hppForm">
			<c:forEach items="${hppFormData}" var="entry">
				<input type="hidden" name="${entry.key}" value="${entry.value}" />
			</c:forEach>
			<noscript>
				<br>
				<br>
				<div style="text-align: center">
					<h1>Processing your Transaction</h1>
					<p>Please click continue to continue the processing of your transaction.</p>
					<input type="submit" class="button" value="continue"/>
				</div>
			</noscript>
		</form>
	</body>
</html>