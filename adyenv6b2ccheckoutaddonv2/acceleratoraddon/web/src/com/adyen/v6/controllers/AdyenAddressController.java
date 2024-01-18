package com.adyen.v6.controllers;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.AddressValidator;
import de.hybris.platform.acceleratorstorefrontcommons.util.AddressDataUtil;
import de.hybris.platform.commercefacades.address.AddressVerificationFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping(value = "/api/account")
public class AdyenAddressController {

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private AddressVerificationFacade addressVerificationFacade;

    @Resource(name = "addressValidator")
    private AddressValidator addressValidator;

    @Resource(name = "addressDataUtil")
    private AddressDataUtil addressDataUtil;

    @RequireHardLogIn
    @GetMapping(value = "/delivery-address", produces = "application/json")
    public ResponseEntity<List<AddressData>> getAllDeliveryAddresses() {
        List<AddressData> addressDataList = getUserFacade().getAddressBook();

        return ResponseEntity.status(HttpStatus.OK).body(addressDataList);
    }

    @RequireHardLogIn
    @PostMapping(value = "/delivery-address")
    public ResponseEntity<AddressData> addDeliveryAddress(@RequestBody AddressForm addressForm) {
        final Errors errors = new BeanPropertyBindingResult(addressForm, "address");
        addressValidator.validate(addressForm, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        AddressData addressData = addressDataUtil.convertToAddressData(addressForm);

        if (addressVerificationFacade.verifyAddressData(addressData).getDecision().equals(AddressVerificationDecision.ACCEPT)) {
            addressData.setVisibleInAddressBook(true);
            getUserFacade().addAddress(addressData);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @RequireHardLogIn
    @PutMapping(value = "/delivery-address")
    public ResponseEntity<AddressData> updateDeliveryAddress(@RequestBody AddressForm addressForm) {
        final Errors errors = new BeanPropertyBindingResult(addressForm, "address");
        addressValidator.validate(addressForm, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        AddressData addressData = addressDataUtil.convertToAddressData(addressForm);


        if (addressVerificationFacade.verifyAddressData(addressData).getDecision().equals(AddressVerificationDecision.ACCEPT)) {
            if (getUserFacade().getAddressBook().stream().anyMatch(ad -> Objects.equals(ad.getId(), addressData.getId()))) {
                addressData.setVisibleInAddressBook(true);
                getUserFacade().editAddress(addressData);

                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @RequireHardLogIn
    @DeleteMapping(value = "/delivery-address/{addressId}")
    public ResponseEntity<HttpStatus> removeDeliveryAddress(@PathVariable String addressId) {
        if (getUserFacade().getAddressBook().stream().anyMatch(ad -> Objects.equals(ad.getId(), addressId))) {
            AddressData addressData = getUserFacade().getAddressForCode(addressId);

            getUserFacade().removeAddress(addressData);
            addressData.setVisibleInAddressBook(false);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public AddressVerificationFacade getAddressVerificationFacade() {
        return addressVerificationFacade;
    }
}
