import React from "react";
import {CheckoutSteps} from "../../types/checkoutStepsEnum";
import {connect} from "react-redux";
import {AppState} from "../../reducers/rootReducer";
import {CartData} from "../../types/cartData";
import {Navigate} from "react-router-dom";
import {routes} from "../../router/routes";

interface ComponentProps {
    currentCheckoutStep: CheckoutSteps
}

interface StoreProps {
    cartData: CartData,
    cartDataLoading: boolean
}

type Props = ComponentProps & StoreProps & React.PropsWithChildren;

interface State {
    redirectToShippingAddress: boolean;
    redirectToShippingMethod: boolean;
    renderChildren: boolean;
}

class RedirectOnIncompleteData extends React.Component<Props, State> {

    constructor(props: Props) {
        super(props);
        this.state = {
            redirectToShippingAddress: false,
            redirectToShippingMethod: false,
            renderChildren: false,
        }
    }

    componentDidUpdate(prevProps: Readonly<Props>, prevState: Readonly<State>, snapshot?: any) {
        if (prevProps.cartDataLoading === true && this.props.cartDataLoading === false) {
            let noRedirect = this.evaluateRedirects();
            if (noRedirect) {
                this.setState({...this.state, renderChildren: true})
            }
        }
    }

    private evaluateRedirects() {
        let noRedirect = true;
        if (this.props.currentCheckoutStep === CheckoutSteps.SHIPPING_METHOD) {
            if (!this.props.cartData.deliveryAddress) {
                this.setState({...this.state, redirectToShippingAddress: true})
                noRedirect = false;
            }
        }
        if (this.props.currentCheckoutStep === CheckoutSteps.PAYMENT_METHOD) {
            if (!this.props.cartData.deliveryAddress) {
                this.setState({...this.state, redirectToShippingAddress: true})
                noRedirect = false;
            } else if (!this.props.cartData.deliveryMode) {
                this.setState({...this.state, redirectToShippingMethod: true})
                noRedirect = false;
            }
        }
        return noRedirect;
    }

    render() {
        if (this.state.redirectToShippingAddress) {
            return <Navigate to={routes.shippingAddress}/>
        }
        if (this.state.redirectToShippingMethod) {
            return <Navigate to={routes.shippingMethod}/>
        }
        if (this.state.renderChildren) {
            return <>{this.props.children}</>
        }
        return <></>
    }
}

function mapStateToProps(appState: AppState): StoreProps {
    return {
        cartData: appState.cartData,
        cartDataLoading: appState.loadingState.loadingCartData
    }
}

export default connect(mapStateToProps)(RedirectOnIncompleteData)