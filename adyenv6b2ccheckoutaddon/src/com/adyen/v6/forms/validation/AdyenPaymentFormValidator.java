package com.adyen.v6.forms.validation;

import java.util.Set;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.adyen.v6.forms.AdyenPaymentForm;

public class AdyenPaymentFormValidator implements Validator {
    private Set<String> storedCards;
    private boolean showRememberTheseDetails;

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
    }
}
