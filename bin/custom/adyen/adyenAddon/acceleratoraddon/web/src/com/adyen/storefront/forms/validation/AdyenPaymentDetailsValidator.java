/**
 * 
 */
package com.adyen.storefront.forms.validation;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.adyen.storefront.forms.AdyenPaymentDetailsForm;


/**
 * @author Kenneth Zhou
 * 
 */
@Component("adyenPaymentDetailsValidator")
public class AdyenPaymentDetailsValidator implements Validator
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> aClass)
	{
		return AdyenPaymentDetailsForm.class.equals(aClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(final Object object, final Errors errors)
	{
		final AdyenPaymentDetailsForm form = (AdyenPaymentDetailsForm) object;

		final Calendar start = parseDate(form.getStartMonth(), form.getStartYear());
		final Calendar expiration = parseDate(form.getExpiryMonth(), form.getExpiryYear());

		if (start != null && expiration != null && start.after(expiration))
		{
			errors.rejectValue("startMonth", "payment.startDate.invalid");
		}
		if (Boolean.TRUE.equals(form.getUseBoleto()))
		{
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "boleto.firstName.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "boleto.lastName.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "selectedBrand", "boleto.selectedBrand.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "socialSecurityNumber", "boleto.socialSecurityNumber.invalid");
		}


		final boolean editMode = StringUtils.isNotBlank(form.getPaymentId());
		if (editMode || Boolean.TRUE.equals(form.getNewBillingAddress()))
		{
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.titleCode", "address.title.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.firstName", "address.firstName.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.lastName", "address.lastName.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.line1", "address.line1.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.townCity", "address.townCity.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.postcode", "address.postcode.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.countryIso", "address.country.invalid");
			// ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.line2", "address.line2.invalid"); // for some addresses this field is required by cybersource
		}
	}

	protected Calendar parseDate(final String month, final String year)
	{
		if (StringUtils.isNotBlank(month) && StringUtils.isNotBlank(year))
		{
			final Integer yearInt = getIntegerForString(year);
			final Integer monthInt = getIntegerForString(month);

			if (yearInt != null && monthInt != null)
			{
				final Calendar date = getCalendarResetTime();
				date.set(Calendar.YEAR, yearInt.intValue());
				date.set(Calendar.MONTH, monthInt.intValue() - 1);
				date.set(Calendar.DAY_OF_MONTH, 1);
				return date;
			}
		}
		return null;
	}

	protected Calendar getCalendarResetTime()
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	/**
	 * Common method to convert a String to an Integer.
	 * 
	 * @param value
	 *           - the String value to be converted.
	 * @return - an Integer object.
	 */
	protected Integer getIntegerForString(final String value)
	{
		if (value != null && !value.isEmpty())
		{
			try
			{
				return Integer.valueOf(value);
			}
			catch (final Exception ignore)
			{
				// Ignore
			}
		}

		return null;
	}

}
