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

import de.hybris.platform.commercefacades.user.data.AddressData;

public interface AdyenAddressDataFactory {
    /**
     * Factory Method for providing AddressData
     * @param country country
     * @param state state
     * @param stateOrProvince state or province
     * @param city city
     * @param street street
     * @param postalCode postal code
     * @param firstName first name
     * @param lastName last name
     * @return AddressData
     */
    AddressData createAddressData(String country, String state, String stateOrProvince, String city, String street, String postalCode, String firstName, String lastName);
}
