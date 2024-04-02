import axios, {AxiosError} from "axios";
import {CSRFToken, urlContextPath} from "../util/baseUrlUtil";
import {ErrorResponse} from "../types/errorResponse";
import {ErrorHandler} from "../components/common/ErrorHandler";

export class PaymentStatusService {
    static fetchPaymentStatus(orderCode: string) {
        return axios.post(urlContextPath + '/api/checkout/get-payment-status', orderCode, {
            headers: {
                'Content-Type': 'application/json',
                'CSRFToken': CSRFToken
            }
        })
            .then(response => {
                return response.data
            })
            .catch((errorResponse:AxiosError<ErrorResponse>) => {
                ErrorHandler.handleError(errorResponse)
                console.error("Payment status fetch error.")
                return false
            })
    }
}