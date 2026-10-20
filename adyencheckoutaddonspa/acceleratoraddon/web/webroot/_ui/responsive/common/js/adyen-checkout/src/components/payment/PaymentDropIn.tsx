import React, {RefObject, useCallback, useEffect, useRef} from "react";
import {AdyenCheckout, AdyenCheckoutError, Dropin, PromptPay} from '@adyen/adyen-web/auto';
import '@adyen/adyen-web/styles/adyen.css';
import {
    AdditionalDetailsActions,
    CardConfiguration,
    CoreConfiguration,
    SubmitActions,
    UIElement
} from "@adyen/adyen-web";
import {AdyenConfigData} from "../../types/adyenConfigData";
import {AddressData} from "../../types/addressData";

class IrisQrCodeElement extends PromptPay {}

const defaultDisclaimerMessage = 'Caution! Borrowing money costs money!';
const europeanCountryCodes = new Set([
    'AL', 'AD', 'AM', 'AT', 'AZ', 'BY', 'BE', 'BA', 'BG', 'HR', 'CY', 'CZ', 'DK', 'EE', 'FI', 'FR', 'GE', 'DE',
    'GR', 'HU', 'IS', 'IE', 'IT', 'KZ', 'XK', 'LV', 'LI', 'LT', 'LU', 'MT', 'MD', 'MC', 'ME', 'NL', 'MK', 'NO',
    'PL', 'PT', 'RO', 'RU', 'SM', 'RS', 'SK', 'SI', 'ES', 'SE', 'CH', 'TR', 'UA', 'GB', 'VA'
]);

(IrisQrCodeElement as any).type = 'iris';
(IrisQrCodeElement as any).txVariants = ['iris'];
AdyenCheckout.register(IrisQrCodeElement as any);

interface PaymentDropInProps {
    adyenConfig: AdyenConfigData;
    shippingAddress: AddressData;
    shopperCountryCode: string;
    partialPaymentId?: string;
    onPayment: (state: any, element: UIElement, actions: SubmitActions) => void;
    onAdditionalDetails: (state: any, element: UIElement, actions: AdditionalDetailsActions) => void;
    onBalanceCheck: (resolve: any, reject: any, data: any) => Promise<void>;
    onOrderRequest: (resolve: any, reject: any, data: any) => Promise<void>;
    onError: (error: AdyenCheckoutError, element?: UIElement) => void;
    onDropInReady: (dropIn: Dropin) => void;
}

export const PaymentDropIn: React.FC<PaymentDropInProps> = ({
    adyenConfig,
    shippingAddress,
    shopperCountryCode,
    partialPaymentId,
    onPayment,
    onAdditionalDetails,
    onBalanceCheck,
    onOrderRequest,
    onError,
    onDropInReady
}) => {
    const paymentRef: RefObject<HTMLDivElement> = useRef(null);
    const dropInRef = useRef<Dropin | null>(null);
    
    // Use refs to store callback functions to avoid dependency issues
    const callbacksRef = useRef({
        onPayment,
        onAdditionalDetails,
        onBalanceCheck,
        onOrderRequest,
        onError,
        onDropInReady
    });
    
    // Update refs when callbacks change
    useEffect(() => {
        callbacksRef.current = {
            onPayment,
            onAdditionalDetails,
            onBalanceCheck,
            onOrderRequest,
            onError,
            onDropInReady
        };
    }, [onPayment, onAdditionalDetails, onBalanceCheck, onOrderRequest, onError, onDropInReady]);

    const castToEnvironment = (env: string): CoreConfiguration['environment'] => {
        const validEnvironments: CoreConfiguration['environment'][] = ['test', 'live', 'live-us', 'live-au', 'live-apse', 'live-in'];
        if (validEnvironments.includes(env as CoreConfiguration['environment'])) {
            return env as CoreConfiguration['environment'];
        }
        throw new Error(`Invalid environment: ${env}`);
    };

    const getAdyenCheckoutConfig = useCallback((): CoreConfiguration => {
        return {
            paymentMethodsResponse: {
                paymentMethods: adyenConfig.paymentMethods,
                storedPaymentMethods: adyenConfig.storedPaymentMethodList
            },
            locale: adyenConfig.shopperLocale,
            environment: castToEnvironment(adyenConfig.environmentMode),
            clientKey: adyenConfig.adyenClientKey,
            countryCode: adyenConfig.countryCode,
            amount: adyenConfig.amount,
            analytics: {
                enabled: false
            },
            // @ts-ignore
            risk: {
                enabled: true
            },
            onError: callbacksRef.current.onError,
            onSubmit: (state: any, element: UIElement, actions: SubmitActions) => callbacksRef.current.onPayment(state.data, element, actions),
            onAdditionalDetails: (state: any, element: UIElement, actions: AdditionalDetailsActions) => callbacksRef.current.onAdditionalDetails(state.data, element, actions),
            onBalanceCheck: async (resolve: any, reject: any, data: any) => await callbacksRef.current.onBalanceCheck(resolve, reject, {...data, amount: adyenConfig.amount}),
            onOrderRequest: async (resolve: any, reject: any, data: any) => await callbacksRef.current.onOrderRequest(resolve, reject, {...data, amount: adyenConfig.amount})
        };
    }, [
        adyenConfig.paymentMethods,
        adyenConfig.storedPaymentMethodList,
        adyenConfig.shopperLocale,
        adyenConfig.environmentMode,
        adyenConfig.adyenClientKey,
        adyenConfig.countryCode,
        adyenConfig.amount
    ]);

    const getAdyenCardConfig = useCallback((): CardConfiguration => {
         const config: CardConfiguration ={
            type: 'card',
            hasHolderName: true,
            holderNameRequired: adyenConfig.cardHolderNameRequired,
            enableStoreDetails: adyenConfig.showRememberTheseDetails,
            clickToPayConfiguration: {
                merchantDisplayName: adyenConfig.merchantDisplayName,
                shopperEmail: adyenConfig.shopperEmail,
                locale: adyenConfig.clickToPayLocale,
            },
        };

        if (adyenConfig.installmentOptions) {
            config.installmentOptions = adyenConfig.installmentOptions;
        }

        return config;
    }, [
        adyenConfig.cardHolderNameRequired,
        adyenConfig.showRememberTheseDetails,
        adyenConfig.merchantDisplayName,
        adyenConfig.shopperEmail,
        adyenConfig.clickToPayLocale,
        adyenConfig.installmentOptions
    ]);

    // Separate from getAdyenCardConfig: the Adyen Drop-in resolves stored/one-click
    // card instances against paymentMethodsConfiguration.storedCard, not .card, so
    // hideCVC must live here to only ever skip CVC for stored payment methods.
    const getAdyenStoredCardConfig = useCallback((): CardConfiguration => ({
        type: 'card',
        hideCVC: adyenConfig.skipCvcForOneClick,
    }), [adyenConfig.skipCvcForOneClick]);

    const initializeDropIn = useCallback(async () => {
        if (!adyenConfig.adyenClientKey || !paymentRef.current) {
            return;
        }

        try {
            const adyenCheckout = await AdyenCheckout(getAdyenCheckoutConfig());

            const nonCardPaymentMethodConfigurations = europeanCountryCodes.has(shopperCountryCode.toUpperCase())
                ? Object.fromEntries(
                    adyenConfig.paymentMethods
                    .filter(paymentMethod => adyenConfig.openInvoiceMethods.includes(paymentMethod.type))
                    .map(paymentMethod => {
                        const disclaimerMessage = adyenConfig.disclaimerMessage || defaultDisclaimerMessage;
                        const disclaimerLink = adyenConfig.disclaimerLink?.trim();
                        return [paymentMethod.type, {
                            disclaimerMessage: disclaimerLink
                                ? {
                                    message: `${disclaimerMessage} %{learnMore}`,
                                    linkText: 'Learn more...',
                                    link: disclaimerLink
                                }
                                : { message: disclaimerMessage }
                        }];
                    })
                )
                : {};
            
            const dropIn = new Dropin(adyenCheckout, {
                paymentMethodsConfiguration: {
                    ...nonCardPaymentMethodConfigurations,
                    card: getAdyenCardConfig(),
                    storedCard: getAdyenStoredCardConfig(),
                    boletobancario: {
                        // @ts-ignore
                        personalDetailsRequired: true,
                        billingAddressRequired: false,
                        showEmailAddress: false,
                        data: {
                            firstName: shippingAddress.firstName,
                            lastName: shippingAddress.lastName,
                        }
                    },
                    giftcard: {
                        // @ts-ignore - Gift card styling configuration
                        styles: {
                            base: {
                                fontSize: '16px',
                                color: '#333'
                            }
                        }
                    }
                },
                paymentMethodComponents: [IrisQrCodeElement as any],
                showPayButton: true,
                isPartialPayment: true,
                showRemainingAmount: true
            }).mount(paymentRef.current);

            dropInRef.current = dropIn;
            callbacksRef.current.onDropInReady(dropIn);
        } catch (error) {
            console.error('Failed to initialize Adyen DropIn:', error);
            callbacksRef.current.onError(error as AdyenCheckoutError);
        }
    }, [
        adyenConfig.adyenClientKey,
        adyenConfig.disclaimerMessage,
        adyenConfig.disclaimerLink,
        adyenConfig.paymentMethods,
        adyenConfig.openInvoiceMethods,
        shopperCountryCode,
        shippingAddress.firstName,
        shippingAddress.lastName,
        getAdyenCheckoutConfig,
        getAdyenCardConfig,
        getAdyenStoredCardConfig
    ]);

    useEffect(() => {
        initializeDropIn();
        
        return () => {
            if (dropInRef.current) {
                dropInRef.current.unmount();
            }
        };
    }, [adyenConfig.adyenClientKey, initializeDropIn]);

    return <div className="dropin-payment" ref={paymentRef} />;
};
