import {store} from "../store/store";
import axios, {AxiosResponse} from "axios";
import {urlContextPath} from "../util/baseUrlUtil";
import {CartData} from "../types/cartData";

export class CartDataService {
    static fetchCartData() {
        store.dispatch({type: "loading/cartData/start"})

        axios.get(urlContextPath + '/api/checkout/cart-data', {
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        })
            .then((response: AxiosResponse<CartData>) => {
                store.dispatch({type: "cartData/setCartData", payload: response.data})
                store.dispatch({type: "loading/cartData/end"})


                //set selected delivery mode
                if (response.data.deliveryMode) {
                    store.dispatch({type: "shippingMethod/setShippingMethod", payload: response.data.deliveryMode.code})
                }
            })
            .

            catch(() => {
                store.dispatch({type: "loading/cartData/end"})
                console.error("Cart data fetch error")
            })

    }
}