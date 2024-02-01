import React from "react";
import {ShippingMethodHeader} from "../headers/ShippingMethodHeader";
import {PaymentHeader} from "../headers/PaymentHeader";
import {FinalReviewHeader} from "../headers/FinalReviewHeader";
import {ShippingAddressHeader} from "../headers/ShippingAddressHeader";
import {CartDataService} from "../../service/cartDataService";

export class PaymentStep extends React.Component<{  }, null> {

    componentDidMount() {
        CartDataService.fetchCartData();
    }

    render() {
        return (
            <>
                <ShippingAddressHeader editEnabled={true}/>
                <ShippingMethodHeader editEnabled={true}/>
                <PaymentHeader/>
                Payment Step
                <FinalReviewHeader/>
            </>
        )
    }

}