export interface AdyenPaymentForm {
    paymentMethod: string;

    //CSE
    cseToken?: string;

    //Secured Fields
    encryptedCardNumber?: string;
    encryptedExpiryMonth?: string;
    encryptedExpiryYear?: string;
    encryptedSecurityCode?: string;
    cardBrand?: string;
    cardHolder?: string;
    cardType?: string;
    selectedReference?: string;
    installments?: number;

    //Save card
    rememberTheseDetails?: boolean;

    //HPP
    issuerId?: string;
    upiVirtualAddress?: string;

    //SEPA direct debit fields
    sepaOwnerName?: string;
    sepaIbanNumber?: string;

    // openinvoice fields
    dob?: string;
    dfValue?: string;

    //Boleto
    firstName?: string;
    lastName?: string;

    // used in openinvoice and boleto
    socialSecurityNumber?: string;

    //3DS 2.0
    browserInfo?: string;

    //POS
    terminalId?: string;

    // AfterPay fields
    gender?: string;
    telephoneNumber?: string;
    shopperEmail?: string;

    // Gift Card
    giftCardBrand?: string;

    //Billing address related fields
    useAdyenDeliveryAddress?: boolean;
    billingAddress?: AdyenAddressForm;
}

export interface AdyenAddressForm {
    addressId: string;
    titleCode: string;
    firstName: string;
    lastName: string;
    line1: string;
    line2: string;
    townCity: string;
    regionIso?: string;
    postcode: string;
    countryIso: string;
    phoneNumber: string;
    saveInAddressBook: boolean;
}