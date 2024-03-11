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
import {AdyenPaymentForm} from "../../types/paymentForm";
import {PaymentService} from "../../service/paymentService";
import UIElement from "@adyen/adyen-web/dist/types/components/UIElement";
import {CardState} from "../../types/paymentState";
import {translationsStore} from "../../store/translationsStore";
import {routes} from "../../router/routes";
import {Navigate} from "react-router-dom";
import {PaymentAction} from "@adyen/adyen-web/dist/types/types";

interface State {
    useDifferentBillingAddress: boolean
    redirectToNextStep: boolean
    redirectTo3DS: boolean
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

    paymentRef: RefObject<HTMLDivElement>
    threeDSRef: RefObject<HTMLDivElement>

    constructor(props: Props) {
        super(props);
        this.state = {
            useDifferentBillingAddress: false,
            redirectToNextStep: false,
            redirectTo3DS: false
        }
        this.paymentRef = React.createRef();
        this.threeDSRef = React.createRef();
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

        this.initiateDropIn(adyenCheckout)
    }

    private getAdyenCheckoutConfig(): CoreOptions {
        return {
            paymentMethodsConfiguration: {
                card: {
                    type: 'card',
                    hasHolderName: true,
                    holderNameRequired: this.props.adyenConfig.cardHolderNameRequired,
                    enableStoreDetails: this.props.adyenConfig.showRememberTheseDetails,
                    onSubmit: (state: CardState, element: UIElement) => this.handleBankCardPayment(state)
                },
                storedCard: {
                    onSubmit: (state: CardState, element: UIElement) => this.handleStoredCardPayment(state)
                }
            },
            paymentMethodsResponse: {
                paymentMethods: this.props.adyenConfig.paymentMethods,
                storedPaymentMethods: this.props.adyenConfig.storedPaymentMethodList
            },
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
            },
            onSubmit(state: any, element: UIElement) {
                console.warn('not implemented payment method')
            }
        }
    }

    private initiateDropIn(adyenCheckout: Core) {

        let dropIn = adyenCheckout.create("dropin");

        dropIn.mount(this.paymentRef.current)
    }

    private async handleBankCardPayment(cardState: CardState) {
        let adyenPaymentForm = PaymentService.prepareBankCardAdyenPaymentForm(cardState,
            this.state.useDifferentBillingAddress, this.props.billingAddress);

        await this.executePaymentRequest(adyenPaymentForm)
    }

    private async handleStoredCardPayment(cardState: CardState) {
        let adyenPaymentForm = PaymentService.prepareStoredCardAdyenPaymentForm(cardState,
            this.state.useDifferentBillingAddress, this.props.billingAddress);

        await this.executePaymentRequest(adyenPaymentForm)
    }

    private async executePaymentRequest(adyenPaymentForm: AdyenPaymentForm) {
        let responseData = await PaymentService.placeOrder(adyenPaymentForm);

        if (responseData.success) {
            if (responseData.is3DSRedirect) {
                await this.mount3DSComponent(responseData.paymentsAction)
            } else {
                this.setState({...this.state, redirectToNextStep: true})
            }
        }

    }

    private async mount3DSComponent(paymentAction: PaymentAction) {
        let adyenCheckout = await AdyenCheckout(this.getAdyenCheckoutConfig());
        adyenCheckout.createFromAction(paymentAction).mount(this.threeDSRef.current);
    }

    private async mount3DSComponent(paymentAction: PaymentAction) {
        let adyenCheckout = await AdyenCheckout(this.getAdyenCheckoutConfig());
        adyenCheckout.createFromAction(paymentAction).mount(this.threeDSRef.current);
    }

    private renderBillingAddressForm(): React.JSX.Element {
        if (this.state.useDifferentBillingAddress) {
            return (
                <>
                    <hr/>
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
                    <hr/>
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
                        <InputCheckbox
                            fieldName={translationsStore.get("checkout.multi.payment.useDifferentBillingAddress")}
                            onChange={(checkboxState) => this.onChangeUseDifferentBillingAddress(checkboxState)}
                            checked={this.state.useDifferentBillingAddress}/>
                        {this.renderBillingAddressForm()}
                        <div className={"dropin-payment"} ref={this.paymentRef}/>
                    </div>
                </div>
                <div ref={this.threeDSRef}/>
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