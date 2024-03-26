import {createBrowserRouter, Navigate} from "react-router-dom";
import React from "react";
import {ShippingAddressStep} from "../components/steps/ShippingAddressStep";
import {PaymentStep} from "../components/steps/PaymentStep";
import {routes} from "./routes";
import CheckoutStepWrapper from "../CheckoutStepWrapper";
import {ShippingMethodStep} from "../components/steps/ShippingMethodStep";
import {ThankYouPageUrlWrapper} from "../components/thank-you-page/ThankYouPageUrlWrapper";
import ThankYouPageStoreWrapper from "../components/thank-you-page/ThankYouPageStoreWrapper";
import {TranslationWrapper} from "../components/common/TranslationWrapper";

export const router = createBrowserRouter([
    {
        path: routes.shippingAddress,
        element: <TranslationWrapper><CheckoutStepWrapper><ShippingAddressStep/></CheckoutStepWrapper></TranslationWrapper>,
    },
    {
        path: routes.shippingMethod,
        element: <TranslationWrapper><CheckoutStepWrapper><ShippingMethodStep/></CheckoutStepWrapper></TranslationWrapper>,
    },
    {
        path: routes.paymentMethod,
        element: <TranslationWrapper><CheckoutStepWrapper><PaymentStep/></CheckoutStepWrapper></TranslationWrapper>,
    },
    {
        path: routes.thankYouPage,
        element: <TranslationWrapper><ThankYouPageStoreWrapper/></TranslationWrapper>,
    },
    {
        path: routes.thankYouPage + "/:orderCode",
        element: <TranslationWrapper><ThankYouPageUrlWrapper/></TranslationWrapper>,
    },
    {
        //in case url doesn't match - redirect to shipping address
        path: "*",
        element: <Navigate to={routes.shippingAddress}/>
    }
]);