import React from "react";
import ShippingAddress from "../shipping-address/ShippingAddress";
import {ShippingMethodHeader} from "../headers/ShippingMethodHeader";
import {PaymentHeader} from "../headers/PaymentHeader";
import {ScrollHere} from "../common/ScrollTo";
import {StepBase} from "./StepBase";

export class ShippingAddressStep extends StepBase {

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