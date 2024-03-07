import {AppState} from "../../reducers/rootReducer";
import {connect} from "react-redux";
import {PlaceOrderData} from "../../types/placeOrderData";
import React from "react";

interface StoreProps {
    placeOrderData: PlaceOrderData
}


class ThankYouPage extends React.Component<StoreProps, null> {
    render() {
        return (
            <div>
            <span>
                Order number: {this.props.placeOrderData.orderNumber}
            </span>
                <span>
                Error message: {this.props.placeOrderData.error}
            </span>
            </div>)
    }
}

function mapStateToProps(state: AppState): StoreProps {
    return {
        placeOrderData: state.placeOrderData
    }
}

export default connect(mapStateToProps)(ThankYouPage)