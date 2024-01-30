import {PayloadAction, RootAction} from "./rootReducer";
import {AddressModel} from "./types";


export const addressInitialState: AddressModel = {
    id: "",
    firstName: "",
    line1: "",
    line2: "",
    city: "",
    country: "",
    countryCode: "",
    lastName: "",
    phoneNumber: "",
    postalCode: "",
    title: "",
    titleCode: ""
}

export function shippingAddressReducer(addressState: AddressModel, action: RootAction): AddressModel {
    switch (action.type) {
        case "shippingAddress/setFirstName":
            return {
                ...addressState,
                firstName: action.payload
            }

        case "shippingAddress/setLastName":
            return {
                ...addressState,
                lastName: action.payload
            }

        case "shippingAddress/setCountryCode":
            return {
                ...addressState,
                countryCode: action.payload
            }

        case "shippingAddress/setTitleCode":
            return {
                ...addressState,
                titleCode: action.payload
            }

        case "shippingAddress/setLine1":
            return {
                ...addressState,
                line1: action.payload
            }

        case "shippingAddress/setLine2":
            return {
                ...addressState,
                line2: action.payload
            }

        case "shippingAddress/setCity":
            return {
                ...addressState,
                city: action.payload
            }

        case "shippingAddress/setPostCode":
            return {
                ...addressState,
                postalCode: action.payload
            }

        case "shippingAddress/setPhoneNumber":
            return {
                ...addressState,
                phoneNumber: action.payload
            }

        case "shippingAddress/setAddress":
            return action.payload

        default:
            return addressState;
    }
}


export interface SetFirstNameAction extends PayloadAction<"shippingAddress/setFirstName"> {}
export interface SetLastNameAction extends PayloadAction<"shippingAddress/setLastName"> {}
export interface SetCountryCodeAction extends PayloadAction<"shippingAddress/setCountryCode"> {}
export interface SetTitleCodeAction extends PayloadAction<"shippingAddress/setTitleCode"> {}
export interface SetLine1Action extends PayloadAction<"shippingAddress/setLine1"> {}
export interface SetLine2Action extends PayloadAction<"shippingAddress/setLine2"> {}
export interface SetCityAction extends PayloadAction<"shippingAddress/setCity"> {}
export interface SetPostCodeAction extends PayloadAction<"shippingAddress/setPostCode"> {}
export interface SetPhoneNumberAction extends PayloadAction<"shippingAddress/setPhoneNumber"> {}
export interface SetAddress extends PayloadAction<"shippingAddress/setAddress", AddressModel> {}



export type AddressAction = SetFirstNameAction | SetLastNameAction | SetCountryCodeAction | SetTitleCodeAction | SetLine1Action |
    SetLine2Action | SetCityAction | SetPostCodeAction | SetPhoneNumberAction | SetAddress