import React from "react";
import {connect} from "react-redux";
import {AppState} from "../../reducers/rootReducer";
import {AddressModel} from "../../reducers/types";
import {StoreDispatch} from "../../store/store";
import {isNotEmpty} from "../../util/stringUtil";
import './AddressBookSelectorStyle.scss'
import {ShippingAddressService} from "../../service/shippingAddressService";

interface Props {
    closeModal: () => void
}

interface StoreProps {
    addressBook: AddressModel[]
}

interface DispatchProps {
    setSelectedAddress: (address: AddressModel) => void
}

interface AddressBookSelectorProps extends Props, StoreProps, DispatchProps {
}

class AddressBookSelector extends React.Component<AddressBookSelectorProps, null> {


    private handleSelectAddress(address: AddressModel) {
        this.props.setSelectedAddress(address)
        ShippingAddressService.selectAddress(address.id);
        //TODO: change step using router
    }

    private renderAddressBookEntries(): React.JSX.Element[] {
        let result: React.JSX.Element[] = []

        this.props.addressBook.forEach((address, index) => {
            result.push(
                <div className={"addressBookSelector_modal_addressesContainer_addressItem"} key={index}>
                    <strong>{isNotEmpty(address.title) ? address.title + '\xa0' : ''}{address.firstName}&nbsp;{address.lastName}&nbsp;</strong>
                    <br/>
                    {address.line1}&nbsp;{address.line2}
                    <br/>
                    {address.city}
                    <br/>
                    {address.country}&nbsp;{address.postalCode}
                    <br/>
                    <button
                        className={"addressBookSelector_modal_addressesContainer_addressItem_button btn btn-primary btn-block"}
                        onClick={() => this.handleSelectAddress(address)}>Use
                        this address
                    </button>
                </div>)
        })
        return result
    }

    render() {
        return (
            <>
                <div className={"addressBookSelector_backdrop"} onClick={() => this.props.closeModal()}/>
                <dialog open className={"addressBookSelector_modal"}>
                <span className={"addressBookSelector_modal_close glyphicon glyphicon-remove"}
                      onClick={() => this.props.closeModal()}/>
                    <div className={"addressBookSelector_modal_header"}>Saved Addresses</div>
                    <div className={"addressBookSelector_modal_addressesContainer"}>
                        {this.renderAddressBookEntries()}
                    </div>
                </dialog>
            </>
        )
    }
}

const mapStateToProps = (state: AppState): StoreProps => ({
    addressBook: state.addressBook
})

const mapDispatchToProps = (dispatch: StoreDispatch): DispatchProps => ({
    setSelectedAddress: (address: AddressModel) => dispatch({type: "shippingAddress/setAddress", payload: address})
})

export default connect(mapStateToProps, mapDispatchToProps)(AddressBookSelector)