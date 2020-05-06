package com.adyen.v6.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 */
public class AddressForm1
{
	private String addressId;
	private String titleCode;
	private String firstName;
	private String lastName;
	private String line1;
	private String line2;
	private String townCity;
	private String regionIso;
	private String postcode;
	private String countryIsoCode;

	public String getCountryIso() {
		return countryIso;
	}

	public void setCountryIso(String countryIso) {
		this.countryIso = countryIso;
	}

	private String countryIso;
	private String phoneNumber;
	private String state;
	private Boolean saveInAddressBook;
	private Boolean defaultAddress;
	private Boolean shippingAddress;
	private Boolean billingAddress;
	private Boolean editAddress;
	private String phone;

	public String getAddressId()
	{
		return addressId;
	}

	public void setAddressId(final String addressId)
	{
		this.addressId = addressId;
	}

	@NotNull(message = "{address.title.invalid}")
	@Size(min = 1, max = 255, message = "{address.title.invalid}")
	public String getTitleCode()
	{
		return titleCode;
	}

	public void setTitleCode(final String titleCode)
	{
		this.titleCode = titleCode;
	}

	@NotNull(message = "{address.firstName.invalid}")
	@Size(min = 1, max = 255, message = "{address.firstName.invalid}")
	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	@NotNull(message = "{address.lastName.invalid}")
	@Size(min = 1, max = 255, message = "{address.lastName.invalid}")
	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

	@NotNull(message = "{address.line1.invalid}")
	@Size(min = 1, max = 255, message = "{address.line1.invalid}")
	public String getLine1()
	{
		return line1;
	}

	public void setLine1(final String line1)
	{
		this.line1 = line1;
	}

	public String getLine2()
	{
		return line2;
	}

	public void setLine2(final String line2)
	{
		this.line2 = line2;
	}

	@NotNull(message = "{address.townCity.invalid}")
	@Size(min = 1, max = 255, message = "{address.townCity.invalid}")
	public String getTownCity()
	{
		return townCity;
	}

	public void setTownCity(final String townCity)
	{
		this.townCity = townCity;
	}

	public String getRegionIso()
	{
		return regionIso;
	}

	public void setRegionIso(final String regionIso)
	{
		this.regionIso = regionIso;
	}

	@NotNull(message = "{address.postcode.invalid}")
	@Size(min = 1, max = 10, message = "{address.postcode.invalid}")
	public String getPostcode()
	{
		return postcode;
	}

	public void setPostcode(final String postcode)
	{
		this.postcode = postcode;
	}

	@NotNull(message = "{address.country.invalid}")
	@Size(min = 1, max = 255, message = "{address.country.invalid}")
	public String getCountryIsoCode()
	{
		return countryIsoCode;
	}

	public void setCountryIsoCode(final String countryIsoCode)
	{
		this.countryIsoCode = countryIsoCode;
	}

	public Boolean getSaveInAddressBook()
	{
		return saveInAddressBook;
	}

	public void setSaveInAddressBook(final Boolean saveInAddressBook)
	{
		this.saveInAddressBook = saveInAddressBook;
	}

	public Boolean getDefaultAddress()
	{
		return defaultAddress;
	}

	public void setDefaultAddress(final Boolean defaultAddress)
	{
		this.defaultAddress = defaultAddress;
	}

	public Boolean getShippingAddress() {
		return shippingAddress;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setShippingAddress(final Boolean shippingAddress)
	{
		this.shippingAddress = shippingAddress;
	}

	public Boolean getBillingAddress()
	{
		return billingAddress;
	}

	public void setBillingAddress(final Boolean billingAddress)
	{
		this.billingAddress = billingAddress;
	}

	public Boolean getEditAddress()
	{
		return editAddress;
	}

	public void setEditAddress(final Boolean editAddress)
	{
		this.editAddress = editAddress;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(final String value)
	{
		phone = value;
	}
	@Override
	public String toString() {
		return "AddressForm{"
				+ "titleCode='"
				+ titleCode
				+ '\''
				+ ", firstName='"
				+ firstName
				+ '\''
				+ ", lastName='"
				+ lastName
				+ '\''
				+ ", line1='"
				+ line1
				+ '\''
				+ ", line2='"
				+ line2
				+ '\''
				+ ", townCity='"
				+ townCity
				+ '\''
				+ ", postcode='"
				+ postcode
				+ '\''
				+ ", countryIso='"
				+ countryIsoCode
				+ '\''
				+ ", phoneNumber='"
				+ phoneNumber
				+ '}';
	}

}
