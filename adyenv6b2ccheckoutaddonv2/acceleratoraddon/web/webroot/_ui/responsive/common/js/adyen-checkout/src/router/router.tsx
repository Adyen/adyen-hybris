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
import NotificationWrapper from "../components/common/NotificationWrapper";
import {CheckoutSteps} from "../types/checkoutStepsEnum";

export const router = createBrowserRouter([
    {
        path: routes.shippingAddress,
        element: <TranslationWrapper><NotificationWrapper
            checkoutStep={CheckoutSteps.SHIPPING_ADDRESS}><CheckoutStepWrapper><ShippingAddressStep/></CheckoutStepWrapper></NotificationWrapper></TranslationWrapper>,
    }, {
        path: routes.shippingAddress + "/missingDataRedirect",
        element: <TranslationWrapper><NotificationWrapper redirectOnMissingData={true}
            checkoutStep={CheckoutSteps.SHIPPING_ADDRESS}><CheckoutStepWrapper><ShippingAddressStep/></CheckoutStepWrapper></NotificationWrapper></TranslationWrapper>,
    },
    {
        path: routes.shippingMethod,
        element: <TranslationWrapper><NotificationWrapper
            checkoutStep={CheckoutSteps.SHIPPING_METHOD}><CheckoutStepWrapper><ShippingMethodStep/></CheckoutStepWrapper></NotificationWrapper></TranslationWrapper>,
    },
    {
        path: routes.shippingMethod + "/missingDataRedirect",
        element: <TranslationWrapper><NotificationWrapper redirectOnMissingData={true}
            checkoutStep={CheckoutSteps.SHIPPING_METHOD}><CheckoutStepWrapper><ShippingMethodStep/></CheckoutStepWrapper></NotificationWrapper></TranslationWrapper>,
    },
    {
        path: routes.paymentMethod,
        element: <TranslationWrapper><NotificationWrapper
            checkoutStep={CheckoutSteps.PAYMENT_METHOD}><CheckoutStepWrapper><PaymentStep/></CheckoutStepWrapper></NotificationWrapper></TranslationWrapper>,
    },
    {
        path: routes.thankYouPage,
        element: <TranslationWrapper><NotificationWrapper
            checkoutStep={CheckoutSteps.THANK_YOU_PAGE}><ThankYouPageStoreWrapper/></NotificationWrapper></TranslationWrapper>,
    },
    {
        path: routes.thankYouPage + "/:orderCode",
        element: <TranslationWrapper><NotificationWrapper
            checkoutStep={CheckoutSteps.THANK_YOU_PAGE}><ThankYouPageUrlWrapper/></NotificationWrapper></TranslationWrapper>,
    },
    {
        //in case url doesn't match - redirect to shipping address
        path: "*",
        element: <Navigate to={routes.shippingAddress}/>
    }
]);