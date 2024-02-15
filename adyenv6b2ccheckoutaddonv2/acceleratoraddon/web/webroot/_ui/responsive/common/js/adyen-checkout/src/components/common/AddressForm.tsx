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
            <InputDropdown values={this.props.addressConfig.countries} fieldName={"COUNTRY/REGION"}
                           onChange={this.props.onCountryCodeChange}
                           selectedValue={this.props.address.countryCode}
                           placeholderText={"Country/Region"} placeholderDisabled={true}/>
            <InputDropdown values={this.props.addressConfig.titles} fieldName={"Title"}
                           onChange={this.props.onTitleCodeChange}
                           selectedValue={this.props.address.titleCode}
                           placeholderText={"None"}/>
            <InputText fieldName={"First name"}
                       onChange={this.props.onFirstNameChange}
                       value={this.props.address.firstName}/>
            <InputText fieldName={"Last name"}
                       onChange={this.props.onLastNameChange}
                       value={this.props.address.lastName}/>
            <InputText fieldName={"ADDRESS LINE 1"}
                       onChange={this.props.onLine1Change}
                       value={this.props.address.line1}/>
            <InputText fieldName={"ADDRESS LINE 2 (OPTIONAL)"}
                       onChange={this.props.onLine2Change}
                       value={this.props.address.line2}/>
            <InputText fieldName={"CITY"}
                       onChange={this.props.onCityChange}
                       value={this.props.address.city}/>
            <InputText fieldName={"POST CODE"}
                       onChange={this.props.onPostCodeChange}
                       value={this.props.address.postalCode}/>
            <InputText fieldName={"PHONE NUMBER"}
                       onChange={this.props.onPhoneNumberChange}
                       value={this.props.address.phoneNumber}/>
        </>;
    }
}