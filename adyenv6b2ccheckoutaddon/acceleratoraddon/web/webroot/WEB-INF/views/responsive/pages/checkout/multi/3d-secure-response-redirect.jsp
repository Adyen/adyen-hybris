<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:url value="/checkout/multi/adyen/summary/authorise-3d-adyen-response" var="redirect3dsResponse"/>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Adyen 3D Secure Response</title>
	</head>

	<body onload="document.getElementById('redirect3dsResponseForm').submit();">
		<form method="POST" action="${redirect3dsResponse}" id="redirect3dsResponseForm">
			<input type="hidden" name="PaRes" value="${PaRes}" />
			<input type="hidden" name="MD" value="${MD}" />
			<noscript>
				<div style="text-align: center">
					<h1>Processing your 3-D Secure Transaction Response</h1>
					<p>Please click continue to continue the processing of your 3-D Secure transaction.</p>
					<input type="submit" class="button" value="continue"/>
				</div>
			</noscript>
		</form>
	</body>

</html>