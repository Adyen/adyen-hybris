import React, {useEffect} from 'react'
import './App.scss';
import ShippingAddress from "./components/shipping-address/ShippingAddress";
import {store} from "./store/store";


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
