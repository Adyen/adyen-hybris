import {Reducer} from "@reduxjs/toolkit";
import {ShippingAddressAction, addressInitialState, shippingAddressReducer} from "./shippingAddressReducer";
import {Action} from "redux";
import {
    AddressConfigAction,
    addressConfigInitialState,
    AddressConfigModel,
    addressConfigReducer
} from "./addressConfigReducer";
import {AddressModel, ShippingMethodState} from "./types";
import {AddressBookAction, addressBookInitialState, addressBookReducer} from "./addressBookReducer";
import {shippingMethodInitialState, shippingMethodReducer, ShippingModeAction} from "./shippingMethodReducer";
import {CartData} from "../types/cartData";
import {CartDataAction, cartDataInitialState, cartDataReducer} from "./cartDataReducer";
import {BillingAddressAction, billingAddressReducer} from "./billingAddressReducer";
import {AdyenConfigAction, adyenConfigInitialState, adyenConfigReducer} from "./adyenConfigReducer";
import {AdyenConfigData} from "../types/adyenConfigData";
import {PlaceOrderData} from "../types/placeOrderData";
import {PlaceOrderDataAction, placeOrderDataInitialState, placeOrderDataReducer} from "./placeOrderDataReducer";

export const initialState: AppState = {
    shippingAddress: addressInitialState,
    addressConfig: addressConfigInitialState,
    addressBook: addressBookInitialState,
    shippingMethod: shippingMethodInitialState,
    cartData: cartDataInitialState,
    billingAddress: addressInitialState,
    adyenConfig: adyenConfigInitialState,
    placeOrderData: placeOrderDataInitialState
}


export const rootReducer: Reducer<AppState, RootAction, AppState> = function (appState: AppState, action: RootAction): AppState {
    return {
        shippingAddress: shippingAddressReducer(appState.shippingAddress, action),
        addressConfig: addressConfigReducer(appState.addressConfig, action),
        addressBook: addressBookReducer(appState.addressBook, action),
        shippingMethod: shippingMethodReducer(appState.shippingMethod, action),
        cartData: cartDataReducer(appState.cartData, action),
        billingAddress: billingAddressReducer(appState.billingAddress, action),
        adyenConfig: adyenConfigReducer(appState.adyenConfig, action),
        placeOrderData: placeOrderDataReducer(appState.placeOrderData,action)
    }
}

export interface AppState {
    shippingAddress: AddressModel
    addressConfig: AddressConfigModel
    addressBook: AddressModel[]
    shippingMethod: ShippingMethodState,
    cartData: CartData,
    billingAddress: AddressModel,
    adyenConfig: AdyenConfigData,
    placeOrderData: PlaceOrderData
}

export type RootAction =
    ShippingAddressAction
    | AddressConfigAction
    | AddressBookAction
    | ShippingModeAction
    | CartDataAction
    | BillingAddressAction
    | AdyenConfigAction
    | PlaceOrderDataAction

export interface PayloadAction<T extends string, PT = string> extends Action<T> {
    payload: PT
}