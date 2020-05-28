$( document ).ready( function () {

    var spinner = $( "<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />" );

    function binduseAdyenDeliveryAddress () {
        $( '#useAdyenDeliveryAddress' ).on( 'change', function () {
            if ( $( '#useAdyenDeliveryAddress' ).is( ":checked" ) ) {
                var options = {'countryIsoCode': $( '#useAdyenDeliveryAddressData' ).data( 'countryisocode' ), 'useAdyenDeliveryAddress': true};
                enableAddressForm();
                displayAdyenCreditCardAddressForm( options, onDeliveryAddressToggle );
                disableAddressForm();
            } else {
                clearAddressForm();
                enableAddressForm();
            }
        } );

        if ( $( '#useAdyenDeliveryAddress' ).is( ":checked" ) ) {
            var options = {'countryIsoCode': $( '#useAdyenDeliveryAddressData' ).data( 'countryisocode' ), 'useAdyenDeliveryAddress': true};
            enableAddressForm();
            displayAdyenCreditCardAddressForm( options, onDeliveryAddressToggle );
            disableAddressForm();
        }
    }

    function bindSubmitSilentOrderPostForm () {
        $( '.submit_silentOrderPostForm' ).click( function () {
            ACC.common.blockFormAndShowProcessingMessage( $( this ) );
            $( '.adyenBillingAddressForm' ).filter( ":hidden" ).remove();
            enableAddressForm();
            $( '#silentOrderPostForm' ).submit();
        } );
    }

    function disableAddressForm () {
        $( 'input[id^="address\\."]' ).prop( 'disabled', true );
        $( 'select[id^="address\\."]' ).prop( 'disabled', true );
    }

    function enableAddressForm () {
        $( 'input[id^="address\\."]' ).prop( 'disabled', false );
        $( 'select[id^="address\\."]' ).prop( 'disabled', false );
    }

    function clearAddressForm () {
        $( 'input[id^="address\\."]' ).val( "" );
        $( 'select[id^="address\\."]' ).val( "" );
    }

    function onDeliveryAddressToggle () {
        if ( $( '#useAdyenDeliveryAddress' ).is( ":checked" ) ) {
            $( '#address\\.country' ).val( $( '#useAdyenDeliveryAddressData' ).data( 'countryisocode' ) );
            disableAddressForm();
        } else {
            clearAddressForm();
            enableAddressForm();
        }
    }

    function bindCreditCardAddressForm () {
        $( '#billingAdyenCountrySelector :input' ).on( "change", function () {
            var countrySelection = $( this ).val();
            var options = {
                'countryIsoCode': countrySelection,
                'useAdyenDeliveryAddress': false
            };
            displayAdyenCreditCardAddressForm( options );
        } )
    }

    function displayAdyenCreditCardAddressForm ( options, callback ) {
        const form = $( "#adyenBillingAddressForm" );
        $.ajax( {
            url: ACC.config.encodedContextPath + '/checkout/multi/adyen/select-payment-method/billingaddressform',
            async: true,
            data: options,
            dataType: "html",
            beforeSend: function () {
                form.html( spinner );
            }
        } ).done( function ( data ) {
            form.html( data );
            if ( typeof callback === 'function' ) {
                callback();
            }
        } );
    }

    binduseAdyenDeliveryAddress();
    bindSubmitSilentOrderPostForm();
    bindCreditCardAddressForm();

} );
