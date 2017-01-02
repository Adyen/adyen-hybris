<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Adyen 3D Secure Payment</title>
	</head>
	<%--<body>--%>
	<body onload="document.getElementById('3dform').submit();">
		<form method="POST" action="${issuerUrl}" id="3dform">
			<input type="hidden" name="PaReq" value="${paReq}" />
			<input type="hidden" name="MD" value="${md}" />
			<input type="hidden" name="TermUrl" value="${termUrl}" />
			<noscript>
				<br>
				<br>
				<div style="text-align: center">
					<h1>Processing your 3-D Secure Transaction</h1>
					<p>Please click continue to continue the processing of your 3-D Secure transaction.</p>
					<input type="submit" class="button" value="continue"/>
				</div>
			</noscript>
		</form>
	</body>
</html>