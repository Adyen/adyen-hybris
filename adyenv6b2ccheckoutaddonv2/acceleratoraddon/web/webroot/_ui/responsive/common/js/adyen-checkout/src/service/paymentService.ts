import axios from "axios";
import {CSRFToken, urlContextPath} from "../util/baseUrlUtil";
import {AdyenAddressForm, AdyenPaymentForm} from "../types/paymentForm";
import {AddressModel} from "../reducers/types";
import {store} from "../store/store";

export class PaymentService {
    static async placeOrder(paymentForm: AdyenPaymentForm) {
        return axios.post(urlContextPath + '/api/checkout/place-order', paymentForm, {
            headers: {
                'Content-Type': 'application/json',
                'CSRFToken': CSRFToken
            }
        })
            .then(response => {
                let placeOrderData = (response.data);
                store.dispatch({type: "placeOrderData/setPlaceOrderData", payload: placeOrderData})
                return true
            })
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