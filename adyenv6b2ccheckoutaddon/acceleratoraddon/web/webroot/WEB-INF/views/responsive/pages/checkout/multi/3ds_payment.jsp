<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <script src="https://${checkoutShopperHost}/checkoutshopper/sdk/4.3.1/adyen.js"
            integrity="sha384-eNk32fgfYxvzNLyV19j4SLSHPQdLNR+iUS1t/D7rO4gwvbHrj6y77oJLZI7ikzBH"
            crossorigin="anonymous"></script>
    <link rel="stylesheet"
          href="https://${checkoutShopperHost}/checkoutshopper/sdk/4.3.1/adyen.css"
          integrity="sha384-5CDvDZiVPuf+3ZID0lh0aaUHAeky3/ACF1YAKzPbn3GEmzWgO53gP6stiYHWIdpB"
          crossorigin="anonymous"/>

    <script type="text/javascript">
        function initiateCheckout ( locale, environmentMode, clientKey ) {
            var configuration = {
                clientKey: clientKey,
                locale: locale,// shopper's locale
                environment: environmentMode, //test or live
                risk: {
                    enabled: false
                },
                onAdditionalDetails: this.handleOnAdditionalDetails
            };
            this.checkout = new AdyenCheckout( configuration );
        }

        function handleOnAdditionalDetails(state, component) {
            document.getElementById("details").value = JSON.stringify(state.data.details);
            document.getElementById("3ds2-form").submit();
        }

        function perform3DSOperations () {
            initiateCheckout( "${shopperLocale}", "${environmentMode}", "${clientKey}");
            var action = ${action};
            this.checkout.createFromAction(action).mount('#threeDS');
        }
    </script>
</head>
<body onload=perform3DSOperations()>
    <div id="threeDS"/>
    <form method="post"
          class="create_update_payment_form"
          id="3ds2-form"
          action="authorise-3d-adyen-response">
        <input type="hidden" id="details" name="details"/>
    </form>
</body>
</html>