import axios, {AxiosError} from "axios";
import {CSRFToken, urlContextPath} from "../util/baseUrlUtil";
import {AddressModel} from "../reducers/types";
import {store} from "../store/store";
import {isNotEmpty} from "../util/stringUtil";
import {AddressConfigModel} from "../reducers/addressConfigReducer";
import {AddressData} from "../types/addressData";
import {ErrorResponse} from "../types/errorResponse";
import {ErrorHandler} from "../components/common/ErrorHandler";

export class AddressService {

    static fetchAddressBook() {
        axios.get(urlContextPath + '/api/account/delivery-address', {
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        })
            .then(response => {
                let addressModels = this.mapResponseDataToModel(response.data);
                store.dispatch({type: "addressBook/setAddressBook", payload: addressModels})
            })
            .catch((errorResponse:AxiosError<ErrorResponse>) => {
                ErrorHandler.handleError(errorResponse)
                console.error("Address book fetch error")
                return false
            })
    // catch(() => console.error("Address book fetch error"))
    }

    static async selectDeliveryAddress(addressId: string) {
        return axios.post(urlContextPath + '/api/checkout/delivery-address', addressId, {
            headers: {
                'Content-Type': 'text/plain',
                'CSRFToken': CSRFToken
            }
        })
            .then(() => true)
            .catch((errorResponse:AxiosError<ErrorResponse>) => {
                ErrorHandler.handleError(errorResponse)
                console.error('Error on address select')
                return false
            })
            // catch(() => {
            //     console.error('Error on address select')
            //     return false
            // })
    }

    static async addDeliveryAddress(address: AddressModel, saveInAddressBook: boolean, isShippingAddress: boolean, isBillingAddress: boolean,
                            editAddress: boolean): Promise<boolean> {
        const payload = this.mapAddressModelToAddressForm(address, saveInAddressBook, isShippingAddress, isBillingAddress, editAddress);
        return axios.post(urlContextPath + '/api/account/delivery-address', payload, {
            headers: {
                'Content-Type': 'application/json; charset=utf-8',
                'CSRFToken': CSRFToken
            }
        }).then(() => true)
            .catch((errorResponse:AxiosError<ErrorResponse>) => {   //w serwisach umiescic
                ErrorHandler.handleError(errorResponse)
                console.error('Error on address select')
                return false
            })
    }

    static fetchAddressConfig() {
        axios.get(urlContextPath + '/api/configuration/shipping-address', {
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        })
            .then(response => {
                let addressConfigModel = this.mapAddressConfigurationResponse(response.data);
                store.dispatch({type: "addressConfig/setAddressConfig", payload: addressConfigModel})
            })
            .catch((errorResponse:AxiosError<ErrorResponse>) => {
                ErrorHandler.handleError(errorResponse)
                console.error("Address config fetch error")
                return false
            })
            // catch(() => console.error("Address config fetch error"))
    }

    private static mapResponseDataToModel(data: AddressData[]): AddressModel[] {
        return data.map(it => this.mapAddressBookDataToAddressModel(it));
    }

    private static mapAddressBookDataToAddressModel(responseData: AddressData): AddressModel {
        return {
            id: responseData.id,
            firstName: responseData.firstName,
            lastName: responseData.lastName,
            titleCode: responseData.titleCode,
            title: responseData.title,
            countryCode: responseData.country.isocode,
            country: responseData.country.name,
            line1: responseData.line1,
            line2: responseData.line2,
            city: responseData.town,
            postalCode: responseData.postalCode,
            phoneNumber: responseData.phone
        }
    }

    private static mapAddressModelToAddressForm(addressModel: AddressModel, saveInAddressBook: boolean, isShippingAddress: boolean,
                                                isBillingAddress: boolean, editAddress: boolean): AddressForm {
        return {
            addressId: isNotEmpty(addressModel.id) ? addressModel.id : null,
            titleCode: addressModel.titleCode,
            firstName: addressModel.firstName,
            lastName: addressModel.lastName,
            line1: addressModel.line1,
            line2: addressModel.line2,
            townCity: addressModel.city,
            regionIso: null,
            postcode: addressModel.postalCode,
            countryIso: addressModel.countryCode,
            saveInAddressBook: saveInAddressBook,
            shippingAddress: isShippingAddress,
            billingAddress: isBillingAddress,
            editAddress: editAddress,
            phone: addressModel.phoneNumber,
            defaultAddress: false,
        }
    }

    private static mapAddressConfigurationResponse(response: AddressConfigResponse): AddressConfigModel {
        let titles = response.titles.map(title => {
            return {code: title.code, value: title.name}
        });

        let countries = response.countries.map(country => {
            return {code: country.isocode, value: country.name}
        });

        return {
            anonymousUser: response.anonymous,
            titles: titles,
            countries: countries,
        }
    }
}

interface AddressForm {
    addressId: string
    titleCode: string
    firstName: string
    lastName: string
    line1: string
    line2: string
    townCity: string
    regionIso: string
    postcode: string
    countryIso: string
    saveInAddressBook: boolean
    defaultAddress: boolean
    shippingAddress: boolean
    billingAddress: boolean
    editAddress: boolean
    phone: string
}

interface CountryData {
    isocode: string,
    name: string
}

interface TitleData {
    code: string,
    name: string
}

interface AddressConfigResponse {
    anonymous: boolean,
    countries: CountryData[],
    titles: TitleData[]
}
