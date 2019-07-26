<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>

    <script type="text/javascript" src="https://${checkoutShopperHost}/checkoutshopper/sdk/3.0.0/adyen.js"></script>
    <link rel="stylesheet" href="https://${checkoutShopperHost}/checkoutshopper/sdk/3.0.0/adyen.css"/>

    <script type="text/javascript">

        function initiateCheckout ( locale, environmentMode ) {
            var configuration = {
                locale: locale,// shopper's locale
                environment: environmentMode, //test or live
                risk: {
                    enabled: false
                }
            };
            this.checkout = new AdyenCheckout( configuration );
        }

        function perform3DS2Operations () {
            initiateCheckout( "${shopperLocale}", "${environmentMode}" );
            var challengeToken = "${challengeToken}";
            var fingerprintToken = "${fingerprintToken}";
            if ( challengeToken ) {
                initiate3DS2ChallengeShopper( challengeToken );
            } else {
                if ( fingerprintToken ) {
                    initiate3DS2IdentifyShopper( fingerprintToken );
                }
            }
        }

        function initiate3DS2IdentifyShopper ( fingerprintToken ) {
            var threeDS2IdentifyShopperNode = document.getElementById( 'threeDS2' );

            var identifyShopperComponent = this.checkout.create( 'threeDS2DeviceFingerprint', {
                fingerprintToken: fingerprintToken,
                onComplete: function ( fingerprintData ) {
                    fingerprintResult = fingerprintData.data.details[ "threeds2.fingerprint" ];

                    var fingerprintResultField = document.getElementById( 'fingerprintResult' );
                    fingerprintResultField.value = fingerprintResult;
                    document.getElementById( "3ds2-form" ).submit();

                }, // Gets triggered whenever the ThreeDS2 component has a result

                onError: function ( error ) {
                    //alert( "Payment Refused with error ["+ error.errorCode +"] and reason is "+ error.message );
                    console.log(JSON.stringify(error));
                } // Gets triggered on error
            } );
            identifyShopperComponent.mount( threeDS2IdentifyShopperNode );
        }

        function initiate3DS2ChallengeShopper ( challengeToken ) {
            var threeDS2ChallengeShopperNode = document.getElementById( 'threeDS2' );
            var challengeShopperComponent = this.checkout
                .create( 'threeDS2Challenge', {
                    challengeToken: challengeToken,
                    onComplete: function ( challengeData ) {
                        challengeResult = challengeData.data.details[ "threeds2.challengeResult" ];
                        var challengeResultField = document.getElementById( 'challengeResult' );
                        challengeResultField.value = challengeResult;
                        document.getElementById( "3ds2-form" ).submit();
                    },
                    onError: function ( error ) {
                        console.log(JSON.stringify(error));
//                        alert( "Payment Refused with error ["+ error.errorCode +"] and reason is "+ error.message );

                    }, // Gets triggered on error
                    size: '05' // Defaults to '01'
                } );
            challengeShopperComponent.mount( threeDS2ChallengeShopperNode );
        }
    </script>

</head>
<body onload=perform3DS2Operations()>
<form method="post"
      class="create_update_payment_form"
      id="3ds2-form"
      action="3ds2-adyen-response">
    <div class="row">
        <div class="col-sm-6">
            <div id="threeDS2"></div>
            <input type="hidden" name="challengeToken" value="${challengeToken}"/>
            <input type="hidden" name="fingerprintToken" value="${fingerprintToken}"/>
            <input type="hidden" id="fingerprintResult" name="fingerprintResult"/>
            <input type="hidden" id="challengeResult" name="challengeResult"/>
        </div>
    </div>
</form>
</body>
</html>