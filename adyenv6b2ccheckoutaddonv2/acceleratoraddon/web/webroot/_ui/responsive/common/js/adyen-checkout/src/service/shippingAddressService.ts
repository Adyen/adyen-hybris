import axios, {AxiosResponse} from "axios";
import {CSRFToken, urlContextPath} from "../util/baseUrlUtil";
import {AddressModel} from "../reducers/types";
import {store} from "../store/store";
import {isNotEmpty} from "../util/stringUtil";

export class ShippingAddressService {


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
            .catch(() => console.error("Address book fetch error"))
    }

    static selectAddress(addressId: string) {
        axios.post(urlContextPath + '/api/checkout/delivery-address', addressId, {
            headers: {
                'Content-Type': 'text/plain',
                'CSRFToken': CSRFToken
            }
        })
            .catch(() => console.error('Error on address select'))
    }

    static addAddress(address: AddressModel, saveInAddressBook: boolean, isShippingAddress: boolean, isBillingAddress: boolean,
                      editAddress: boolean) {
        const payload = this.mapAddressModelToAddressForm(address, saveInAddressBook, isShippingAddress, isBillingAddress, editAddress);
        axios.post(urlContextPath + '/api/account/delivery-address', payload, {
            headers: {
                'Content-Type': 'application/json; charset=utf-8',
                // 'Accept': 'application/json',
                'CSRFToken': CSRFToken
            }
        })
            .catch(() => console.error('Error on address select'))
    }

    private static mapResponseDataToModel(data: AddressBookResponseData[]): AddressModel[] {
        return data.map(it => this.mapAddressBookDataToAddressModel(it));
    }

    private static mapAddressBookDataToAddressModel(responseData: AddressBookResponseData): AddressModel {
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
}

type AddressBookResponseData = Omit<AddressModel, 'country'> & {
    country: { isocode: string, name: string },
    titleCode: string,
    town: string,
    phone: string
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