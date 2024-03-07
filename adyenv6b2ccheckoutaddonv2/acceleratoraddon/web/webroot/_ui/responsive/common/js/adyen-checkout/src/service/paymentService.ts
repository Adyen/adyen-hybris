import axios, {AxiosResponse} from "axios";
import {CSRFToken, urlContextPath} from "../util/baseUrlUtil";
import {AdyenAddressForm, AdyenPaymentForm} from "../types/paymentForm";
import {AddressModel} from "../reducers/types";
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
                return {
                    success: true,
                    is3DSRedirect: response.data.redirectTo3DS,
                    paymentsAction: response.data.paymentsAction
                }
            })
            .catch((): PaymentResponse => {
                console.error('Error on shipping method select')
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
}