var AdyenExpressCheckoutHybris = (function () {
    'use strict';


    return {

    adyenConfig: {
        merchantName: null,
        merchantId: null,
        pageType: null,
        productCode: null
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
            onError: (error, component) => {
                console.error(error.name, error.message, error.stack, component);
            },
        };

        return await AdyenCheckout(configuration);
    },
        initiateApplePayExpress: async function (params, config) {
            var checkoutPromise = this.initiateCheckout(config);
            const {amount, countryCode, applePayMerchantIdentifier, applePayMerchantName, pageType, productCode} = params;
            const applePayNode = document.getElementById('adyen-component-button-container');

            this.adyenConfig.merchantName = applePayMerchantName;
            this.adyenConfig.merchantId = applePayMerchantIdentifier;
            this.adyenConfig.pageType = pageType;
            this.adyenConfig.productCode = productCode;

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
                buttonType: "check-out",
                buttonColor: "black",
                requiredShippingContactFields: [
                   "postalAddress",
                   "name",
                   "email"
                ],
//                onShippingContactSelected: function(resolve, reject, event){
//                    //TODO: might be used to recalculate cart with shipping method
//                    console.log(event.shippingContact);
//
//                    var shippingMethodUpdate = {
//                        newTotal: {
//                            amount: amount.value
//                        }
//                    }
//                    resolve(shippingMethodUpdate);
//                },
                onClick: function(resolve, reject) {
                    resolve();
                },
                onAuthorized: (resolve, reject, event) => {
                   var data = this.prepareData(event);
                   this.makePayment(data, resolve, reject);
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
        },
        makePayment: function (data, resolve, reject) {
        $.ajax({
            url: this.getUrl(),
            type: "POST",
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                try {
                    var response = JSON.parse(data);
                    if (response.resultCode && (response.resultCode === 'Authorised' || response.resultCode === 'RedirectShopper')) {
                        resolve();
                        this.handleResult(response, false);
                    } else {
                        reject();
                        this.handleResult(ErrorMessages.PaymentError, true);
                    }
                } catch (e) {
                    console.log('Error parsing makePayment response: ' + data);
                    reject();
                    this.handleResult(ErrorMessages.PaymentError, true);
                }
            },
            error: function (xmlHttpResponse, exception) {
                reject();
                var responseMessage = xmlHttpResponse.responseJSON;
                if (xmlHttpResponse.status === 400) {
                    this.handleResult(responseMessage, true);
                } else {
                    console.log('Error on makePayment: ' + responseMessage);
                    this.handleResult(ErrorMessages.PaymentError, true);
                }
            }
        })
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
    prepareData: function(event) {
        if (this.adyenConfig.pageType === 'PDP'){
            return {
                productCode: this.adyenConfig.productCode,
                adyenApplePayMerchantName: this.adyenConfig.merchantName,
                adyenApplePayMerchantIdentifier: this.adyenConfig.merchantId,
                applePayToken: btoa(JSON.stringify(event.payment.token.paymentData)),
                addressData: {
                    email: event.payment.shippingContact.emailAddress,
                    firstName: event.payment.shippingContact.givenName,
                    lastName: event.payment.shippingContact.familyName,
                    line1: event.payment.shippingContact.addressLines[0],
                    line2: event.payment.shippingContact.addressLines[1],
                    postalCode: event.payment.shippingContact.postalCode,
                    town: event.payment.shippingContact.locality,
                    country: {
                        isocode: event.payment.shippingContact.countryCode,
                        name: event.payment.shippingContact.country
                     }
                }
            }
        }
        if (this.adyenConfig.pageType === 'cart'){
            return {
                adyenApplePayMerchantName: this.adyenConfig.merchantName,
                adyenApplePayMerchantIdentifier: this.adyenConfig.merchantId,
                applePayToken: btoa(JSON.stringify(event.payment.token.paymentData)),
                addressData: {
                    email: event.payment.shippingContact.email,
                    firstName: event.payment.shippingContact.givenName,
                    lastName: event.payment.shippingContact.familyName,
                    line1: event.payment.shippingContact.addressLines[0],
                    line2: event.payment.shippingContact.addressLines[1],
                    postalCode: event.payment.shippingContact.postalCode,
                    town: event.payment.shippingContact.locality,
                    country: {
                        isocode: event.payment.shippingContact.countryCode,
                        name: event.payment.shippingContact.country
                     }
                }
            }
        }
        console.error('unknown page type')
        return {};
    },
    getUrl: function(){
        if (this.adyenConfig.pageType === 'PDP'){
            return ACC.config.encodedContextPath + '/expressCheckout/applePayPDP'
        }
        if (this.adyenConfig.pageType === 'cart'){
            return ACC.config.encodedContextPath + '/expressCheckout/cart'
        }
        console.error('unknown page type')
        return null;
    }
    }
})();