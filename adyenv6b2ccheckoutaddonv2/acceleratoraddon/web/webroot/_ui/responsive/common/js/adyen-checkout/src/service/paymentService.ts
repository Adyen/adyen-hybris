import axios, {AxiosResponse} from "axios";
import {CSRFToken, urlContextPath} from "../util/baseUrlUtil";
import {AdyenAddressForm, AdyenPaymentForm} from "../types/paymentForm";
import {AddressModel} from "../reducers/types";
import {store} from "../store/store";
import {CardState} from "../types/paymentState";
import {PaymentAction} from "@adyen/adyen-web/dist/types/types";

export interface PaymentResponse {
    success: boolean,
    is3DSRedirect?: boolean,
    paymentsAction?: PaymentAction
}

export class PaymentService {
    static async placeOrder(paymentForm: AdyenPaymentForm) {
        return axios.post<PaymentResponse>(urlContextPath + '/api/checkout/place-order', paymentForm, {
            headers: {
                'Content-Type': 'application/json',
                'CSRFToken': CSRFToken
            }
        })
            .then((response: AxiosResponse<any>): PaymentResponse => {
                let placeOrderData = (response.data);
                store.dispatch({type: "placeOrderData/setPlaceOrderData", payload: placeOrderData})

                return {
                    success: true,
                    is3DSRedirect: response.data.redirectTo3DS,
                    paymentsAction: response.data.paymentsAction
                }
            })
            .catch((): PaymentResponse => {
                console.error('Error on place order')
                return {success: false}
            })
    }

    static convertBillingAddress(address: AddressModel): AdyenAddressForm {
        return {
            addressId: address.id,
            countryIsoCode: address.countryCode,
            firstName: address.firstName,
            lastName: address.lastName,
            line1: address.line1,
            line2: address.line2,
            phoneNumber: address.phoneNumber,
            postcode: address.postalCode,
            titleCode: address.titleCode,
            townCity: address.city
        }
    }

    static prepareBankCardAdyenPaymentForm(cardState: CardState, useDifferentBillingAddress: boolean, billingAddress?: AddressModel): AdyenPaymentForm {
        return {
            paymentMethod: "adyen_cc",
            useAdyenDeliveryAddress: !useDifferentBillingAddress,
            billingAddress: useDifferentBillingAddress ? this.convertBillingAddress(billingAddress) : null,
            encryptedCardNumber: cardState.data.paymentMethod.encryptedCardNumber,
            encryptedSecurityCode: cardState.data.paymentMethod.encryptedSecurityCode,
            encryptedExpiryMonth: cardState.data.paymentMethod.encryptedExpiryMonth,
            encryptedExpiryYear:cardState.data.paymentMethod.encryptedExpiryYear,
            cardHolder: cardState.data.paymentMethod.holderName,
            browserInfo: JSON.stringify(cardState.data.browserInfo),
            rememberTheseDetails: cardState.data.storePaymentMethod,
            cardBrand: cardState.data.paymentMethod.brand
        }
    }

    static prepareStoredCardAdyenPaymentForm(cardState: CardState, useDifferentBillingAddress: boolean, billingAddress?: AddressModel): AdyenPaymentForm {
        return {
            paymentMethod: "adyen_oneclick_" + cardState.data.paymentMethod.storedPaymentMethodId,
            selectedReference: cardState.data.paymentMethod.storedPaymentMethodId,
            useAdyenDeliveryAddress: !useDifferentBillingAddress,
            billingAddress: useDifferentBillingAddress ? this.convertBillingAddress(billingAddress) : null,
            encryptedSecurityCode: cardState.data.paymentMethod.encryptedSecurityCode,
            browserInfo: JSON.stringify(cardState.data.browserInfo),
            cardBrand: cardState.data.paymentMethod.brand
        }
    }
}