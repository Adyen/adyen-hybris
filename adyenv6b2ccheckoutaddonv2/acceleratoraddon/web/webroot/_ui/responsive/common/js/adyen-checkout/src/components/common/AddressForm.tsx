import {InputDropdown} from "../controls/InputDropdown";
import {InputText} from "../controls/InputText";
import React from "react";
import {AddressConfigModel} from "../../reducers/addressConfigReducer";
import {AddressModel} from "../../reducers/types";

interface Props {
    addressConfig: AddressConfigModel,
    address: AddressModel,
    onCountryCodeChange: (countryCode: string) => void,
    onTitleCodeChange: (titleCode: string) => void,
    onFirstNameChange: (firstName: string) => void,
    onLastNameChange: (lastName: string) => void,
    onLine1Change: (line1: string) => void,
    onLine2Change: (line2: string) => void,
    onCityChange: (city: string) => void,
    onPostCodeChange: (postCode: string) => void,
    onPhoneNumberChange: (phoneNumber: string) => void
}

export class AddressForm extends React.Component<Props, any> {

    render() {
        return <>
            <InputDropdown testId={"address.country"}
                           values={this.props.addressConfig.countries} fieldName={"COUNTRY/REGION"}
                           onChange={(countryCode) => this.props.onCountryCodeChange(countryCode)}
                           selectedValue={this.props.address.countryCode}
                           placeholderText={"Country/Region"} placeholderDisabled={true}/>
            <InputDropdown testId={"address.title"}
                           values={this.props.addressConfig.titles} fieldName={"Title"}
                           onChange={(titleCode) => this.props.onTitleCodeChange(titleCode)}
                           selectedValue={this.props.address.titleCode}
                           placeholderText={"None"}/>
            <InputText testId={"address.firstName"}
                       fieldName={"First name"}
                       onChange={(firstName) => this.props.onFirstNameChange(firstName)}
                       value={this.props.address.firstName}/>
            <InputText testId={"address.surname"}
                       fieldName={"Last name"}
                       onChange={(lastName) => this.props.onLastNameChange(lastName)}
                       value={this.props.address.lastName}/>
            <InputText testId={"address.line1"}
                       fieldName={"ADDRESS LINE 1"}
                       onChange={(line1) => this.props.onLine1Change(line1)}
                       value={this.props.address.line1}/>
            <InputText testId={"address.line2"}
                       fieldName={"ADDRESS LINE 2 (OPTIONAL)"}
                       onChange={(line2) => this.props.onLine2Change(line2)}
                       value={this.props.address.line2}/>
            <InputText testId={"address.townCity"}
                       fieldName={"CITY"}
                       onChange={(city) => this.props.onCityChange(city)}
                       value={this.props.address.city}/>
            <InputText testId={"address.postcode"}
                       fieldName={"POST CODE"}
                       onChange={(postCode) => this.props.onPostCodeChange(postCode)}
                       value={this.props.address.postalCode}/>
            <InputText testId={"address.phone"}
                       fieldName={"PHONE NUMBER"}
                       onChange={(phoneNumber) => this.props.onPhoneNumberChange(phoneNumber)}
                       value={this.props.address.phoneNumber}/>
        </>;
    }
}