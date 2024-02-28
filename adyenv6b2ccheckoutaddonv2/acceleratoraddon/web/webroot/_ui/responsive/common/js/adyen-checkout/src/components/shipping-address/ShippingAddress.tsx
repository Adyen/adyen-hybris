import React from "react";
import {connect} from "react-redux";
import {AppState} from "../../reducers/rootReducer";
import {StoreDispatch} from "../../store/store";
import {AddressConfigModel} from "../../reducers/addressConfigReducer";
import {InputCheckbox} from "../controls/InputCheckbox";
import {AddressService} from "../../service/addressService";
import {AddressModel} from "../../reducers/types";
import AddressBookSelector from "./AddressBookSelector";
import {ShippingAddressHeader} from "../headers/ShippingAddressHeader";
import {Navigate} from "react-router-dom";
import {routes} from "../../router/routes";
import {AddressForm} from "../common/AddressForm";
import {translationsStore} from "../../store/translationsStore";


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

type ShippingAddressProps = StoreProps & DispatchProps;

interface ShippingAddressState {
    addressBookModalOpen: boolean
    saveInAddressBook: boolean
    redirectToNextStep: boolean
}

class ShippingAddress extends React.Component<ShippingAddressProps, ShippingAddressState> {

    constructor(props: ShippingAddressProps) {
        super(props);
        this.state = {
            addressBookModalOpen: false,
            saveInAddressBook: false,
            redirectToNextStep: false
        }
    }

    componentDidMount() {
        AddressService.fetchAddressBook()
        AddressService.fetchAddressConfig()
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

    private async handleSubmitButton() {
        let success = await AddressService.addDeliveryAddress(this.props.shippingAddress, this.state.saveInAddressBook, true, false, false);
        if (success) {
            this.setState({...this.state, redirectToNextStep: true})
        }
    }

    private renderSaveAddressCheckbox(): React.JSX.Element {
        if (!this.props.addressConfig.anonymousUser) {
            return <InputCheckbox fieldName={translationsStore.get("checkout.summary.deliveryAddress.saveAddressInMyAddressBook")}
                                  onChange={(value) => this.onChangeSaveInAddressBook(value)}
                                  checked={this.state.saveInAddressBook}/>
        }
        return <></>
    }

    private renderAddressBookButton(): React.JSX.Element {
        if (this.props.addressBook.length > 0) {
            return <button className={"btn btn-default btn-block"} onClick={() => this.openAddressBookModal()}>{translationsStore.get("checkout.checkout.multi.deliveryAddress.viewAddressBook")}</button>
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
        if (this.state.redirectToNextStep) {
            return <Navigate to={routes.shippingMethod}/>
        }

        return (
            <>
                <ShippingAddressHeader isActive={true}/>

                <div className={"step-body"}>
                    {this.renderAddressBookModal()}

                    <div className={"shippingAddress_form checkout-shipping"}>
                        <div className={"checkout-indent"}>
                            <div className={"shippingAddress_form_header headline"}>{translationsStore.get("checkout.summary.shippingAddress")}</div>
                            {this.renderAddressBookButton()}
                            <br/>
                            <AddressForm addressConfig={this.props.addressConfig}
                                         onCountryCodeChange={(countryCode) => this.props.setCountryCode(countryCode)}
                                         address={this.props.shippingAddress}
                                         onTitleCodeChange={(titleCode) => this.props.setTitleCode(titleCode)}
                                         onFirstNameChange={(firstName) => this.props.setFirstName(firstName)}
                                         onLastNameChange={(lastName) => this.props.setLastName(lastName)}
                                         onLine1Change={(line1) => this.props.setLine1(line1)}
                                         onLine2Change={(line2) => this.props.setLine2(line2)}
                                         onCityChange={(city) => this.props.setCity(city)}
                                         onPostCodeChange={(postCode) => this.props.setPostCode(postCode)}
                                         onPhoneNumberChange={(phoneNumber) => this.props.setPhoneNumber(phoneNumber)}/>
                            {this.renderSaveAddressCheckbox()}
                        </div>
                    </div>
                    <button className={"btn btn-primary btn-block checkout-next"}
                            onClick={() => this.handleSubmitButton()}>{translationsStore.get("checkout.multi.deliveryAddress.continue")}
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