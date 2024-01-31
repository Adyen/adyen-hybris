import React from 'react'
import './App.scss';
import ShippingAddress from "./components/shipping-address/ShippingAddress";


function App() {

    return (
        <div className="App">
            <div className={"checkout-steps"}>
                <ShippingAddress/>
            </div>
        </div>
    );
}

export default App;
