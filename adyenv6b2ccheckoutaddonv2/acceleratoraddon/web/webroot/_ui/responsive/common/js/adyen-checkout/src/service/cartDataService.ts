import {store} from "../store/store";
import axios from "axios";
import {urlContextPath} from "../util/baseUrlUtil";

export class CartDataService {
    static fetchCartData() {
        axios.get(urlContextPath + '/api/checkout/cart-data', {
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        })
            .then(response => {
                store.dispatch({type: "cartData/setCartData", payload: response.data})
            })
            .catch(() => console.error("Cart data fetch error"))

    }
}