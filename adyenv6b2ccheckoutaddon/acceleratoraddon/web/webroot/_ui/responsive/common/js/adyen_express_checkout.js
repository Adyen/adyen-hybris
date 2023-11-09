var AdyenExpressCheckoutHybris = (function () {
    'use strict';


    return {

    address: null,
    adyenConfig: {
            merchantName: null,
            merchantId: null
    },

    initiateCheckout: async function (initConfig) {
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

                return await AdyenCheckout(configuration);
            },

        initiateApplePayExpress: async function (params, config) {
                    var checkoutPromise = this.initiateCheckout(config);
                    const {amount, countryCode, applePayMerchantIdentifier, applePayMerchantName} = params;
                    const applePayNode = document.getElementById('adyen-component-button-container');
                    const self = this;

                    this.adyenConfig.merchantName = applePayMerchantName;
                    this.adyenConfig.merchantId = applePayMerchantIdentifier;

                    const applePayConfiguration = {
                        //onValidateMerchant is required if you're using your own Apple Pay certificate
                        onValidateMerchant: (resolve, reject, validationURL) => {
                             resolve();

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
                        }
                    };
                    checkoutPromise.then((checkout) => {
                    var applePayComponent = checkout.create("applepay", {
                        amount: {
                            currency: amount.currency,
                            value: amount.value
                        },
                        configuration: {
                            merchantName: applePayMerchantName,
                            merchantId: applePayMerchantIdentifier
                        },
                        // Button config
                        buttonType: "plain",
                        buttonColor: "black",
                        onChange: function(state, component) {
                            console.log("Apple pay on change, state: ")
                            console.log(state)
                        },
                        onSubmit: function(state, component) {
                            if (!state.isValid) {
                                return false;
                            }
                            //TODO: implement payment call

                            this.address = null;
                        },
                        onShippingContactSelected: function(event){
                            this.address = event.shippingContact;
                            //TODO: implement call with address
                            console.log(this.address);
                        },
                        onClick: function(resolve, reject) {
                            resolve();
                        }
                    });
                    applePayComponent.isAvailable()
                        .then(function () {
                            applePayComponent.mount(applePayNode);
                        })
                        .catch(function (e) {
                            // Apple Pay is not available
                            console.log('Something went wrong trying to mount the Apple Pay component: ' + e);
                        });
                    })
                }
    }
})();