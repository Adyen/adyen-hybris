import axios from "axios";
import {CSRFToken, urlContextPath} from "../util/baseUrlUtil";
import {AdyenAddressForm, AdyenPaymentForm} from "../types/paymentForm";
import {AddressData} from "../types/addressData";
import {AddressModel} from "../reducers/types";

export class PaymentService {
    static async selectPaymentMethod(paymentForm: AdyenPaymentForm) {
        return axios.post(urlContextPath + '/api/checkout/select-payment-method', paymentForm, {
            headers: {
                'Content-Type': 'application/json',
                'CSRFToken': CSRFToken
            }
        })
            .then(() => true)
            .catch(() => {
                console.error('Error on shipping method select')
                return false
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