import axios from "axios";
import {CSRFToken, urlContextPath} from "../util/baseUrlUtil";
import {store} from "../store/store";
import {ShippingMethodModel} from "../reducers/types";

export class ShippingMethodService {
    public static fetchShippingMethods() {
        axios.get(urlContextPath + '/api/checkout/delivery-methods', {
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        })
            .then(response => {
                let shippingModeModels = this.mapShippingMethodsResponseToModel(response.data);
                store.dispatch({type: "shippingMethod/setAvailableShippingMethods", payload: shippingModeModels})
            })
            .catch(() => console.error("Shipping method fetch error"))
    }


    static async selectShippingMethod(shippingMethodId: string) {
        return axios.post(urlContextPath + '/api/checkout/select-delivery-method', shippingMethodId, {
            headers: {
                'Content-Type': 'text/plain',
                'CSRFToken': CSRFToken
            }
        })
            .then(() => true)
            .catch(() => {
                console.error('Error on shipping method select')
                return false
            })
    }

    private static mapShippingMethodsResponseToModel(data: any[]): ShippingMethodModel[] {
        return data.map(shippingMethodData => {
            return {
                code: shippingMethodData.code,
                description: shippingMethodData.description,
                name: shippingMethodData.name,
                deliveryCost: {
                    currencyIso: shippingMethodData.deliveryCost.currencyIso,
                    formattedValue: shippingMethodData.deliveryCost.formattedValue,
                    value: shippingMethodData.deliveryCost.value
                }
            }
        })
    }

}