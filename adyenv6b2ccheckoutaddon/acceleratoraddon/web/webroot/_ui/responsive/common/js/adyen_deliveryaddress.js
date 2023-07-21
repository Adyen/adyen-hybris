$( document ).ready( function () {

    var spinner = $( "<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />" );

    function bindCountrySpecificAddressForms  (){
        $(document).on("change",'#adyenCountrySelector select', function (){
            var options = {
                'addressCode': '',
                'countryIsoCode': $(this).val()
            };
            displayCountrySpecificAddressForm(options, showAddressFormButtonPanel);
        });

    }

    function showAddressFormButtonPanel () {
        if ($('#adyenCountrySelector :input').val() !== '')
        {
            $('#addressform_button_panel').show();
        }
    }

    function displayCountrySpecificAddressForm  (options, callback)
    {
        $.ajax({
            url: ACC.config.encodedContextPath + '/adyen/my-account/addressform',
            async: true,
            data: options,
            dataType: "html",
            beforeSend: function ()
            {
                $("#i18nAddressForm").html(spinner);
            }
        }).done(function (data)
        {
            $("#i18nAddressForm").html($(data).html());
            if (typeof callback == 'function')
            {
                callback.call();
            }

            for (const phoneInputPhoneNumber of document.querySelectorAll('input[type=text][name="billingAddress.phoneNumber"]')) {
                phoneInputPhoneNumber.addEventListener('change', phoneFormatter, false);
                phoneInputPhoneNumber.addEventListener('blur', phoneFormatter, false);
            }

            for (const phoneInput of document.querySelectorAll('input[type=text][name=phone]')) {
                phoneInput.addEventListener('change', phoneFormatter, false);
                phoneInput.addEventListener('blur', phoneFormatter, false);
            }
        });
    }

    bindCountrySpecificAddressForms();

    function phoneFormatter(event) {
        const input = event.target;
        let rawValue = input.value;
        if (rawValue && rawValue.length) {
            const prefix = rawValue[0] === '+' ? '+' : '';
            input.value = prefix + input.value.replace(/[^0-9]/g, "");
        }
    }

} );
