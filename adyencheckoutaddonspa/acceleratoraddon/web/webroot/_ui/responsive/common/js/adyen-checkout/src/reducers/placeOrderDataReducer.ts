import {PayloadAction, RootAction} from "./rootReducer";
import {PlaceOrderData} from "../types/placeOrderData";

export const placeOrderDataInitialState: PlaceOrderData = {
    error: "",
    orderNumber: ""
}

export function placeOrderDataReducer(placeOrderDataState: PlaceOrderData, action: RootAction): PlaceOrderData {
    switch (action.type) {
        case "placeOrderData/setPlaceOrderData": {
            return action.payload
        }

        default:
            return placeOrderDataState
    }
}

export interface SetPlaceOrderDataAction extends PayloadAction<"placeOrderData/setPlaceOrderData", PlaceOrderData> {
}

export type PlaceOrderDataAction = SetPlaceOrderDataAction