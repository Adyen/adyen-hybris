import React from "react";
import {PaymentHeader} from "../headers/PaymentHeader";
import {FinalReviewHeader} from "../headers/FinalReviewHeader";
import {ShippingAddressHeader} from "../headers/ShippingAddressHeader";
import {CartDataService} from "../../service/cartDataService";
import {ScrollHere} from "../common/ScrollTo";
import ShippingMethod from "../shipping-method/ShippingMethod";

export class ShippingMethodStep extends React.Component<{ }, null> {

    componentDidMount() {
        CartDataService.fetchCartData();
    }

    render() {

        return (
            <>
                <ScrollHere/>
                <ShippingAddressHeader editEnabled={true}/>
                <ShippingMethod/>
                <PaymentHeader/>
                <FinalReviewHeader/>
            </>
        )
    }
}