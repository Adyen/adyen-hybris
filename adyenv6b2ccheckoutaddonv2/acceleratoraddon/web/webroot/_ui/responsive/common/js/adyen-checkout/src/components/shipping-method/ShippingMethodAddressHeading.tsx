import React from "react";
import {isNotEmpty} from "../../util/stringUtil";
import {AddressData} from "../../types/addressData";

interface Props {
    address: AddressData
}

export class ShippingMethodAddressHeading extends React.Component<Props, any> {

    render() {
        if (!this.props.address) {
            return <></>
        }

        return (
            <>
                <div className="checkout-shipping-items-header"> SHIPPING ADDRESS</div>
                <span>
                    <b>{isNotEmpty(this.props.address.title) ? this.props.address.title + '\xa0' : ''}{this.props.address.firstName}&nbsp;{this.props.address.lastName}&nbsp;</b>
                    <br/>
                    {isNotEmpty(this.props.address.line1) ? this.props.address.line1 + ",\xa0" : ""}
                    {isNotEmpty(this.props.address.line2) ? this.props.address.line2 + ",\xa0" : ""}
                    {isNotEmpty(this.props.address.town) ? this.props.address.town + ",\xa0" : ""}
                    {isNotEmpty(this.props.address.postalCode) ? this.props.address.postalCode + ",\xa0" : ""}
                    {isNotEmpty(this.props.address.country.name) ? this.props.address.country.name + "\xa0" : ""}
                    <br/>
                    {isNotEmpty(this.props.address.phone) ? this.props.address.phone + "\xa0" : ""}
                </span>
            </>
        )
    }
}