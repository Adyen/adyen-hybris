import React from "react";
import {InputDropdown} from "../controls/InputDropdown";
import {ShippingMethodHeader} from "../headers/ShippingMethodHeader";
import {ShippingMethodService} from "../../service/shippingMethodService";
import {connect} from "react-redux";
import {AppState} from "../../reducers/rootReducer";
import {CodeValueItem, ShippingMethodModel} from "../../reducers/types";
import HTMLReactParser from "html-react-parser";
import {StoreDispatch} from "../../store/store";
import {routes} from "../../router/routes";
import {Navigate} from "react-router-dom";
import {ShippingMethodAddressHeading} from "./ShippingMethodAddressHeading";
import {AddressData} from "../../types/addressData";

interface StoreProps {
    shippingMethods: ShippingMethodModel[],
    selectedShippingMethodCode: string,
    shippingAddress: AddressData
}

interface DispatchProps {
    setShippingMethod: (code: string) => void;
}

type Props = StoreProps & DispatchProps

interface State {
    redirectToNextStep: boolean
}

class ShippingMethod extends React.Component<Props, State> {

    constructor(props: Props) {
        super(props);
        this.state = {redirectToNextStep: false}
    }

    componentDidMount() {
        ShippingMethodService.fetchShippingMethods();
    }

    private getDropdownItems(): CodeValueItem[] {
        return this.props.shippingMethods.map(shippingMethod => {
            return {
                code: shippingMethod.code,
                value: `${shippingMethod.name}\xa0-\xa0${shippingMethod.description}\xa0-\xa0${shippingMethod.deliveryCost.formattedValue}`
            }
        })
    }

    private async handleSubmitButton() {
        let success = await ShippingMethodService.selectShippingMethod(this.props.selectedShippingMethodCode);
        if (success) {
            this.setState({...this.state, redirectToNextStep: true})
        }
    }

    render() {
        if (this.state.redirectToNextStep) {
            return <Navigate to={routes.paymentMethod}/>
        }

        return (
            <>
                <ShippingMethodHeader isActive={true}/>

                <div className={"step-body"}>

                    <div className={"checkout-shipping"}>
                        <div className="checkout-shipping-items row">
                            <div className={"col-sm-12"}>
                                <ShippingMethodAddressHeading address={this.props.shippingAddress}/>
                            </div>
                        </div>
                        <hr/>
                        <div className={"checkout-indent"}>
                            <div className={"headline"}>Shipment Method</div>
                            <InputDropdown values={this.getDropdownItems()}
                                           onChange={(code) => {
                                               this.props.setShippingMethod(code)
                                           }}
                                           selectedValue={this.props.selectedShippingMethodCode}/>
                        </div>
                        <p>{HTMLReactParser("Items will ship as soon as they are available. <br> See Order Summary for more information.")}</p>
                    </div>
                    <button className={"btn btn-primary btn-block checkout-next"}
                            onClick={() => this.handleSubmitButton()}>NEXT
                    </button>
                </div>
            </>
        )
    }
}

const mapStateToProps = (state: AppState): StoreProps => ({
    shippingMethods: state.shippingMethod.shippingMethods,
    selectedShippingMethodCode: state.shippingMethod.selectedShippingMethodCode,
    shippingAddress: state.cartData.deliveryAddress
})

const mapDispatchToProps = (dispatch: StoreDispatch): DispatchProps => ({
    setShippingMethod: (code: string) => dispatch({type: "shippingMethod/setShippingMethod", payload: code}),
});

export default connect(mapStateToProps, mapDispatchToProps)(ShippingMethod)