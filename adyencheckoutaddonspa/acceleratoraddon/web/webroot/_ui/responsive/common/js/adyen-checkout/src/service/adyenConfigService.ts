import axios, {AxiosError, AxiosResponse} from "axios";
import {urlContextPath} from "../util/baseUrlUtil";
import {store} from "../store/store";
import {AdyenConfigData} from "../types/adyenConfigData";
import {ErrorResponse} from "../types/errorResponse";
import {ErrorHandler} from "../components/common/ErrorHandler";

export class AdyenConfigService {

    public static fetchPaymentMethodsConfig() {
        axios.get(urlContextPath + '/api/checkout/payment-methods-configuration', {
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        })
            .then((response: AxiosResponse<AdyenConfigData>) => {
                store.dispatch({type: "adyenConfig/setAdyenConfig", payload: response.data})
            })
            .catch((errorResponse:AxiosError<ErrorResponse>) => {
                ErrorHandler.handleError(errorResponse)
                console.error("Payment method config fetch error.")
                return false
            })
    }
}