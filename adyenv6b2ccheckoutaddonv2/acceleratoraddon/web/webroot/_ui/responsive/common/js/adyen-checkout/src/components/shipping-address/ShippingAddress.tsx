import React from "react";
import {InputText} from "../controls/InputText";
import {InputDropdown} from "../controls/InputDropdown";
import {connect} from "react-redux";
import {AppState} from "../../reducers/rootReducer";
import {StoreDispatch} from "../../store/store";
import {AddressConfigModel} from "../../reducers/addressConfigReducer";
import {InputCheckbox} from "../controls/InputCheckbox";
import {ShippingAddressService} from "../../service/shippingAddressService";
import {AddressModel} from "../../reducers/types";
import AddressBookSelector from "./AddressBookSelector";


interface StoreProps {
    shippingAddress: AddressModel
    addressConfig: AddressConfigModel
    addressBook: AddressModel[]
}

interface DispatchProps {
    setFirstName: (firstName: string) => void
    setLastName: (lastName: string) => void
    setCountryCode: (countryCode: string) => void
    setTitleCode: (titleCode: string) => void
    setLine1: (line1: string) => void
    setLine2: (line2: string) => void
    setCity: (city: string) => void
    setPostCode: (postCode: string) => void
    setPhoneNumber: (phoneNumber: string) => void
}

interface ShippingAddressProps extends StoreProps, DispatchProps {
}

interface ShippingAddressState {
    addressBookModalOpen: boolean
    saveInAddressBook: boolean
}

class ShippingAddress extends React.Component<ShippingAddressProps, ShippingAddressState> {

    constructor(props: ShippingAddressProps) {
        super(props);
        this.state = {
            addressBookModalOpen: false,
            saveInAddressBook: false
        }
    }

    componentDidMount() {
        ShippingAddressService.fetchAddressBook()
        ShippingAddressService.fetchAddressConfig()
    }

    private openAddressBookModal() {
        this.setState({...this.state, addressBookModalOpen: true})
    }

    private closeAddressBookModal() {
        this.setState({...this.state, addressBookModalOpen: false})
    }

    private onChangeSaveInAddressBook(value: boolean) {
        this.setState({...this.state, saveInAddressBook: value})
    }

    private handleSubmitButton() {
        ShippingAddressService.addAddress(this.props.shippingAddress, this.state.saveInAddressBook, true, false, false)
    }

    private renderSaveAddressCheckbox(): React.JSX.Element {
        if (!this.props.addressConfig.anonymousUser) {
            return <InputCheckbox fieldName={"Save shipping address"}
                                  onChange={(value) => this.onChangeSaveInAddressBook(value)}
                                  checked={this.state.saveInAddressBook}/>
        }
        return <></>
    }

    private renderAddressBookButton(): React.JSX.Element {
        if (this.props.addressBook.length > 0) {
            return <button className={"btn btn-default btn-block"} onClick={() => this.openAddressBookModal()}>Address
                Book</button>
        }
        return <></>
    }

    private renderAddressBookModal(): React.JSX.Element {
        if (this.state.addressBookModalOpen) {
            return <AddressBookSelector closeModal={() => this.closeAddressBookModal()}/>
        } else {
            return <></>
        }
    }

    render() {
        return (
            <>
                <div className={"step-head active"}>
                    <div className={"shippingAddress_stepHeader title"}>
                        Shipment/Pick Up Location
                    </div>
                </div>

                <div className={"step-body"}>
                    {this.renderAddressBookModal()}


                    <div className={"shippingAddress_form checkout-shipping"}>
                        <div className={"checkout-indent"}>
                            <div className={"shippingAddress_form_header headline"}>Shipping Address</div>
                            {this.renderAddressBookButton()}
                            <br/>
                            <InputDropdown values={this.props.addressConfig.countries} fieldName={"COUNTRY/REGION"}
                                           onChange={(countryCode) => this.props.setCountryCode(countryCode)}
                                           selectedValue={this.props.shippingAddress.countryCode}
                                            placeholderText={"Country/Region"} placeholderDisabled={true}/>
                            <InputDropdown values={this.props.addressConfig.titles} fieldName={"Title"}
                                           onChange={(titleCode) => this.props.setTitleCode(titleCode)}
                                           selectedValue={this.props.shippingAddress.titleCode}
                                            placeholderText={"None"}/>
                            <InputText fieldName={"First name"}
                                       onChange={(firstName) => this.props.setFirstName(firstName)}
                                       value={this.props.shippingAddress.firstName}/>
                            <InputText fieldName={"Last name"}
                                       onChange={(lastName) => this.props.setLastName(lastName)}
                                       value={this.props.shippingAddress.lastName}/>
                            <InputText fieldName={"ADDRESS LINE 1"}
                                       onChange={(line1) => this.props.setLine1(line1)}
                                       value={this.props.shippingAddress.line1}/>
                            <InputText fieldName={"ADDRESS LINE 2 (OPTIONAL)"}
                                       onChange={(line2) => this.props.setLine2(line2)}
                                       value={this.props.shippingAddress.line2}/>
                            <InputText fieldName={"CITY"}
                                       onChange={(city) => this.props.setCity(city)}
                                       value={this.props.shippingAddress.city}/>
                            <InputText fieldName={"POST CODE"}
                                       onChange={(postCode) => this.props.setPostCode(postCode)}
                                       value={this.props.shippingAddress.postalCode}/>
                            <InputText fieldName={"PHONE NUMBER"}
                                       onChange={(phoneNumber) => this.props.setPhoneNumber(phoneNumber)}
                                       value={this.props.shippingAddress.phoneNumber}/>
                            {this.renderSaveAddressCheckbox()}
                        </div>
                    </div>
                    <button className={"btn btn-primary btn-block checkout-next"}
                            onClick={() => this.handleSubmitButton()}>NEXT
                    </button>
                </div>
            </>
        )
    }
}

const mapStateToProps = (state: AppState): StoreProps => ({
    shippingAddress: state.shippingAddress,
    addressConfig: state.addressConfig,
    addressBook: state.addressBook
})

const mapDispatchToProps = (dispatch: StoreDispatch): DispatchProps => ({
    setFirstName: (firstName: string) => dispatch({type: "shippingAddress/setFirstName", payload: firstName}),
    setLastName: (lastName: string) => dispatch({type: "shippingAddress/setLastName", payload: lastName}),
    setCountryCode: (country: string) => dispatch({type: "shippingAddress/setCountryCode", payload: country}),
    setTitleCode: (title: string) => dispatch({type: "shippingAddress/setTitleCode", payload: title}),
    setLine1: (line1: string) => dispatch({type: "shippingAddress/setLine1", payload: line1}),
    setLine2: (line2: string) => dispatch({type: "shippingAddress/setLine2", payload: line2}),
    setCity: (city: string) => dispatch({type: "shippingAddress/setCity", payload: city}),
    setPostCode: (postCode: string) => dispatch({type: "shippingAddress/setPostCode", payload: postCode}),
    setPhoneNumber: (phoneNumber: string) => dispatch({type: "shippingAddress/setPhoneNumber", payload: phoneNumber}),
})

export default connect(mapStateToProps, mapDispatchToProps)(ShippingAddress)