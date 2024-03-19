import React from "react";
import ShippingAddress from "../shipping-address/ShippingAddress";
import {ShippingMethodHeader} from "../headers/ShippingMethodHeader";
import {PaymentHeader} from "../headers/PaymentHeader";
import {CartDataService} from "../../service/cartDataService";
import {ScrollHere} from "../common/ScrollTo";

export class ShippingAddressStep extends React.Component<{}, null> {

    componentDidMount() {
        CartDataService.fetchCartData();
    }

    render() {
        return (
            <>
                <ScrollHere/>
                <ShippingAddress/>
                <ShippingMethodHeader/>
                <PaymentHeader/>
            </>
        )
    }

}