import React from "react";
import {ShippingMethodHeader} from "../headers/ShippingMethodHeader";
import {ShippingAddressHeader} from "../headers/ShippingAddressHeader";
import {CartDataService} from "../../service/cartDataService";
import Payment from "../payment/Payment";

export class PaymentStep extends React.Component<{  }, null> {

    componentDidMount() {
        CartDataService.fetchCartData();
    }

    render() {
        return (
            <>
                <ShippingAddressHeader editEnabled={true}/>
                <ShippingMethodHeader editEnabled={true}/>
                <Payment/>
            </>
        )
    }

}