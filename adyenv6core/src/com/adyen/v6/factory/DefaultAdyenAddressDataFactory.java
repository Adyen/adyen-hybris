/*
 *                        ######
 *                        ######
 *  ############    ####( ######  #####. ######  ############   ############
 *  #############  #####( ######  #####. ######  #############  #############
 *         ######  #####( ######  #####. ######  #####  ######  #####  ######
 *  ###### ######  #####( ######  #####. ######  #####  #####   #####  ######
 *  ###### ######  #####( ######  #####. ######  #####          #####  ######
 *  #############  #############  #############  #############  #####  ######
 *   ############   ############  #############   ############  #####  ######
 *                                       ######
 *                                #############
 *                                ############
 *
 *  Adyen Hybris Extension
 *
 *  Copyright (c) 2017 Adyen B.V.
 *  This file is open source and available under the MIT license.
 *  See the LICENSE file for more info.
 */
package com.adyen.v6.factory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultAdyenAddressDataFactory implements AdyenAddressDataFactory {
    private ModelService modelService;
    private CommonI18NService commonI18NService;

    private static final Logger LOG = Logger.getLogger(DefaultAdyenAddressDataFactory.class);

    @Override
    public AddressData createAddressData(String country, String state, String stateOrProvince, String city, String street, String postalCode, String firstName, String lastName) {
        AddressData addressData = new AddressData();

        addressData.setFirstName(firstName);
        addressData.setLastName(lastName);
        addressData.setLine1(street);
        addressData.setPostalCode(postalCode);
        addressData.setTown(city);
        addressData.setVisibleInAddressBook(false);
        addressData.setDefaultAddress(false);

        //Find country by iso code
        if (StringUtils.isNotBlank(country)) {
            final CountryData countryData = new CountryData();
            countryData.setIsocode(country);
            addressData.setCountry(countryData);
        }

        //Find Region/state
        if (StringUtils.isNotBlank(stateOrProvince) && StringUtils.isNotBlank(country)) {
            final RegionData regionData = new RegionData();
            final CountryModel countryModel = commonI18NService.getCountry(country);
            final String isocode = country.concat("-").concat(stateOrProvince);
            try {
                final RegionModel regionModel = commonI18NService.getRegion(countryModel, isocode);

                regionData.setIsocode(regionModel.getIsocode());
                addressData.setRegion(regionData);
            } catch (final UnknownIdentifierException e) {
                LOG.info("No region with the code " + isocode + " found.", e);
            }
        }

        return addressData;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
