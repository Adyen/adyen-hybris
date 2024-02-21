export interface AdyenConfigData {
    alternativePaymentMethods: PaymentMethodData[];
    connectedTerminalList: string[];
    storedPaymentMethodList: StoredPaymentMethodData[];
    issuerLists: Map<string, string>;
    creditCardLabel: string;
    allowedCards: AllowedCard[];
    amount: AmountData;
    adyenClientKey: string;
    adyenPaypalMerchantId: string;
    deviceFingerPrintUrl: string;
    sessionData: SessionData;
    selectedPaymentMethod: string;
    showRememberTheseDetails: boolean;
    checkoutShopperHost: string;
    environmentMode: string;
    shopperLocale: string;
    openInvoiceMethods: string[];
    showSocialSecurityNumber: boolean;
    showBoleto: boolean;
    showComboCard: boolean;
    showPos: boolean;
    immediateCapture: boolean;
    countryCode: string;
    cardHolderNameRequired: boolean;
    sepaDirectDebit: boolean;
}

interface SessionData {
    id: string,
    sessionData: string
}

export interface PaymentMethodData {
    brand?: string;
    brands?: string[];
    configuration: object;
    issuers?: PaymentMethodIssuerData[];
    fundingSource?: string;
    group?: PaymentMethodGroupData;
    inputDetails?: InputDetailData[];
    name: string;
    type: string;
}

interface PaymentMethodIssuerData {
    disabled: boolean;
    id: string;
    name: string;
}

interface InputDetailData {
    onfiguration: Map<string, string>
    details: SubInputDetailData[];
    itemSearchUrl: string;
    items: ItemData[];
    key: string;
    optional: boolean;
    type: string;
    value: string;
}

interface SubInputDetailData {
    items: ItemData[];
    key: string;
    optional: boolean;
    type: string;
    value: string;
    configuration: Map<string, string>;
}

interface ItemData {
    id: string;
    name: string;
}

interface PaymentMethodGroupData {
    name: string;
    paymentMethodData: string;
    type: string;
}

interface StoredPaymentMethodData {
    brand: string;
    expiryMonth: string;
    expiryYear: string;
    holderName: string;
    iban: string;
    id: string;
    lastFour: string;
    name: string;
    networkTxReference: string;
    ownerName: string;
    shopperEmail: string;
    supportedShopperInteractions: string[];
    type: string;
}

interface AmountData {
    value: number;
    currency: string;
}

interface AllowedCard {
    code: string,
    type: string
}