import React, {RefObject} from "react";
import {PaymentHeader} from "../headers/PaymentHeader";
import {connect} from "react-redux";
import {ShippingAddressHeading} from "../common/ShippingAddressHeading";
import {AddressModel} from "../../reducers/types";
import {AppState} from "../../reducers/rootReducer";
import {AddressConfigModel} from "../../reducers/addressConfigReducer";
import {StoreDispatch} from "../../store/store";
import {AddressData} from "../../types/addressData";
import {InputCheckbox} from "../controls/InputCheckbox";
import {AddressForm} from "../common/AddressForm";
import {AddressService} from "../../service/addressService";
import {AdyenConfigService} from "../../service/adyenConfigService";
import AdyenCheckout from '@adyen/adyen-web';
import '@adyen/adyen-web/dist/adyen.css';
import {AdyenConfigData} from "../../types/adyenConfigData";
import {isEmpty, isNotEmpty} from "../../util/stringUtil";
import {CoreOptions} from "@adyen/adyen-web/dist/types/core/types";
import {OnPaymentCompletedData} from "@adyen/adyen-web/dist/types/components/types";
import AdyenCheckoutError from "@adyen/adyen-web/dist/types/core/Errors/AdyenCheckoutError";
import Core from "@adyen/adyen-web/dist/types/core";
import UIElement from "@adyen/adyen-web/dist/types/components/UIElement";
import {AdyenPaymentForm} from "../../types/paymentForm";
import {PaymentService} from "../../service/paymentService";
import {Navigate} from "react-router-dom";
import {routes} from "../../router/routes";

interface State {
    useDifferentBillingAddress: boolean
    redirectToNextStep: boolean
    cardState: CardState
}

interface CardState {
    isValid: boolean,
    data: {
        paymentMethod: {
            type: string,
            encryptedCardNumber: string,
            encryptedExpiryMonth: string,
            encryptedExpiryYear: string,
            encryptedSecurityCode: string,
            holderName: string,
            brand: string
        },
        browserInfo: any,
        storePaymentMethod?: boolean
    }
}

interface StoreProps {
    billingAddress: AddressModel,
    addressConfig: AddressConfigModel,
    shippingAddressFromCart: AddressData,
    adyenConfig: AdyenConfigData
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

type Props = StoreProps & DispatchProps

class Payment extends React.Component<Props, State> {

    cardRef: RefObject<HTMLDivElement>

    constructor(props: Props) {
        super(props);
        this.state = {
            useDifferentBillingAddress: false,
            redirectToNextStep: false,
            cardState: {
                isValid: false,
                data: {
                    paymentMethod: {
                        type: '',
                        encryptedCardNumber: '',
                        encryptedExpiryMonth: '',
                        encryptedExpiryYear: '',
                        encryptedSecurityCode: '',
                        holderName: '',
                        brand: ''
                    },
                    browserInfo: {},
                }
            }
        }
        this.cardRef = React.createRef();
    }

    async componentDidUpdate(prevProps: Readonly<Props>, prevState: Readonly<State>, snapshot?: any) {
        if (isEmpty(prevProps.adyenConfig.adyenClientKey) && isNotEmpty(this.props.adyenConfig.adyenClientKey)) {
            await this.initializeWebComponentsCheckout()
        }
    }

    async componentDidMount() {
        AddressService.fetchAddressConfig();
        AdyenConfigService.fetchPaymentMethodsConfig();
        if (isNotEmpty(this.props.adyenConfig.adyenClientKey)) {
            await this.initializeWebComponentsCheckout()
        }
    }

    private async initializeWebComponentsCheckout() {
        let adyenCheckout = await AdyenCheckout(this.getAdyenCheckoutConfig());

        this.initiateBankCard(adyenCheckout)
    }

    private getAdyenCheckoutConfig(): CoreOptions {
        return {
            locale: this.props.adyenConfig.shopperLocale,
            environment: this.props.adyenConfig.environmentMode,
            clientKey: this.props.adyenConfig.adyenClientKey,
            session: {
                id: this.props.adyenConfig.sessionData.id,
                sessionData: this.props.adyenConfig.sessionData.sessionData
            },
            analytics: {
                enabled: false
            },
            onPaymentCompleted(data: OnPaymentCompletedData, element?: UIElement) {
                console.info(data, element);
            },
            onError(error: AdyenCheckoutError, element?: UIElement) {
                console.error(error.name, error.message, error.stack, element);
            }
        }
    }

    private initiateBankCard(adyenCheckout: Core) {
        let brands = this.props.adyenConfig.allowedCards.map(value => value.code)

        let card = adyenCheckout.create("card", {
            type: 'card',
            hasHolderName: true,
            holderNameRequired: this.props.adyenConfig.cardHolderNameRequired,
            enableStoreDetails: this.props.adyenConfig.showRememberTheseDetails,
            brands: brands,
            onChange: (cardState) => this.onCardChange(cardState)
        });

        card.mount(this.cardRef.current)
    }

    private onCardChange(cardState: CardState) {
        this.setState({
            ...this.state, cardState
        })
    }

    private prepareAdyenPaymentForm(): AdyenPaymentForm {
        return {
            paymentMethod: "adyen_cc",
            useAdyenDeliveryAddress: !this.state.useDifferentBillingAddress,
            billingAddress: this.state.useDifferentBillingAddress ? PaymentService.convertBillingAddress(this.props.billingAddress) : null,
            encryptedCardNumber: this.state.cardState.data.paymentMethod.encryptedCardNumber,
            encryptedSecurityCode: this.state.cardState.data.paymentMethod.encryptedSecurityCode,
            encryptedExpiryMonth: this.state.cardState.data.paymentMethod.encryptedExpiryMonth,
            encryptedExpiryYear: this.state.cardState.data.paymentMethod.encryptedExpiryYear,
            cardHolder: this.state.cardState.data.paymentMethod.holderName,
            browserInfo: JSON.stringify(this.state.cardState.data.browserInfo),
            rememberTheseDetails: this.state.cardState.data.storePaymentMethod,
            cardBrand: this.state.cardState.data.paymentMethod.brand
        }
    }

    private async handleSubmitButton() {
        if (this.state.cardState.isValid) {
            let success = await PaymentService.placeOrder(this.prepareAdyenPaymentForm());

            if (success) {
                this.setState({...this.state, redirectToNextStep: true})
            }
        }

    }

    private renderBillingAddressForm(): React.JSX.Element {
        if (this.state.useDifferentBillingAddress) {
            return (
                <>
                    <div className={"headline"}>Billing Address</div>
                    <AddressForm addressConfig={this.props.addressConfig} address={this.props.billingAddress}
                                 onCountryCodeChange={(countryCode) => this.props.setCountryCode(countryCode)}
                                 onTitleCodeChange={(titleCode) => this.props.setTitleCode(titleCode)}
                                 onFirstNameChange={(firstName) => this.props.setFirstName(firstName)}
                                 onLastNameChange={(lastName) => this.props.setLastName(lastName)}
                                 onLine1Change={(line1) => this.props.setLine1(line1)}
                                 onLine2Change={(line2) => this.props.setLine2(line2)}
                                 onCityChange={(city) => this.props.setCity(city)}
                                 onPostCodeChange={(postCode) => this.props.setPostCode(postCode)}
                                 onPhoneNumberChange={(phoneNumber) => this.props.setPhoneNumber(phoneNumber)}/>
                </>
            )
        }
        return <></>
    }

    private onChangeUseDifferentBillingAddress(value: boolean): void {
        this.setState({...this.state, useDifferentBillingAddress: value})
    }

    render() {
        if (this.state.redirectToNextStep) {
            return <Navigate to={routes.thankYouPage}/>
        }

        return (
            <>
                <PaymentHeader isActive={true}/>
                <div className={"step-body"}>

                    <div className={"checkout-paymentmethod"}>
                        <ShippingAddressHeading address={this.props.shippingAddressFromCart}/>
                        <InputCheckbox fieldName={"Use different billing address"}
                                       onChange={(checkboxState) => this.onChangeUseDifferentBillingAddress(checkboxState)}
                                       checked={this.state.useDifferentBillingAddress}/>
                        {this.renderBillingAddressForm()}

                        <div ref={this.cardRef}/>
                    </div>
                    <button className={"btn btn-primary btn-block checkout-next"}
                            onClick={() => this.handleSubmitButton()}>NEXT
                    </button>
                </div>
            </>
        )
    }
}

function mapDispatchToProps(dispatch: StoreDispatch): DispatchProps {
    return {
        setFirstName: (firstName: string) => dispatch({
            type: "billingAddress/setFirstName",
            payload: firstName
        }),
        setLastName: (lastName: string) => dispatch({type: "billingAddress/setLastName", payload: lastName}),
        setCountryCode: (country: string) => dispatch({
            type: "billingAddress/setCountryCode",
            payload: country
        }),
        setTitleCode: (title: string) => dispatch({type: "billingAddress/setTitleCode", payload: title}),
        setLine1: (line1: string) => dispatch({type: "billingAddress/setLine1", payload: line1}),
        setLine2: (line2: string) => dispatch({type: "billingAddress/setLine2", payload: line2}),
        setCity: (city: string) => dispatch({type: "billingAddress/setCity", payload: city}),
        setPostCode: (postCode: string) => dispatch({type: "billingAddress/setPostCode", payload: postCode}),
        setPhoneNumber: (phoneNumber: string) => dispatch({
            type: "billingAddress/setPhoneNumber",
            payload: phoneNumber
        }),
    }
}

function mapStateToProps(state: AppState): StoreProps {
    return {
        addressConfig: state.addressConfig,
        billingAddress: state.billingAddress,
        shippingAddressFromCart: state.cartData.deliveryAddress,
        adyenConfig: state.adyenConfig
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(Payment)