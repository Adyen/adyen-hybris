/**
 * 
 */
package com.adyen.storefront.forms;

import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;


/**
 * @author Kenneth Zhou
 * 
 */
public class AdyenPaymentDetailsForm
{

	private String paymentId;
	private String cardTypeCode;
	private String nameOnCard;
	private String cardNumber;
	private String startMonth;
	private String startYear;
	private String expiryMonth;
	private String expiryYear;
	private String issueNumber;

	private Boolean saveInAccount;

	private Boolean newBillingAddress;

	private AddressForm billingAddress;

	//For recurring
	private boolean savePayment;
	private String securityCode;
	private String savedPaymentMethodId;
	private String savedPaymentMethodNumber;
	private String savedPaymentMethodCVC;
	private String savePaymentMethodExpiryMonth;
	private String savePaymentMethodExpiryYear;
	private String savedPaymentMethodCardNumber;
	private String savedPaymentMethodOwner;
	private String savedPaymentMethodType;

	//For installments
	private String installments;

	//For Boleto
	private String firstName;
	private String lastName;
	private String selectedBrand;
	private String socialSecurityNumber;
	private String shopperStatement;
	private Boolean useBoleto = Boolean.FALSE;
	private Boolean useSavedPayment = Boolean.FALSE;
	private Boolean useCreditCard = Boolean.FALSE;

	//HPP
	private Boolean useHPP = Boolean.FALSE;
	private String adyenPaymentBrand;
	private String issuerId;



	/**
	 * @param useHPP
	 *           the useHPP to set
	 */
	public void setUseHPP(final Boolean useHPP)
	{
		this.useHPP = useHPP;
	}

	/**
	 * @return the adyenPaymentBrand
	 */
	public String getAdyenPaymentBrand()
	{
		return adyenPaymentBrand;
	}

	/**
	 * @param adyenPaymentBrand
	 *           the adyenPaymentBrand to set
	 */
	public void setAdyenPaymentBrand(final String adyenPaymentBrand)
	{
		this.adyenPaymentBrand = adyenPaymentBrand;
	}

	/**
	 * @return the useHPP
	 */
	public Boolean getUseHPP()
	{
		return useHPP;
	}

	/**
	 * @return the useBoleto
	 */
	public Boolean getUseBoleto()
	{
		return useBoleto;
	}

	/**
	 * @param useBoleto
	 *           the useBoleto to set
	 */
	public void setUseBoleto(final Boolean useBoleto)
	{
		this.useBoleto = useBoleto;
	}

	/**
	 * @return the useSavedPayment
	 */
	public Boolean getUseSavedPayment()
	{
		return useSavedPayment;
	}

	/**
	 * @param useSavedPayment
	 *           the useSavedPayment to set
	 */
	public void setUseSavedPayment(final Boolean useSavedPayment)
	{
		this.useSavedPayment = useSavedPayment;
	}

	/**
	 * @return the useCreditCard
	 */
	public Boolean getUseCreditCard()
	{
		return useCreditCard;
	}

	/**
	 * @param useCreditCard
	 *           the useCreditCard to set
	 */
	public void setUseCreditCard(final Boolean useCreditCard)
	{
		this.useCreditCard = useCreditCard;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * @param firstName
	 *           the firstName to set
	 */
	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * @param lastName
	 *           the lastName to set
	 */
	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * @return the selectedBrand
	 */
	public String getSelectedBrand()
	{
		return selectedBrand;
	}

	/**
	 * @param selectedBrand
	 *           the selectedBrand to set
	 */
	public void setSelectedBrand(final String selectedBrand)
	{
		this.selectedBrand = selectedBrand;
	}

	/**
	 * @return the socialSecurityNumber
	 */
	public String getSocialSecurityNumber()
	{
		return socialSecurityNumber;
	}

	/**
	 * @param socialSecurityNumber
	 *           the socialSecurityNumber to set
	 */
	public void setSocialSecurityNumber(final String socialSecurityNumber)
	{
		this.socialSecurityNumber = socialSecurityNumber;
	}

	/**
	 * @return the shopperStatement
	 */
	public String getShopperStatement()
	{
		return shopperStatement;
	}

	/**
	 * @param shopperStatement
	 *           the shopperStatement to set
	 */
	public void setShopperStatement(final String shopperStatement)
	{
		this.shopperStatement = shopperStatement;
	}

	/**
	 * @return the installmets
	 */
	public String getInstallments()
	{
		return installments;
	}

	/**
	 * @param installmets
	 *           the installmets to set
	 */
	public void setInstallments(final String installments)
	{
		this.installments = installments;
	}

	/**
	 * @return the savedPaymentMethodCardNumber
	 */
	public String getSavedPaymentMethodCardNumber()
	{
		return savedPaymentMethodCardNumber;
	}

	/**
	 * @param savedPaymentMethodCardNumber
	 *           the savedPaymentMethodCardNumber to set
	 */
	public void setSavedPaymentMethodCardNumber(final String savedPaymentMethodCardNumber)
	{
		this.savedPaymentMethodCardNumber = savedPaymentMethodCardNumber;
	}

	/**
	 * @return the savedPaymentMethodNumber
	 */
	public String getSavedPaymentMethodNumber()
	{
		return savedPaymentMethodNumber;
	}

	/**
	 * @param savedPaymentMethodNumber
	 *           the savedPaymentMethodNumber to set
	 */
	public void setSavedPaymentMethodNumber(final String savedPaymentMethodNumber)
	{
		this.savedPaymentMethodNumber = savedPaymentMethodNumber;
	}

	/**
	 * @return the savedPaymentMethodOwner
	 */
	public String getSavedPaymentMethodOwner()
	{
		return savedPaymentMethodOwner;
	}

	/**
	 * @param savedPaymentMethodOwner
	 *           the savedPaymentMethodOwner to set
	 */
	public void setSavedPaymentMethodOwner(final String savedPaymentMethodOwner)
	{
		this.savedPaymentMethodOwner = savedPaymentMethodOwner;
	}

	/**
	 * @return the savedPaymentMethodType
	 */
	public String getSavedPaymentMethodType()
	{
		return savedPaymentMethodType;
	}

	/**
	 * @param savedPaymentMethodType
	 *           the savedPaymentMethodType to set
	 */
	public void setSavedPaymentMethodType(final String savedPaymentMethodType)
	{
		this.savedPaymentMethodType = savedPaymentMethodType;
	}

	/**
	 * @return the savedPaymentMethodId
	 */
	public String getSavedPaymentMethodId()
	{
		return savedPaymentMethodId;
	}

	/**
	 * @param savedPaymentMethodId
	 *           the savedPaymentMethodId to set
	 */
	public void setSavedPaymentMethodId(final String savedPaymentMethodId)
	{
		this.savedPaymentMethodId = savedPaymentMethodId;
	}

	/**
	 * @return the savedPaymentMethodCVC
	 */
	public String getSavedPaymentMethodCVC()
	{
		return savedPaymentMethodCVC;
	}

	/**
	 * @param savedPaymentMethodCVC
	 *           the savedPaymentMethodCVC to set
	 */
	public void setSavedPaymentMethodCVC(final String savedPaymentMethodCVC)
	{
		this.savedPaymentMethodCVC = savedPaymentMethodCVC;
	}

	/**
	 * @return the savePaymentMethodExpiryMonth
	 */
	public String getSavePaymentMethodExpiryMonth()
	{
		return savePaymentMethodExpiryMonth;
	}

	/**
	 * @param savePaymentMethodExpiryMonth
	 *           the savePaymentMethodExpiryMonth to set
	 */
	public void setSavePaymentMethodExpiryMonth(final String savePaymentMethodExpiryMonth)
	{
		this.savePaymentMethodExpiryMonth = savePaymentMethodExpiryMonth;
	}

	/**
	 * @return the savePaymentMethodExpiryYear
	 */
	public String getSavePaymentMethodExpiryYear()
	{
		return savePaymentMethodExpiryYear;
	}

	/**
	 * @param savePaymentMethodExpiryYear
	 *           the savePaymentMethodExpiryYear to set
	 */
	public void setSavePaymentMethodExpiryYear(final String savePaymentMethodExpiryYear)
	{
		this.savePaymentMethodExpiryYear = savePaymentMethodExpiryYear;
	}

	/**
	 * @return the securityCode
	 */
	public String getSecurityCode()
	{
		return securityCode;
	}

	/**
	 * @param securityCode
	 *           the securityCode to set
	 */
	public void setSecurityCode(final String securityCode)
	{
		this.securityCode = securityCode;
	}

	/**
	 * @return the savePayment
	 */
	public boolean isSavePayment()
	{
		return savePayment;
	}

	/**
	 * @param savePayment
	 *           the savePayment to set
	 */
	public void setSavePayment(final boolean savePayment)
	{
		this.savePayment = savePayment;
	}

	/**
	 * @return the paymentId
	 */
	public String getPaymentId()
	{
		return paymentId;
	}

	/**
	 * @param paymentId
	 *           the paymentId to set
	 */
	public void setPaymentId(final String paymentId)
	{
		this.paymentId = paymentId;
	}

	/**
	 * @return the cardTypeCode
	 */
	public String getCardTypeCode()
	{
		return cardTypeCode;
	}

	/**
	 * @param cardTypeCode
	 *           the cardTypeCode to set
	 */
	public void setCardTypeCode(final String cardTypeCode)
	{
		this.cardTypeCode = cardTypeCode;
	}

	/**
	 * @return the nameOnCard
	 */
	public String getNameOnCard()
	{
		return nameOnCard;
	}

	/**
	 * @param nameOnCard
	 *           the nameOnCard to set
	 */
	public void setNameOnCard(final String nameOnCard)
	{
		this.nameOnCard = nameOnCard;
	}

	/**
	 * @return the cardNumber
	 */
	public String getCardNumber()
	{
		return cardNumber;
	}

	/**
	 * @param cardNumber
	 *           the cardNumber to set
	 */
	public void setCardNumber(final String cardNumber)
	{
		this.cardNumber = cardNumber;
	}

	/**
	 * @return the startMonth
	 */
	public String getStartMonth()
	{
		return startMonth;
	}

	/**
	 * @param startMonth
	 *           the startMonth to set
	 */
	public void setStartMonth(final String startMonth)
	{
		this.startMonth = startMonth;
	}

	/**
	 * @return the startYear
	 */
	public String getStartYear()
	{
		return startYear;
	}

	/**
	 * @param startYear
	 *           the startYear to set
	 */
	public void setStartYear(final String startYear)
	{
		this.startYear = startYear;
	}

	/**
	 * @return the expiryMonth
	 */
	public String getExpiryMonth()
	{
		return expiryMonth;
	}

	/**
	 * @param expiryMonth
	 *           the expiryMonth to set
	 */
	public void setExpiryMonth(final String expiryMonth)
	{
		this.expiryMonth = expiryMonth;
	}

	/**
	 * @return the expiryYear
	 */
	public String getExpiryYear()
	{
		return expiryYear;
	}

	/**
	 * @param expiryYear
	 *           the expiryYear to set
	 */
	public void setExpiryYear(final String expiryYear)
	{
		this.expiryYear = expiryYear;
	}

	/**
	 * @return the issueNumber
	 */
	public String getIssueNumber()
	{
		return issueNumber;
	}

	/**
	 * @param issueNumber
	 *           the issueNumber to set
	 */
	public void setIssueNumber(final String issueNumber)
	{
		this.issueNumber = issueNumber;
	}

	/**
	 * @return the saveInAccount
	 */
	public Boolean getSaveInAccount()
	{
		return saveInAccount;
	}

	/**
	 * @param saveInAccount
	 *           the saveInAccount to set
	 */
	public void setSaveInAccount(final Boolean saveInAccount)
	{
		this.saveInAccount = saveInAccount;
	}

	/**
	 * @return the newBillingAddress
	 */
	public Boolean getNewBillingAddress()
	{
		return newBillingAddress;
	}

	/**
	 * @param newBillingAddress
	 *           the newBillingAddress to set
	 */
	public void setNewBillingAddress(final Boolean newBillingAddress)
	{
		this.newBillingAddress = newBillingAddress;
	}

	/**
	 * @return the billingAddress
	 */
	public AddressForm getBillingAddress()
	{
		return billingAddress;
	}

	/**
	 * @param billingAddress
	 *           the billingAddress to set
	 */
	public void setBillingAddress(final AddressForm billingAddress)
	{
		this.billingAddress = billingAddress;
	}
	/**
	 * @return the issuerId
	 */
	public String getIssuerId()
	{
		return issuerId;
	}

	/**
	 * @param issuerId
	 *           the issuerId to set
	 */
	public void setIssuerId(String issuerId)
	{
		this.issuerId = issuerId;
	}


}
