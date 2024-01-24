export interface AddressModel {
    id: string
    country: string
    countryCode: string
    title: string
    titleCode: string
    firstName: string
    lastName: string
    line1: string
    line2: string
    city: string
    postalCode: string
    phoneNumber: string
}

export interface CodeValueItem<CT = string, VT = string> {
    code: CT
    value: VT
}