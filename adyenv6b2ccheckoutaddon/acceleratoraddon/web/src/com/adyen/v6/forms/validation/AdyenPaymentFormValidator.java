package com.adyen.v6.forms.validation;

import com.adyen.model.hpp.PaymentMethod;
import com.adyen.model.recurring.RecurringDetail;
import com.adyen.v6.enums.AdyenCardTypeEnum;
import com.adyen.v6.forms.AdyenPaymentForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_CC;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_ONECLICK;

public class AdyenPaymentFormValidator implements Validator {
    private List<PaymentMethod> alternativePaymentMethods;
    private Set<AdyenCardTypeEnum> allowedCards;
    private List<RecurringDetail> storedCards;
    private boolean showRememberTheseDetails = false;

    public AdyenPaymentFormValidator(List<PaymentMethod> alternativePaymentMethods,
                                     Set<AdyenCardTypeEnum> allowedCards,
                                     List<RecurringDetail> storedCards,
                                     boolean showRememberTheseDetails) {
        this.alternativePaymentMethods = alternativePaymentMethods;
        this.allowedCards = allowedCards;
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
        Set<String> allowedPaymentMethods = new HashSet<>();

        //Add CC
        if (allowedCards.size() > 0) {
            allowedPaymentMethods.add(PAYMENT_METHOD_CC);
        }

        //Add Oneclick
        storedCards.forEach(storedCard -> allowedPaymentMethods.add(PAYMENT_METHOD_ONECLICK + storedCard.getAlias()));

        //Add APMs
        alternativePaymentMethods.forEach(paymentMethod -> allowedPaymentMethods.add(paymentMethod.getBrandCode()));

        //Add aliases
        Set<String> aliases = storedCards.stream().map(storedCard -> storedCard.getAlias()).collect(Collectors.toSet());

        //Check paymentMethod
        if (form.getPaymentMethod() != null && !form.getPaymentMethod().isEmpty() && allowedPaymentMethods != null) {
            if (!allowedPaymentMethods.contains(form.getPaymentMethod())) {
                errors.reject("checkout.error.paymentethod.invalid");
            }
        }

        //Check CSE token in case of CC/OneClick
        if (form.isCC() || form.isOneClick()) {
            if (form.getCseToken() == null || form.getCseToken().isEmpty()) {
                errors.reject("checkout.error.paymentethod.cse.missing");
            }
        }

        //Check selectedAlias in case of oneClick
        if (form.getSelectedAlias() != null && !form.getSelectedAlias().isEmpty() && aliases != null) {
            if (!aliases.contains(form.getSelectedAlias())) {
                errors.reject("checkout.error.paymentethod.alias.invalid");
            }
        }

        //Check remember these details
        if (form.getRememberTheseDetails()) {
            if (!showRememberTheseDetails) {
                errors.reject("checkout.error.paymentethod.rememberdetails.invalid");
            }
        }
    }
}
