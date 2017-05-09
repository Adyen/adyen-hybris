package com.adyen.v6.forms.validation;

import java.util.Set;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.adyen.v6.forms.AdyenPaymentForm;
import de.hybris.platform.commercefacades.user.data.AddressData;
import static com.adyen.v6.constants.Adyenv6coreConstants.OPENINVOICE_METHODS_ALLOW_SOCIAL_SECURITY_NUMBER;
import static com.adyen.v6.constants.Adyenv6coreConstants.OPENINVOICE_METHODS_API;

public class AdyenPaymentFormValidator implements Validator {
    private Set<String> storedCards;
    private boolean showRememberTheseDetails;
    private AddressData deliveryAddress;

    public AdyenPaymentFormValidator(Set<String> storedCards, boolean showRememberTheseDetails, AddressData deliveryAddress) {
        this.storedCards = storedCards;
        this.showRememberTheseDetails = showRememberTheseDetails;
        this.deliveryAddress = deliveryAddress;

    public AdyenPaymentFormValidator(Set<String> storedCards, boolean showRememberTheseDetails) {
        this.storedCards = storedCards;
        this.showRememberTheseDetails = showRememberTheseDetails;
    }

    @Override
    public boolean supports(final Class<?> aClass) {
        return AdyenPaymentForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        AdyenPaymentForm form = (AdyenPaymentForm) o;

        //Check CSE token in case of CC/OneClick
        if (form.isCC() || form.isOneClick()) {
            if (form.getCseToken() == null || form.getCseToken().isEmpty()) {
                errors.reject("checkout.error.paymentmethod.cse.missing");
            }
        }

        //Check selectedReference in case of oneClick
        if (form.getSelectedReference() != null && ! form.getSelectedReference().isEmpty()) {
            if (storedCards == null || ! storedCards.contains(form.getSelectedReference())) {
                errors.reject("checkout.error.paymentmethod.recurringDetailReference.invalid");
            }
        }

        //Check remember these details
        if (form.getRememberTheseDetails()) {
            if (! showRememberTheseDetails) {
                errors.reject("checkout.error.paymentethod.rememberdetails.invalid");
            }
        }

        // check if date or social seucrity number is set
        if( OPENINVOICE_METHODS_API.contains(form.getPaymentMethod())) {
            String countryIsoCode = deliveryAddress.getCountry().getIsocode();

            if(OPENINVOICE_METHODS_ALLOW_SOCIAL_SECURITY_NUMBER.contains(countryIsoCode)) {

                if(form.getSocialSecurityNumber().isEmpty() ) {
                    errors.reject("checkout.error.paymentethod.ssn.invalid");
                }
            }

            if(form.getDob() == null) {
                errors.reject("checkout.error.paymentethod.dob.invalid");
            }
        }
    }
}
