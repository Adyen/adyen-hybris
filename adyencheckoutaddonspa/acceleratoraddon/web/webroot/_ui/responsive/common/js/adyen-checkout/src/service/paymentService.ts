import {AxiosError, AxiosResponse} from "axios";
import {CSRFToken, urlContextPath} from "../util/baseUrlUtil";
import {AdyenAddressForm, AdyenPaymentForm} from "../types/paymentForm";
import {AddressModel} from "../reducers/types";
import {CardState} from "../types/paymentState";
import {PaymentAction} from "@adyen/adyen-web/dist/types/types";
import {ErrorResponse} from "../types/errorResponse";
import {adyenAxios} from "../axios/AdyenAxios";

export interface PaymentResponse {
    success: boolean,
    is3DSRedirect?: boolean,
    paymentsAction?: PaymentAction,
    error?: string,
    errorFieldCodes?: string[]
    orderNumber?: string
}

export class PaymentService {
    static async placeOrder(paymentForm: AdyenPaymentForm) {
        return adyenAxios.post<PaymentResponse>(urlContextPath + '/api/checkout/place-order', paymentForm, {
            headers: {
                'Content-Type': 'application/json',
                'CSRFToken': CSRFToken
            }
        })
            .then((response: AxiosResponse<any>): PaymentResponse => {
                let placeOrderData = response.data;

                return {
                    success: true,
                    is3DSRedirect: placeOrderData.redirectTo3DS,
                    paymentsAction: placeOrderData.paymentsAction,
                    orderNumber: placeOrderData.orderNumber
                }
            })
            .catch((errorResponse: AxiosError<ErrorResponse>): PaymentResponse | void => {
                console.error('Error on place order')
                if (errorResponse.response.status === 400) {
                    return {
                        success: false,
                        error: errorResponse.response.data.errorCode,
                        errorFieldCodes: errorResponse.response.data.invalidFields
                    }
                }
            })
    }

    static convertBillingAddress(address: AddressModel, saveInAddressBook: boolean): AdyenAddressForm {
        return {
            addressId: address.id,
            countryIso: address.countryCode,
            firstName: address.firstName,
            lastName: address.lastName,
            line1: address.line1,
            line2: address.line2,
            phoneNumber: address.phoneNumber,
            postcode: address.postalCode,
            titleCode: address.titleCode,
            townCity: address.city,
            saveInAddressBook: saveInAddressBook,
        }
    }

    static prepareBankCardAdyenPaymentForm(cardState: CardState, useDifferentBillingAddress: boolean, saveInAddressBook: boolean, billingAddress?: AddressModel): AdyenPaymentForm {
        return {
            paymentMethod: "adyen_cc",
            useAdyenDeliveryAddress: !useDifferentBillingAddress,
            billingAddress: useDifferentBillingAddress ? this.convertBillingAddress(billingAddress, saveInAddressBook) : null,
            encryptedCardNumber: cardState.data.paymentMethod.encryptedCardNumber,
            encryptedSecurityCode: cardState.data.paymentMethod.encryptedSecurityCode,
            encryptedExpiryMonth: cardState.data.paymentMethod.encryptedExpiryMonth,
            encryptedExpiryYear: cardState.data.paymentMethod.encryptedExpiryYear,
            cardHolder: cardState.data.paymentMethod.holderName,
            browserInfo: JSON.stringify(cardState.data.browserInfo),
            rememberTheseDetails: cardState.data.storePaymentMethod,
            cardBrand: cardState.data.paymentMethod.brand
        }
    }

    static prepareStoredCardAdyenPaymentForm(cardState: CardState, useDifferentBillingAddress: boolean, saveInAddressBook: boolean, billingAddress?: AddressModel): AdyenPaymentForm {
        return {
            paymentMethod: "adyen_oneclick_" + cardState.data.paymentMethod.storedPaymentMethodId,
            selectedReference: cardState.data.paymentMethod.storedPaymentMethodId,
            useAdyenDeliveryAddress: !useDifferentBillingAddress,
            billingAddress: useDifferentBillingAddress ? this.convertBillingAddress(billingAddress, saveInAddressBook) : null,
            encryptedSecurityCode: cardState.data.paymentMethod.encryptedSecurityCode,
            browserInfo: JSON.stringify(cardState.data.browserInfo),
            cardBrand: cardState.data.paymentMethod.brand
        }
    }
}