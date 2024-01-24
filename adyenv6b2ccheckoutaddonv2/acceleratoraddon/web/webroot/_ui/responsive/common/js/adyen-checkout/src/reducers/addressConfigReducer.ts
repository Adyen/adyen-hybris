import {PayloadAction, RootAction} from "./rootReducer";
import {CodeValueItem} from "./types";
import {countriesMock, titlesMock} from "./mockAddressConfig";


export const addressConfigInitialState: AddressConfigModel = {
    titles: titlesMock,
    countries: countriesMock,
    anonymousUser: false
}

export function addressConfigReducer(addressConfigState: AddressConfigModel, action: RootAction): AddressConfigModel {
    switch (action.type) {

        case "addressConfig/setAnonymousUser":
            return {
                ...addressConfigState,
                anonymousUser: action.payload
            }

        default:
            return addressConfigState
    }

}

export interface AddressConfigModel {
    titles: CodeValueItem[]
    countries: CodeValueItem[]
    anonymousUser: boolean
}

interface SetAnonymousUserAction extends PayloadAction<"addressConfig/setAnonymousUser", boolean> {}


export type AddressConfigAction = SetAnonymousUserAction

