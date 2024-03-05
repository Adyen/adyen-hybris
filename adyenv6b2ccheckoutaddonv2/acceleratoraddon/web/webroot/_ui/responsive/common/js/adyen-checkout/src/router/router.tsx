import {createBrowserRouter, Navigate} from "react-router-dom";
import React from "react";
import {ShippingAddressStep} from "../components/steps/ShippingAddressStep";
import {PaymentStep} from "../components/steps/PaymentStep";
import {FinalStep} from "../components/steps/FinalStep";
import {routes} from "./routes";
import App from "../App";
import {ShippingMethodStep} from "../components/steps/ShippingMethodStep";
import ThankYouPage from "../components/thank-you-page/ThankYouPage";

export const router = createBrowserRouter([
    {
        path: routes.shippingAddress,
        element: <App><ShippingAddressStep/></App>,
    },
    {
        path: routes.shippingMethod,
        element: <App><ShippingMethodStep/></App>,
    },
    {
        path: routes.paymentMethod,
        element: <App><PaymentStep/></App>,
    },
    {
        path: routes.thankYouPage,
        element: <ThankYouPage/>,
    },
    {
        //in case url doesn't match - redirect to shipping address
        path: "*",
        element: <Navigate to={routes.shippingAddress}/>
    }
]);