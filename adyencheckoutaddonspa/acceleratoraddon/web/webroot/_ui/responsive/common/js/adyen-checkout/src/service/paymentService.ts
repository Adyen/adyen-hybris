import axios, {AxiosError, AxiosResponse} from "axios";
import {CSRFToken, urlContextPath} from "../util/baseUrlUtil";
import {AddressData, PlaceOrderRequest} from "../types/paymentForm";
import {AddressModel} from "../reducers/types";
import {store} from "../store/store";
import {CardState} from "../types/paymentState";
import {PaymentAction} from "@adyen/adyen-web/dist/types/types";
import {ErrorResponse} from "../types/errorResponse";
import {ErrorHandler} from "../components/common/ErrorHandler";

export interface PaymentResponse {
    success: boolean,
    is3DSRedirect?: boolean,
    paymentsAction?: PaymentAction,
    error?: string
}

export class PaymentService {
    static async placeOrder(paymentForm: PlaceOrderRequest) {
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
            .catch((errorResponse: AxiosError<ErrorResponse>): PaymentResponse | void => {
                console.error('Error on place order')
                if (errorResponse.response.status === 400) {
                    return {
                        success: false,
                        error: errorResponse.response.data.errorCode
                    }
                } else {
                    ErrorHandler.handleError(errorResponse)
                }
            })
    }

    static convertBillingAddress(address: AddressModel, saveInAddressBook: boolean): AddressData {
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
            townCity: address.city,
            saveInAddressBook: saveInAddressBook,
        }
    }

    static preparePlaceOrderRequest(data: any, useDifferentBillingAddress: boolean, saveInAddressBook: boolean, billingAddress?: AddressModel): PlaceOrderRequest {
        return {
            paymentRequest: data,
            useAdyenDeliveryAddress: !useDifferentBillingAddress,
            billingAddress: useDifferentBillingAddress ? this.convertBillingAddress(billingAddress, saveInAddressBook) : null,

        }
    }
}