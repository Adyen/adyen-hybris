import React from 'react'
import './App.scss';
import CartDetails from "./components/cart-details/CartDetails";


class App extends React.Component<React.PropsWithChildren, null> {
    render() {
        return (
            <>
                <div className="col-sm-6">
                    <div className="checkout-headline">
                        <span className="glyphicon glyphicon-lock"></span>
                        Secure Checkout
                    </div>
                    {this.props.children}
                </div>

                <div className="col-sm-6 hidden-xs">
                    <CartDetails/>
                </div>
            </>
        );
    }
}

export default App;
