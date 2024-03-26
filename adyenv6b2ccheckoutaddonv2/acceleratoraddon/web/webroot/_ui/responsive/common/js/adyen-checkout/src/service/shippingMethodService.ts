import axios, {AxiosResponse} from "axios";
import {CSRFToken, urlContextPath} from "../util/baseUrlUtil";
import {store} from "../store/store";
import {ShippingMethodModel} from "../reducers/types";
import {CartData} from "../types/cartData";
import {isEmpty} from "../util/stringUtil";

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

                //if shipping method is not selected, select first
                if (shippingModeModels.length > 0 && isEmpty(store.getState().shippingMethod.selectedShippingMethodCode)) {
                    this.selectShippingMethod(shippingModeModels[0].code)
                }

            })
            .catch(() => console.error("Shipping method fetch error"))
    }


    static selectShippingMethod(shippingMethodId: string) {
        return axios.post(urlContextPath + '/api/checkout/select-delivery-method', shippingMethodId, {
            headers: {
                'Content-Type': 'text/plain',
                'CSRFToken': CSRFToken
            }
        })
            .then((response: AxiosResponse<CartData>) => {
                store.dispatch({type: "cartData/setCartData", payload: response.data})

                //set selected delivery mode
                if (response.data.deliveryMode) {
                    store.dispatch({type: "shippingMethod/setShippingMethod", payload: response.data.deliveryMode.code})
                }
                return true;
            })
            .catch(() => {
                console.error('Error on shipping method select')
                return false;
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