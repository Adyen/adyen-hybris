var AdyenCheckoutHybris = (function () {
    'use strict';

    var CardBrand = {
        Visa: 'visa',
        MasterCard: 'mc',
        Elo: 'elo',
        Maestro: 'maestro',
        Electron: 'electron',
        EloDebit: 'elodebit'
    };

    return {
        oneClickForms: [],
        dobDateFormat: "yy-mm-dd",
        securedFieldsData: {},
        allowedCards: [],
        cseEncryptedForm: null,
        checkout: null,
        card: null,
        oneClickCards: {},
        selectedCardBrand: null,

        convertCardBrand: function () {
            var cardBrand = this.selectedCardBrand;

            if ( cardBrand === CardBrand.Visa ) {
                return CardBrand.Electron;
            }

            if ( cardBrand === CardBrand.MasterCard ) {
                return CardBrand.Maestro;
            }
            if ( cardBrand === CardBrand.Elo ) {
                return CardBrand.EloDebit;
            }
        },

        isValidBrandType: function () {
            var cardBrand = this.selectedCardBrand;
            return cardBrand === CardBrand.Visa || cardBrand === CardBrand.MasterCard || cardBrand === CardBrand.Elo;
        },

        getCardType: function () {
            return $( '#adyen_combo_card_type' ).val();
        },

        isDebitCard: function () {
            return this.getCardType() === 'debit';
        },

        validateForm: function () {
            var paymentMethod = $( 'input[type=radio][name=paymentMethod]:checked' ).val();

            if ( paymentMethod === "" ) {
                window.alert( "Please select a payment method" );
                return false;
            }

            // Check if it is a valid card and encrypt
            if ( paymentMethod === "adyen_cc" ) {
                var isInvalidCard = !this.card.isValid ||
                    (this.isDebitCard() && !this.isValidBrandType());

                if ( isInvalidCard ) {
                    window.alert( 'This ' + this.getCardType() + ' card is not allowed' );
                    return false;
                }
                this.copyCardData();
            }

            if (paymentMethod.indexOf( "adyen_oneclick_" ) === 0) {
                var recurringReference = paymentMethod.slice("adyen_oneclick_".length);
                var oneClickCard = this.oneClickCards[recurringReference];

                if (!(oneClickCard && oneClickCard.isValid)) {
                    window.alert('This credit card is not allowed');
                    return false;
                }
                if ( (oneClickCard.props.brand == "bcmc") ) {
                    this.copyOneClickCardDataBCMC( recurringReference )
                }
                else {
                    this.copyOneClickCardData( recurringReference, oneClickCard.data.paymentMethod.encryptedSecurityCode );
                }
            }

            if ( ['eps','ideal'].includes(paymentMethod) ) {
                var issuerIdField = document.getElementById('issuerId');
                if( issuerIdField.value === "" ) {
                    window.alert("Please select an issuer");
                    return false;
                }
            }

            if ( paymentMethod === "pos" ) {
                var terminalId = $( '#adyen_pos_terminal' );
                if( terminalId.val() === "" ) {
                    window.alert("Please select a terminal");
                    return false;
                }
            }

            $( 'input[name="txvariant"]' ).remove();

            return true;
        },

        copyCardData: function() {
            var state = this.card.data.paymentMethod;
            $( 'input[name="encryptedCardNumber"]' ).val( state.encryptedCardNumber );
            $( 'input[name="encryptedExpiryMonth"]' ).val( state.encryptedExpiryMonth );
            $( 'input[name="encryptedExpiryYear"]' ).val( state.encryptedExpiryYear );
            $( 'input[name="encryptedSecurityCode"]' ).val( state.encryptedSecurityCode );
            $( 'input[name="cardHolder"]' ).val( state.holderName );
            if(this.card.data.storePaymentMethod!=null){
            $( 'input[name="rememberTheseDetails"]' ).val( this.card.data.storePaymentMethod );}

            if ( this.isDebitCard() ) {
                $( 'input[name="cardBrand"]' ).val( this.convertCardBrand() );
                $( 'input[name="cardType"]' ).val( this.getCardType() );
            }
                 $( 'input[name="browserInfo"]' ).val( JSON.stringify( this.card.data.browserInfo ) );
        },

        copyOneClickCardData: function ( recurringReference, cvc ) {
            $( "#selectedReference" ).val( recurringReference );
            $( 'input[name="encryptedSecurityCode"]' ).val( cvc );
            $( 'input[name="browserInfo"]' ).val( JSON.stringify( this.card.data.browserInfo ) );

        },
        copyOneClickCardDataBCMC: function ( recurringReference ) {
            $( "#selectedReference" ).val( recurringReference );
            $( 'input[name="cardBrand"]' ).val( "bcmc" );
            $( 'input[name="browserInfo"]' ).val( JSON.stringify( this.card.data.browserInfo ) );
        },

        /**
         * Set Custom values for certain payment methods
         */
        setCustomPaymentMethodValues: function () {
            var paymentMethod = $( 'input[type=radio][name=paymentMethod]:checked' ).val();
            var dob = $( '#p_method_adyen_hpp_' + paymentMethod + '_dob' );
            if ( dob ) {
                $( "#dob" ).val( dob.val() );
            }

            var ssn = $( '#p_method_adyen_hpp_' + paymentMethod + '_ssn' );
            if ( ssn ) {
                $( "#socialSecurityNumber" ).val( ssn.val() );
            }

            var terminalId = $( '#adyen_pos_terminal' );
            if ( terminalId ) {
                $( "#terminalId" ).val( terminalId.val() );
            }
        },

        /**
         * Create DatePicker for Date of Birth field
         *
         * @param element
         */
        createDobDatePicker: function ( element ) {
            $( "." + element ).datepicker( {
                dateFormat: this.dobDateFormat,
                changeMonth: true,
                changeYear: true,
                yearRange: "-120:+0"
            } );
        },


        togglePaymentMethod: function ( paymentMethod ) {
            $( ".payment_method_details" ).hide();
            $( ".chckt-pm__details" ).hide();

            $( "#dd_method_" + paymentMethod ).show();
            $( "#adyen_hpp_" + paymentMethod + "_container" ).show();
        },

        createDfValue: function () {
            window.dfDo( "dfValue" );
        },
        initiateCheckout: function ( locale, environment, originKey ) {
            var configuration = {
                locale: locale,// shopper's locale
                environment: environment,
                originKey: originKey,
                risk: {
                    enabled: false
                }
            };
            this.checkout = new AdyenCheckout( configuration );
        },


    initiateOneClickCard: function(storedCard) {
            var oneClickCardNode = document.getElementById("one-click-card_" + storedCard.storedPaymentMethodId);
            var oneClickCard = this.checkout.create('card', storedCard);
            oneClickCard.mount(oneClickCardNode);
            this.oneClickCards[storedCard.storedPaymentMethodId] = oneClickCard;
        },

        initiateCard: function (allowedCards) {
            var context = this;
            this.card = this.checkout.create( 'card', {
                type: 'card',
                hasHolderName: true,
                holderNameRequired: true,
                enableStoreDetails: true,
                groupTypes: allowedCards,
                onBrand: copyCardBrand

            });

            function copyCardBrand(event) {
                context.selectedCardBrand = event.brand;
            }

            this.card.mount(document.getElementById('card-div'));
        },

        initiateGenericCard: function (locale, originKey) {
            var testResponse = JSON.stringify({"groups":[{"name":"Credit Card","types":["mc","visa","elo","amex","hipercard","maestro","diners","discover","cup","hiper","jcb"]}],"paymentMethods":[{"brands":["mc","visa","elo","amex","hipercard","maestro","diners","discover","cup","hiper","jcb"],"details":[{"key":"number","type":"text"},{"key":"expiryMonth","type":"text"},{"key":"expiryYear","type":"text"},{"key":"cvc","type":"text"},{"key":"holderName","optional":true,"type":"text"}],"name":"Credit Card","type":"scheme"},{"name":"Boleto","supportsRecurring":true,"type":"boleto"},{"name":"Boleto Bancario","supportsRecurring":true,"type":"boletobancario"},{"details":[{"key":"number","type":"text"},{"key":"expiryMonth","type":"text"},{"key":"expiryYear","type":"text"},{"key":"cvc","type":"text"},{"key":"holderName","optional":true,"type":"text"},{"key":"telephoneNumber","optional":true,"type":"text"}],"name":"ExpressPay","supportsRecurring":true,"type":"cup"},{"name":"Direct Debit Brazil - Bradesco","supportsRecurring":true,"type":"directdebit_BR_bradesco"},{"name":"Mercado Pago","supportsRecurring":true,"type":"mercadopago"},{"details":[{"key":"paywithgoogle.token","type":"payWithGoogleToken"}],"name":"Google Pay","supportsRecurring":true,"type":"paywithgoogle"},{"name":"UnionPay","supportsRecurring":true,"type":"unionpay"}]});
            var cardConfiguration = JSON.stringify({
                card: {
                    type: 'card',
                    hasHolderName: true,
                    holderNameRequired: true,
                    enableStoreDetails: true
                },
            });
            var node = `
                <adyen-checkout
                    origin-key="${originKey}"
                    locale="${locale}"
                    environment="test"
                    payment-methods='${testResponse}'
                   payment-methods-configuration='${cardConfiguration}'        
              >
                    <adyen-payment-method-card></adyen-payment-method-card>
                </adyen-checkout>
            `;
            $('#generic-component-div').append(node);
            var cardComponent = document.querySelector('adyen-payment-method-card');

            cardComponent.addEventListener('adyenChange', (function (event) {
                this.card = event.detail.state;
                console.log(this.card);
            }).bind(this))

        },

        initiateIdeal: function (idealDetails) {
            var idealNode = document.getElementById('adyen_hpp_ideal_container');
            var ideal = this.checkout.create('ideal', {
                details: idealDetails, // The array of issuers coming from the /paymentMethods api call
                showImage: true, // Optional, defaults to true
                onChange: handleChange // Gets triggered whenever a user selects a bank// Gets triggered once the state is valid
            });

            function handleChange(event) {
                var issuerIdField = document.getElementById('issuerId');
                var issuerId = event.data.paymentMethod.issuer;
                issuerIdField.value = issuerId;
            }

            try {
                ideal.mount(idealNode);
            } catch (e) {
                console.log('Something went wrong trying to mount the iDEAL component: ${e}');
            }
        },

        initiateEps: function (epsDetails) {
            var epsNode = document.getElementById('adyen_hpp_eps_container');
            var eps = this.checkout.create('eps', {
                details: epsDetails, // The array of issuers coming from the /paymentMethods api call
                showImage: true, // Optional, defaults to true
                onChange: handleOnChange // Gets triggered once the shopper selects an issuer
            });

            function handleOnChange(event) {
                var issuerIdField = document.getElementById('issuerId');
                var issuerId = event.data.paymentMethod.issuer;
                issuerIdField.value = issuerId;
            }

            try {
                eps.mount(epsNode);
            } catch (e) {
                console.log('Something went wrong trying to mount the EPS component: ${e}');
            }
        },

        showSpinner: function () {
            document.getElementById("spinner_wrapper").style.display = "flex";
        }
    };
})();
