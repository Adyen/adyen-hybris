import {createBrowserRouter, Navigate} from "react-router-dom";
import React from "react";
import {ShippingAddressStep} from "../components/steps/ShippingAddressStep";
import {PaymentStep} from "../components/steps/PaymentStep";
import {routes} from "./routes";
import App from "../App";
import {ShippingMethodStep} from "../components/steps/ShippingMethodStep";
import {ThankYouPageUrlWrapper} from "../components/thank-you-page/ThankYouPageUrlWrapper";
import ThankYouPageStoreWrapper from "../components/thank-you-page/ThankYouPageStoreWrapper";

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
        element: <ThankYouPageStoreWrapper/>,
    },
    {
        path: routes.thankYouPage + "/:orderCode",
        element: <ThankYouPageUrlWrapper/>,
    },
    {
        //in case url doesn't match - redirect to shipping address
        path: "*",
        element: <Navigate to={routes.shippingAddress}/>
    }
]);