import {AppState} from "../../reducers/rootReducer";
import {connect} from "react-redux";
import {PlaceOrderData} from "../../types/placeOrderData";
import React from "react";
import {ThankYouPage} from "./ThankYouPage";

interface StoreProps {
    placeOrderData: PlaceOrderData
}


class ThankYouPageStoreWrapper extends React.Component<StoreProps, null> {
    render() {
        return <ThankYouPage orderCode={this.props.placeOrderData.orderNumber}/>
    }
}

function mapStateToProps(state: AppState): StoreProps {
    return {
        placeOrderData: state.placeOrderData
    }
}

export default connect(mapStateToProps)(ThankYouPageStoreWrapper)