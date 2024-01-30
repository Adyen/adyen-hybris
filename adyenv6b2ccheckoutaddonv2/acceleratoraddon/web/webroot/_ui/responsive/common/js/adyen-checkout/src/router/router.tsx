import {createBrowserRouter} from "react-router-dom";
import App from "../App";
import React from "react";
import {urlContextPath} from "../util/baseUrlUtil";

export const router = createBrowserRouter([
    {
        // eslint-disable-next-line no-undef
        path: urlContextPath + "/checkout/multi/adyen",
        element: <App />,
    },
]);

