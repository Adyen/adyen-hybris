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

    var ErrorMessages = {
        PaymentCancelled: 'checkout.error.authorization.payment.cancelled',
        PaymentError: 'checkout.error.authorization.payment.error',
        PaymentNotAvailable: 'checkout.summary.component.notavailable',
        TermsNotAccepted: 'checkout.error.terms.not.accepted'
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
        sepaDirectDebit: null,
        afterPay: null,
        bcmc: null,
        paypalButton: null,

        convertCardBrand: function () {
            var cardBrand = this.selectedCardBrand;

            if (cardBrand === CardBrand.Visa) {
                return CardBrand.Electron;
            }

            if (cardBrand === CardBrand.MasterCard) {
                return CardBrand.Maestro;
            }
            if (cardBrand === CardBrand.Elo) {
                return CardBrand.EloDebit;
            }
        },

        isValidBrandType: function () {
            var cardBrand = this.selectedCardBrand;
            return cardBrand === CardBrand.Visa || cardBrand === CardBrand.MasterCard || cardBrand === CardBrand.Elo;
        },

        getCardType: function () {
            var cardType = $('#adyen_combo_card_type').val();
            if (cardType === "" || cardType == undefined)
                cardType = "credit";
            return cardType;
        },

        isDebitCard: function () {
            return this.getCardType() === 'debit';
        },

        validateForm: function () {
            var paymentMethod = $('input[type=radio][name=paymentMethod]:checked').val();

            if (paymentMethod === "") {
                window.alert("Please select a payment method");
                return false;
            }

            // Check if it is a valid card and encrypt
            if (paymentMethod === "adyen_cc") {
                var isInvalidCard = !this.card.isValid ||
                    (this.isDebitCard() && !this.isValidBrandType());

                if (isInvalidCard) {
                    window.alert('Please check your card details.');
                    this.card.showValidation();
                    document.getElementById("card-div").scrollIntoView();
                    return false;
                }
                this.copyCardData();
            }

            if (paymentMethod.indexOf("adyen_oneclick_") === 0) {
                var recurringReference = paymentMethod.slice("adyen_oneclick_".length);
                var oneClickCard = this.oneClickCards[recurringReference];

                if (!(oneClickCard && oneClickCard.isValid)) {
                    window.alert('This credit card is not allowed');
                    return false;
                }
                if (['bcmc', 'maestro'].indexOf(oneClickCard.props.brand) >= 0) {
                    this.copyOneClickCardBrandData(recurringReference, oneClickCard.props.brand)
                } else {
                    this.copyOneClickCardData(recurringReference, oneClickCard.data.paymentMethod.encryptedSecurityCode, oneClickCard.props.brand);
                }
            }
            $('input[name="txvariant"]').remove();

            if (['eps', 'ideal','onlinebanking_IN', 'onlineBanking_PL'].indexOf(paymentMethod) >= 0) {
                var issuerIdField = document.getElementById('issuerId');
                if (issuerIdField.value === "") {
                    window.alert("Please select an issuer");
                    return false;
                }
            }

            if (paymentMethod === "pos") {
                var terminalId = $('#adyen_pos_terminal');
                if (terminalId.val() === "") {
                    window.alert("Please select a terminal");
                    return false;
                }
            }

            if (paymentMethod === "sepadirectdebit") {
                if (!this.sepaDirectDebit.state.isValid) {
                    window.alert("Invalid SEPA Owner Name and IBAN number");
                    return false;
                }
            }

            if (paymentMethod === "afterpay_default") {
                if (!this.afterPay.state.isValid) {
                    window.alert("Please fill all the details");
                    this.afterPay.showValidation();
                    document.getElementById("afterpay-container").scrollIntoView();
                    return false;
                }
                var dob = $("input[name=dateOfBirth]").val();
                if (dob) {
                    $("#dob").val(dob);
                }
            }

            if (paymentMethod === "paybright") {
                var phoneNumber = $("#p_method_adyen_hpp_paybright_telephonenumber").val();
                if (!phoneNumber) {
                    window.alert("Please fill phone number");
                    document.getElementById("p_method_adyen_hpp_paybright_telephonenumber").scrollIntoView();
                    return false;
                }
            }

            if (paymentMethod === "giftcard") {
                $('input[name="giftCardBrand"]').val($('input[type=radio][name=paymentMethod]:checked').attr('brand'));
            }

            if (paymentMethod === "bcmc") {
                if (!this.bcmc.state.isValid) {
                    window.alert("Please fill all the details");
                    this.bcmc.showValidation();
                    document.getElementById("bcmc-container").scrollIntoView();
                    return false;
                }
                var state = this.bcmc.data.paymentMethod;
                $('input[name="encryptedCardNumber"]').val(state.encryptedCardNumber);
                $('input[name="encryptedExpiryMonth"]').val(state.encryptedExpiryMonth);
                $('input[name="encryptedExpiryYear"]').val(state.encryptedExpiryYear);
                $('input[name="cardHolder"]').val(state.holderName);
                $('input[name="cardBrand"]').val('bcmc');
                $('input[name="cardType"]').val('debit');
                $('input[name="browserInfo"]').val(JSON.stringify(this.bcmc.data.browserInfo));
            }

            return true;
        },

        copyCardData: function () {
            var state = this.card.data.paymentMethod;
            $('input[name="encryptedCardNumber"]').val(state.encryptedCardNumber);
            $('input[name="encryptedExpiryMonth"]').val(state.encryptedExpiryMonth);
            $('input[name="encryptedExpiryYear"]').val(state.encryptedExpiryYear);
            $('input[name="encryptedSecurityCode"]').val(state.encryptedSecurityCode);
            $('input[name="cardHolder"]').val(state.holderName);
            if (this.card.data.storePaymentMethod != null) {
                $('input[name="rememberTheseDetails"]').val(this.card.data.storePaymentMethod);
            }

            if (this.isDebitCard()) {
                $('input[name="cardBrand"]').val(this.convertCardBrand());
                $('input[name="cardType"]').val(this.getCardType());
            } else {
                $('input[name="cardBrand"]').val(this.selectedCardBrand);
            }
            $('input[name="browserInfo"]').val(JSON.stringify(this.card.data.browserInfo));
        },

        copyOneClickCardData: function (recurringReference, cvc, brand) {
            $("#selectedReference").val(recurringReference);
            $('input[name="encryptedSecurityCode"]').val(cvc);
            $('input[name="browserInfo"]').val(JSON.stringify(this.card.data.browserInfo));
            if (brand) {
                $('input[name="cardBrand"]').val(brand);
            }

        },
        copyOneClickCardBrandData: function (recurringReference, brand) {
            $("#selectedReference").val(recurringReference);
            $('input[name="cardBrand"]').val(brand);
            $('input[name="browserInfo"]').val(JSON.stringify(this.card.data.browserInfo));
        },

        /**
         * Set Custom values for certain payment methods
         */
        setCustomPaymentMethodValues: function () {
            var paymentMethod = $('input[type=radio][name=paymentMethod]:checked').val();
            var dob = $('#p_method_adyen_hpp_' + paymentMethod + '_dob').val();
            if (dob) {
                $("#dob").val(dob);
            }

            var ssn = $('#p_method_adyen_hpp_' + paymentMethod + '_ssn');
            if (ssn) {
                $("#socialSecurityNumber").val(ssn.val());
            }

            var terminalId = $('#adyen_pos_terminal');
            if (terminalId) {
                $("#terminalId").val(terminalId.val());
            }

            var firstName = $('#p_method_adyen_hpp_' + paymentMethod + '_first_name').val();
            if (firstName) {
                $("#firstName").val(firstName);
            }

            var lastName = $('#p_method_adyen_hpp_' + paymentMethod + '_last_name').val();
            if (lastName) {
                $("#lastName").val(lastName);
            }
        },

        /**
         * Create DatePicker for Date of Birth field
         *
         * @param element
         */
        createDobDatePicker: function (element) {
            $("." + element).datepicker({
                dateFormat: this.dobDateFormat,
                changeMonth: true,
                changeYear: true,
                yearRange: "-120:+0"
            });
        },


        togglePaymentMethod: function (paymentMethod) {
            $(".payment_method_details").hide();
            $(".chckt-pm__details").hide();

            $("#dd_method_" + paymentMethod).show();
            $("#adyen_hpp_" + paymentMethod + "_container").show();
        },

        createDfValue: function () {
            window.dfDo("dfValue");
        },

        checkTermsAndConditions: function (checked) {
            $('.adyen-terms-conditions-check').prop('checked', checked)
            if (checked) {
                actions.enable();
                $('.adyen-terms-conditions-check-error').addClass('hidden');
            } else {
                actions.disable();
                $('.adyen-terms-conditions-check-error').removeClass('hidden');
            }
        },

        showCheckTermsAndConditionsError: function (checked) {
            const $errorMessage = $('.adyen-terms-conditions-check-error');
            if ($('.adyen-terms-conditions-check').prop('checked')) {
                $errorMessage.addClass('hidden');
            } else {
                $errorMessage.removeClass('hidden');
            }
        },

        /**
         *
         * @returns {Promise<void>}
         * @param initConfig
         * @param fnCallbackArray
         */
        initiateCheckout: async function (initConfig, fnCallbackArray) {
            const configuration = {
                ...initConfig,
                analytics: {
                    enabled: false // Set to false to not send analytics data to Adyen.
                },
                risk: {
                    enabled: false
                },
                onPaymentCompleted: (result, component) => {
                    console.info(result, component);
                },
                onError: (error, component) => {
                    console.error(error.name, error.message, error.stack, component);
                },
            };

            this.checkout = await AdyenCheckout(configuration);
            for (let callback of Object.keys(fnCallbackArray)) {
                const params = fnCallbackArray[callback];
                if (params && params.hasOwnProperty('label') && !!params.label) {
                    for (const label of params.label) {
                        const paramsWithLabel = {
                            ...params,
                            label: label
                        };
                        this[callback](paramsWithLabel);
                    }
                } else {
                    this[callback](params);
                }
            }
        },

        initiateOneClickCard: function (storedCardList) {
            if (storedCardList && storedCardList.length) {
                for (const storedCard of storedCardList) {
                    const oneClickCardNode = document.getElementById("one-click-card_" + storedCard.storedPaymentMethodId);
                    const oneClickCard = this.checkout.create('card', storedCard);
                    oneClickCard.mount(oneClickCardNode);
                    this.oneClickCards[storedCard.storedPaymentMethodId] = oneClickCard;
                }
            }
        },

        /**
         *
         * @param functionParameters
         */
        initiateCard: function (functionParameters) {
            const {allowedCards, showRememberDetails, cardHolderNameRequired} = functionParameters;
            const context = this;
            this.card = this.checkout.create('card', {
                type: 'card',
                hasHolderName: true,
                holderNameRequired: cardHolderNameRequired,
                enableStoreDetails: showRememberDetails,
                brands: allowedCards,
                onBrand: copyCardBrand

            });

            function copyCardBrand (event) {
                context.selectedCardBrand = event.brand;
            }

            this.card.mount(document.getElementById('card-div'));
        },

        /**
         *
         */
        initiateSepaDirectDebit: function () {
            const sepaDirectDebitNode = document.getElementById('adyen_hpp_sepadirectdebit_container');
            if (this.checkout) {
                this.sepaDirectDebit = this.checkout.create('sepadirectdebit', {
                    onChange: handleOnChange
                });

                function handleOnChange (event) {
                    var sepaOwnerNameField = document.getElementById('sepaOwnerName');
                    var sepaIbanNumberField = document.getElementById('sepaIbanNumber');

                    var sepaOwnerName = event.data.paymentMethod["ownerName"]
                    var sepaIbanNumber = event.data.paymentMethod["iban"]

                    sepaOwnerNameField.value = sepaOwnerName;
                    sepaIbanNumberField.value = sepaIbanNumber;
                }

                this.sepaDirectDebit.mount(sepaDirectDebitNode);
            }
        },

        initiateIdeal: function (idealDetails) {
            var idealNode = document.getElementById('adyen_hpp_ideal_container');
            var ideal = this.checkout.create('ideal', {
                details: idealDetails, // The array of issuers coming from the /paymentMethods api call
                showImage: true, // Optional, defaults to true
                onChange: handleChange // Gets triggered whenever a user selects a bank// Gets triggered once the state is valid
            });

            function handleChange (event) {
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

        initiateOnlinebankingIN: function () {
            let onlineBankingInNode = document.getElementById('adyen_hpp_onlinebanking_IN_container');
            let onlineBankingIn = this.checkout.create('onlinebanking_IN', {
                onChange: handleChange // Gets triggered whenever a user selects a bank// Gets triggered once the state is valid
            });

            function handleChange (event) {
                let issuerIdField = document.getElementById('issuerId');
                let issuerId = event.data.paymentMethod.issuer;
                issuerIdField.value = issuerId;
            }

            try {
                onlineBankingIn.mount(onlineBankingInNode);
            } catch (e) {
                console.log('Something went wrong trying to mount the onlineBanking_In component: ${e}');
            }
        },
        initiateOnlineBankingPL: function () {
            let onlineBankingPlNode = document.getElementById('adyen_hpp_onlineBanking_PL_container');
            let onlineBankingPl = this.checkout.create('onlineBanking_PL', {
                onChange: handleChange // Gets triggered whenever a user selects a bank// Gets triggered once the state is valid
            });

            function handleChange (event) {
                let issuerIdField = document.getElementById('issuerId');
                let issuerId = event.data.paymentMethod.issuer;
                issuerIdField.value = issuerId;
            }

            try {
                onlineBankingPl.mount(onlineBankingPlNode);
            } catch (e) {
                console.log('Something went wrong trying to mount the onlineBanking_PL component: ${e}');
            }
        },

        initiateWalletIN: function () {
            let walletInNode = document.getElementById('adyen_hpp_wallet_IN_container');
            let walletIn = this.checkout.create('wallet_IN', {
                onChange: handleChange // Gets triggered whenever a user selects a bank// Gets triggered once the state is valid
            });

            function handleChange (event) {
                let issuerIdField = document.getElementById('issuerId');
                let issuerId = event.data.paymentMethod.issuer;
                issuerIdField.value = issuerId;
            }

            try {
                walletIn.mount(walletInNode);
            } catch (e) {
                console.log('Something went wrong trying to mount the upi_In component: ${e}');
            }
        },

        initiateUPI: function () {
            const label = this.getVisibleLabel();
            const self = this;
            const uPINode = document.getElementById('adyen-component-button-container-' + label);
            let upi = this.checkout.create('upi', {
                onPaymentCompleted: handlePaymentResult,
                defaultMode:'vpa',
                showPayButton:true,
            });

            function handlePaymentResult(result, component){
                $.ajax({
                    url: ACC.config.encodedContextPath + '/adyen/component/resultHandler',
                    type: "POST",
                    data: JSON.stringify({
                        resultCode: result.resultCode,
                        sessionData: result.sessionData
                    }),
                    contentType: "application/json; charset=utf-8",
                    success: function (data) {
                        try {
                            window.location.href = ACC.config.encodedContextPath + "/" + data.replace("redirect:/","");
                        } catch (e) {
                            console.log('Error redirecting the user to the placeOrder page');
                            AdyenCheckoutHybris.handleResult(ErrorMessages.PaymentError, true);
                        }
                    },
                    error: function (xmlHttpResponse, exception) {
                        var responseMessage = xmlHttpResponse.responseJSON;
                        if (xmlHttpResponse.status === 400) {
                            AdyenCheckoutHybris.handleResult(responseMessage, true);
                        } else {
                            console.log('Error on handling the redirect to the placeOrder page: ' + responseMessage);
                            handleResult(ErrorMessages.PaymentError, true);
                        }
                    }
                })
            }
            try {
                upi.mount(uPINode);
            } catch (e) {
                console.log('Something went wrong trying to mount the upi component: ${e}');
            }
        },

        initiatePaytm: function () {
            let payTmNode = document.getElementById('adyen_hpp_paytm_container');
            let payTm = this.checkout.create('paytm');
            try {
                payTm.mount(payTmNode);
            } catch (e) {
                console.log('Something went wrong trying to mount the paytm component: ${e}');
            }
        },

        initiateEps: function (epsDetails) {
            var epsNode = document.getElementById('adyen_hpp_eps_container');
            var eps = this.checkout.create('eps', {
                issuers: epsDetails, // The array of issuers coming from the /paymentMethods api call
                showImage: true, // Optional, defaults to true
                onChange: handleOnChange // Gets triggered once the shopper selects an issuer
            });

            function handleOnChange (event) {
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

        /**
         *
         * @param params
         */
        initiatePaypal: function (params) {
            const {amount, isImmediateCapture, paypalMerchantId, label} = params;
            const paypalNode = document.getElementById('adyen-component-button-container-' + label);
            const self = this;

            const adyenComponent = this.checkout.create("paypal", {
                style: { // Optional configuration for PayPal payment buttons.
                    layout: "vertical",
                    color: "gold"
                },
                environment: this.checkout.options.environment,
                amount: {
                    currency: amount.currency,
                    value: amount.value
                },
                configuration: {
                    intent: isImmediateCapture ? "capture" : "authorize",
                    merchantId: (this.checkout.options.environment === 'test') ? null : paypalMerchantId,  // Your PayPal Merchant ID. Required for accepting live payments.
                },
                blockPayPalCreditButton: true,
                blockPayPalPayLaterButton: true,
                onInit: (data, actions) => {
                    actions.enable();
                    $(document).on('change', '.adyen-terms-conditions-check', function (event) {
                        const checked = (event.target.checked)
                        $('.adyen-terms-conditions-check').prop('checked', checked)
                        if (checked) {
                            actions.enable();
                        } else {
                            actions.disable();
                        }
                        self.showCheckTermsAndConditionsError();
                    });
                },
                onClick: function () {
                    // Show a validation error if the checkbox is not checked
                    self.showCheckTermsAndConditionsError();
                },
                onChange: function (state, component) {
                    if (!state.isValid) {
                        self.enablePlaceOrder(label);
                    }
                },
                onSubmit: function (state, component) {
                    if (!state.isValid) {
                        self.enablePlaceOrder(label);
                        return false;
                    }
                    self.makePayment(state.data.paymentMethod, component, self.handleResult, label);
                },
                onCancel: function (data, component) {
                    // Sets your prefered status of the component when a PayPal payment is cancelled.
                    self.handleResult(ErrorMessages.PaymentCancelled, true);
                },
                onError: function (error, component) {
                    // Sets your prefered status of the component when an error occurs.
                    self.handleResult(ErrorMessages.PaymentError, true);
                },
                onAdditionalDetails: function (state, component) {
                    self.submitDetails(state.data, self.handleResult);
                }
            });

            try {
                adyenComponent.mount(paypalNode);
            } catch (e) {
                console.log('Something went wrong trying to mount the PayPal component: ' + e);
            }
        },

        initiateApplePay: function (params) {
            const {amount, countryCode, applePayMerchantIdentifier, applePayMerchantName, label} = params;
            const applePayNode = document.getElementById('adyen-component-button-container-' + label);
            const self = this;
            const applePayConfiguration = {
                //onValidateMerchant is required if you're using your own Apple Pay certificate
                onValidateMerchant: (resolve, reject, validationURL) => {
                    if (self.isTermsAccepted(label)) {
                        resolve();
                    } else {
                        reject();
                        self.handleResult(ErrorMessages.TermsNotAccepted, true);
                    }
                    // Your server uses the validation URL to request a session from the Apple Pay server.
                    // Call resolve(MERCHANTSESSION) or reject() to complete merchant validation.
                    /*validateMerchant(validationURL)
                        .then(response => {
                            // Complete merchant validation with resolve(MERCHANTSESSION) after receiving an opaque merchant session object, MerchantSession
                            resolve(response);
                        })
                        .catch(error => {
                            // Complete merchant validation with reject() if any error occurs
                            reject();
                           });*/
                    console.log(validationURL, reject, resolve);
                }
            };
            const adyenComponent = this.checkout.create("applepay", {
                amount: {
                    currency: amount.currency,
                    value: amount.value
                },
                countryCode: countryCode,
                configuration: {
                    merchantName: applePayMerchantName,
                    merchantId: applePayMerchantIdentifier
                },
                // Button config
                buttonType: "plain",
                buttonColor: "black",
                onChange: function(state, component) {
                    if (!state.isValid) {
                        self.enablePlaceOrder(label);
                    }
                },
                onSubmit: function(state, component) {
                    if (!state.isValid) {
                        self.enablePlaceOrder(label);
                        return false;
                    }
                    self.makePayment(state.data.paymentMethod, component, self.handleResult, label);
                },
                onClick: function(resolve, reject) {
                    if (self.isTermsAccepted(label)) {
                        resolve();
                    } else {
                        reject();
                        self.handleResult(ErrorMessages.TermsNotAccepted, true);
                    }
                }
            });

            adyenComponent.isAvailable()
                .then(function () {
                    adyenComponent.mount(applePayNode);
                })
                .catch(function (e) {
                    // Apple Pay is not available
                    console.log('Something went wrong trying to mount the Apple Pay component: ' + e);
                    self.handleResult(ErrorMessages.PaymentNotAvailable, true);
                });
        },

        /**
         *
         * @param params
         */
        initiateGooglePay: function (params) {
            const {amount, merchantAccount, label} = params;
            const googlePayNode = document.getElementById('adyen-component-button-container-' + label);
            const self = this;
            const adyenComponent = this.checkout.create("paywithgoogle", {
                environment: this.checkout.options.environment,
                amount: {
                    currency: amount.currency,
                    value: amount.value
                },
                configuration: {
                    gatewayMerchantId: merchantAccount,
                    merchantName: merchantAccount
                },
                buttonColor: "white",
                onChange: function (state, component) {
                    if (!state.isValid) {
                        self.hideSpinner();
                    }
                },
                onSubmit: function (state, component) {
                    if (!state.isValid) {
                        self.hideSpinner();
                        return false;
                    }
                    self.showSpinner();
                    self.makePayment(state.data.paymentMethod, component, self.handleResult, label);
                },
                onClick: function (resolve, reject) {
                    if (self.isTermsAccepted(label)) {
                        resolve();
                    } else {
                        reject();
                        self.handleResult(ErrorMessages.TermsNotAccepted, true);
                    }
                }
            });

            adyenComponent.isAvailable()
                .then(function () {
                    adyenComponent.mount(googlePayNode);
                })
                .catch(function (e) {
                    // Google Pay is not available
                    console.log('Something went wrong trying to mount the Google Pay component: ' + e);
                    self.handleResult(ErrorMessages.PaymentNotAvailable, true);
                });
        },

        /**
         *
         * @param params
         */
        initiateAmazonPay: function (params) {
            const {amount, deliveryAddress, amazonPayConfiguration, locale} = params;
            const label = this.getVisibleLabel();
            let componentConfiguration;
            let state = '';
            if (deliveryAddress.country.isocode === 'US') {
                state = deliveryAddress.region.isocodeShort;
            }

            componentConfiguration = {
                environment: this.checkout.options.environment,
                addressDetails: {
                    name: deliveryAddress.firstName + ' ' + deliveryAddress.lastName,
                    addressLine1: deliveryAddress.line1,
                    addressLine2: deliveryAddress.line2,
                    city: deliveryAddress.town,
                    stateOrRegion: state,
                    postalCode: deliveryAddress.postalCode,
                    countryCode: deliveryAddress.country.isocode,
                    phoneNumber: deliveryAddress.phone || '0'
                },
                region: amazonPayConfiguration.region,
                amount: amount,
                locale: locale,
                configuration: amazonPayConfiguration,
                checkoutMode: 'ProcessOrder',
                productType: 'PayAndShip',
                placement: 'Checkout',
                returnUrl: window.location.origin + ACC.config.encodedContextPath + '/checkout/multi/adyen/summary/amazonpay/placeorder',
                onClick: (resolve, reject) => {
                    if (this.isTermsAccepted(label)) {
                        resolve();
                    } else {
                        reject();
                        this.handleResult(ErrorMessages.TermsNotAccepted, true);
                    }
                }
            };
            const amazonPayNode = document.getElementById('adyen-component-button-container-' + label);
            const adyenComponent = this.checkout.create("amazonpay", componentConfiguration);
            try {
                adyenComponent.mount(amazonPayNode);
            } catch (e) {
                console.log('Something went wrong trying to mount the Amazon Pay component: ' + e);
                this.handleResult(ErrorMessages.PaymentNotAvailable, true);
            }
        },

        initiateMbway: function (params) {
            const {label} = params;
            var mbwayNode = document.getElementById('adyen-component-container-' + label);
            var self = this;

            var adyenComponent = this.checkout.create("mbway", {
                showPayButton: false,
                onChange: function (state, component) {
                    if (!state.isValid) {
                        self.enablePlaceOrder(label);
                    }
                },
                onSubmit: function (state, component) {
                    if (!state.isValid) {
                        self.enablePlaceOrder(label);
                        return;
                    }
                    self.makePayment(state.data.paymentMethod, component, self.handleResult, label);
                },
                onAdditionalDetails: function (state, component) {
                    self.submitDetails(state.data, self.handleResult);
                },
                onError: function (error, component) {
                    console.log('Something went wrong trying to make the MBWay payment: ' + error);
                    self.handleResult(ErrorMessages.PaymentError, true);
                }
            });

            try {
                adyenComponent.mount(mbwayNode);
                self.configureButton(adyenComponent, false, label);
            } catch (e) {
                console.log('Something went wrong trying to mount the MBWay component: ' + e);
            }
        },

        initiateAfterPay: function (countryCode) {
            this.afterPay = this.checkout.create("afterpay_default", {
                countryCode: countryCode,
                visibility: { // Optional configuration
                    personalDetails: "editable",
                    billingAddress: "hidden",
                    deliveryAddress: "hidden"
                }
            });

            try {
                this.afterPay.mount('#afterpay-container');
            } catch (e) {
                console.log('Something went wrong trying to mount the afterpay component: ' + e);
            }
        },

        initiatePix: function (params) {
            const {label, issuers} = params;
            $("#generateqr-" + label).click(function () {
                AdyenCheckoutHybris.showSpinner();
                if (!AdyenCheckoutHybris.isTermsAccepted(label)) {
                    AdyenCheckoutHybris.handleResult(ErrorMessages.TermsNotAccepted, true)
                } else {
                    $("#generateqr-" + label).hide();
                    $(".checkbox").hide();
                    var actionHandler = {
                        handleAction: function (action) {
                            AdyenCheckoutHybris.checkout.createFromAction(action, {
                                issuers: issuers,
                                onAdditionalDetails: function (state) {
                                    AdyenCheckoutHybris.hideSpinner();
                                    AdyenCheckoutHybris.submitDetails(state.data, AdyenCheckoutHybris.handleResult);
                                }
                            }).mount('#qrcode-container-' + label);
                            AdyenCheckoutHybris.hideSpinner();
                        }
                    };
                    AdyenCheckoutHybris.makePayment({type: "pix"}, actionHandler, AdyenCheckoutHybris.handleResult, label);
                }
            });
        },

        initiateBcmc: function () {
            this.bcmc = this.checkout.create("bcmc", {
                hasHolderName: true,
                holderNameRequired: true
            });

            try {
                this.bcmc.mount('#bcmc-container');
            } catch (e) {
                console.log('Something went wrong trying to mount the BCMC component: ' + e);
            }
        },

        initiateBcmcMobile: function (params) {
            const {label} = params;
            $("#generateqr-" + label).click(function () {
                AdyenCheckoutHybris.showSpinner();
                if (!AdyenCheckoutHybris.isTermsAccepted(label)) {
                    AdyenCheckoutHybris.handleResult(ErrorMessages.TermsNotAccepted, true)
                } else {
                    $("#generateqr-" + label).hide();
                    $(".checkbox").hide();
                    var actionHandler = {
                        handleAction: function (action) {
                            AdyenCheckoutHybris.checkout.createFromAction(action, {
                                onAdditionalDetails: function (state) {
                                    AdyenCheckoutHybris.hideSpinner();
                                    AdyenCheckoutHybris.submitDetails(state.data, AdyenCheckoutHybris.handleResult);
                                }
                            }).mount('#qrcode-container-' + label);
                            AdyenCheckoutHybris.hideSpinner();
                        }
                    };
                    AdyenCheckoutHybris.makePayment({type: "bcmc_mobile"}, actionHandler, AdyenCheckoutHybris.handleResult, label);
                }
            });
        },

        initiateBizum: function (params) {
            const {label} = params;
            const self = this;

            $(document).ready(function () {
                $("#placeOrder-" + label).click(function () {
                    $(this).prop('disabled', true);

                    AdyenCheckoutHybris.showSpinner();

                    let termsCheck = document.getElementById('terms-conditions-check-' + label).checked

                    if (termsCheck === false) {
                        self.handleResult(ErrorMessages.TermsNotAccepted, true);
                        return;
                    }

                    const placeOrderForm = {
                        "securityCode": null,
                        "termsCheck": termsCheck,
                    };

                    $.ajax({
                        url: ACC.config.encodedContextPath + '/checkout/multi/adyen/summary/placeOrderBizum',
                        type: "POST",
                        data: placeOrderForm,
                        success: function (data) {
                            try {
                                let response = JSON.parse(data);

                                const form = document.createElement('form');
                                form.method = 'POST';
                                form.action = response.url;

                                for (const key in response.data) {
                                    form.innerHTML+='<input type="hidden" name="' + key + '" value="' +response.data[key] + '" /> ';
                                }

                                document.body.appendChild(form);
                                form.submit();
                            } catch (e) {
                                console.log('Error place order: ' + e);
                                AdyenCheckoutHybris.hideSpinner();
                                $("#placeOrder-" + label).prop('disabled', false);
                                self.handleResult(data, true);
                            }
                        },
                        error: function (xmlHttpResponse, exception) {
                            console.log(exception);
                            AdyenCheckoutHybris.hideSpinner();
                            $("#placeOrder-" + label).prop('disabled', false);
                            self.handleResult(responseMessage, true);
                        }
                    });
                });
            });
        },

        /**
         * @param form
         * @param useSpinner
         * @param label
         */
        configureButton: function (form, useSpinner, label) {
            $(document).ready(function () {
                $("#placeOrder-" + label).click(function () {
                    $(this).prop('disabled', true);
                    if (useSpinner) {
                        AdyenCheckoutHybris.showSpinner();
                    }
                    form.submit();
                });
            });
        },

        makePayment: function (data, component, handleResult, label) {
            $.ajax({
                url: ACC.config.encodedContextPath + '/adyen/component/payment',
                type: "POST",
                data: JSON.stringify({
                    paymentMethodDetails: data,
                    termsCheck: document.getElementById('terms-conditions-check-' + label).checked
                }),
                contentType: "application/json; charset=utf-8",
                success: function (data) {
                    try {
                        var response = JSON.parse(data);
                        if (response.resultCode && response.resultCode === 'Pending' && response.action) {
                            component.handleAction(response.action);
                        } else if (response.resultCode && (response.resultCode === 'Authorised' || response.resultCode === 'RedirectShopper')) {
                            handleResult(response, false);
                        } else {
                            handleResult(ErrorMessages.PaymentError, true);
                        }
                    } catch (e) {
                        console.log('Error parsing makePayment response: ' + data);
                        handleResult(ErrorMessages.PaymentError, true);
                    }
                },
                error: function (xmlHttpResponse, exception) {
                    var responseMessage = xmlHttpResponse.responseJSON;
                    if (xmlHttpResponse.status === 400) {
                        handleResult(responseMessage, true);
                    } else {
                        console.log('Error on makePayment: ' + responseMessage);
                        handleResult(ErrorMessages.PaymentError, true);
                    }
                }
            })
        },

        submitDetails: function (data, handleResult) {
            $.ajax({
                url: ACC.config.encodedContextPath + '/adyen/component/submit-details',
                type: "POST",
                data: JSON.stringify(data),
                contentType: "application/json; charset=utf-8",
                success: function (data) {
                    try {
                        var response = JSON.parse(data);
                        if (response.resultCode) {
                            handleResult(response, false);
                        } else {
                            handleResult(ErrorMessages.PaymentError, true);
                        }
                    } catch (e) {
                        console.log('Error parsing submitDetails response: ' + data);
                        handleResult(ErrorMessages.PaymentError, true);
                    }
                },
                error: function (xhr, exception) {
                    var responseMessage = xhr.responseJSON;
                    if (xhr.status === 400) {
                        handleResult(responseMessage, true);
                    } else {
                        console.log('Error on submitDetails: ' + responseMessage);
                        handleResult(ErrorMessages.PaymentError, true);
                    }
                }
            })
        },

        isTermsAccepted: function (label) {
            return document.getElementById('terms-conditions-check-' + label).checked;
        },


        handleResult: function (data, error) {
            if (error) {
                document.querySelector("#resultData").value = data;
                document.querySelector("#isResultError").value = error;
            } else {
                document.querySelector("#resultData").value = JSON.stringify(data);
            }
            document.querySelector("#handleComponentResultForm").submit();
        },

        showSpinner: function () {
            document.getElementById("spinner_wrapper").style.display = "flex";
        },

        hideSpinner: function () {
            document.getElementById("spinner_wrapper").style.display = "none";
        },

        enablePlaceOrder: function (label) {
            //hide spinner
            this.hideSpinner();
            //enable button
            $("#placeOrder-" + label).prop('disabled', false);
        },

        getVisibleLabel: function () {
            if (!(window.getComputedStyle(document.getElementById('adyen-checkout-visible-xs')).display === "none")) {
                return 'visible-xs';
            }
            if (!(window.getComputedStyle(document.getElementById('adyen-checkout-hidden-xs')).display === "none")) {
                return 'hidden-xs';
            }
            console.log('Something went wrong while trying to compute current visible label');
            return '';
        }
    };
})();
