<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<body>
		<form:form method="post" commandName="3DS2Form"
				   class="create_update_payment_form"
				   id="3ds2-form">
			<div class="row">
			<div class="col-sm-6">

			<div id="threeDS2"></div>
				<input type="text" name="ResultObject" value="${resultObject}" />
				<input type="text" name="paymentData" value="${paymentData}" />

			<h1>Hello you are on 3DS2 page</h1>

		</form:form>




			</div>
	</div>
</body>
</html>



