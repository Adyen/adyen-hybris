import React from "react";
import {ShippingMethodHeader} from "../headers/ShippingMethodHeader";
import {ShippingAddressHeader} from "../headers/ShippingAddressHeader";
import {CartDataService} from "../../service/cartDataService";
import Payment from "../payment/Payment";
import RedirectOnIncompleteData from "../common/RedirectOnIncompleteData";
import {CheckoutSteps} from "../../types/checkoutStepsEnum";

export class PaymentStep extends React.Component<{}, null> {

    componentDidMount() {
        CartDataService.fetchCartData();
    }

    render() {
        return (
            <>
                <RedirectOnIncompleteData currentCheckoutStep={CheckoutSteps.PAYMENT_METHOD}>
                    <ShippingAddressHeader editEnabled={true}/>
                    <ShippingMethodHeader editEnabled={true}/>
                    <Payment/>
                </RedirectOnIncompleteData>
            </>
        )
    }

}