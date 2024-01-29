import React, {useEffect} from 'react'
import './App.scss';
import ShippingAddress from "./components/shipping-address/ShippingAddress";
import {store} from "./store/store";


function App() {
    useEffect(() => {
        const rootElement = document.getElementById('root');
        const anonymousUserString = rootElement.getAttribute('anonymous-user');

        store.dispatch({type: "addressConfig/setAnonymousUser", payload: anonymousUserString === 'true'})
    })


    return (
        <div className="App">
            <div className={"checkout-steps"}>
                <ShippingAddress/>
            </div>
        </div>
    );
}

export default App;
