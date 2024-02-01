import {urlContextPath} from "../util/baseUrlUtil";

export const routes = {
    shippingAddress: urlContextPath + "/checkout/multi/adyen/shipping-address",
    shippingMethod:  urlContextPath + "/checkout/multi/adyen/shipping-method",
    paymentMethod:  urlContextPath + "/checkout/multi/adyen/payment-method",
    review:  urlContextPath + "/checkout/multi/adyen/review"
}