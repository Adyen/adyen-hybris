import axios from "axios";
import {CSRFToken, urlContextPath} from "../util/baseUrlUtil";

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
            .catch(() => console.error("Payment status fetch error"))
    }
}