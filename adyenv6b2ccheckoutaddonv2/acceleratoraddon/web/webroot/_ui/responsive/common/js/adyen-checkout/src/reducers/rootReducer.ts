import {Reducer} from "@reduxjs/toolkit";
import {AddressAction, addressInitialState, shippingAddressReducer} from "./shippingAddressReducer";
import {Action} from "redux";
import {
    AddressConfigAction,
    addressConfigInitialState,
    AddressConfigModel,
    addressConfigReducer
} from "./addressConfigReducer";
import {AddressModel} from "./types";
import {AddressBookAction, addressBookInitialState, addressBookReducer} from "./addressBookReducer";

export const initialState: AppState = {
    shippingAddress: addressInitialState,
    addressConfig: addressConfigInitialState,
    addressBook: addressBookInitialState
}


export const rootReducer: Reducer<AppState, RootAction, AppState> = function (initialState: AppState, action: RootAction): AppState {
    return {
        shippingAddress: shippingAddressReducer(initialState.shippingAddress, action),
        addressConfig: addressConfigReducer(initialState.addressConfig, action),
        addressBook: addressBookReducer(initialState.addressBook, action)
    }
}

export interface AppState {
    shippingAddress: AddressModel
    addressConfig: AddressConfigModel
    addressBook: AddressModel[]
}

export type RootAction = AddressAction | AddressConfigAction | AddressBookAction

export interface PayloadAction<T extends string, PT = string> extends Action<T> {
    payload: PT
}