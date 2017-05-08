package com.adyen.v6.forms.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.adyen.model.hpp.PaymentMethod;
import com.adyen.model.recurring.RecurringDetail;
import com.adyen.v6.enums.AdyenCardTypeEnum;
import com.adyen.v6.forms.AdyenPaymentForm;
import de.hybris.platform.commercefacades.user.data.AddressData;
import static com.adyen.v6.constants.Adyenv6coreConstants.OPENINVOICE_METHODS_ALLOW_SOCIAL_SECURITY_NUMBER;
import static com.adyen.v6.constants.Adyenv6coreConstants.OPENINVOICE_METHODS_API;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_CC;
import static com.adyen.v6.constants.Adyenv6coreConstants.PAYMENT_METHOD_ONECLICK;

public class AdyenPaymentFormValidator implements Validator {
    private static final Logger LOGGER = Logger.getLogger(AdyenPaymentForm.class);

    private List<PaymentMethod> alternativePaymentMethods;
    private Set<AdyenCardTypeEnum> allowedCards;
    private List<RecurringDetail> storedCards;
    private boolean showRememberTheseDetails = false;
    private AddressData deliveryAddress;

    public AdyenPaymentFormValidator(List<PaymentMethod> alternativePaymentMethods,
                                     Set<AdyenCardTypeEnum> allowedCards,
                                     List<RecurringDetail> storedCards,
                                     boolean showRememberTheseDetails,
                                     AddressData deliveryAddress) {
        this.alternativePaymentMethods = alternativePaymentMethods;
        this.allowedCards = allowedCards;
        this.storedCards = storedCards;
        this.showRememberTheseDetails = showRememberTheseDetails;
        this.deliveryAddress = deliveryAddress;
    }

    @Override
    public boolean supports(final Class<?> aClass) {
        return AdyenPaymentForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

        LOGGER.info("Validate paymentForm");

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
