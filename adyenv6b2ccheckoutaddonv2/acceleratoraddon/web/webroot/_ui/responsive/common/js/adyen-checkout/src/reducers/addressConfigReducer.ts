import {PayloadAction, RootAction} from "./rootReducer";
import {CodeValueItem} from "./types";

export const addressConfigInitialState: AddressConfigModel = {
    titles: [],
    countries: [],
    anonymousUser: false
}

export function addressConfigReducer(addressConfigState: AddressConfigModel, action: RootAction): AddressConfigModel {
    switch (action.type) {

        case "addressConfig/setAnonymousUser":
            return {
                ...addressConfigState,
                anonymousUser: action.payload
            }

        case "addressConfig/setAddressConfig":
            return action.payload

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
interface SetAddressConfigAction extends PayloadAction<"addressConfig/setAddressConfig", AddressConfigModel> {}


export type AddressConfigAction = SetAnonymousUserAction | SetAddressConfigAction

