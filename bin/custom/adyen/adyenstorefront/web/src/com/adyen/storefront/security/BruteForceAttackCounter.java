/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.adyen.storefront.security;

/**
 * Interface for checking brute force attack attempts.
 */
public interface BruteForceAttackCounter
{
    /**
     * Method registers user login failure.
     *
     * @param userUid that the failure is registered for
     */
    void registerLoginFailure(final String userUid);


    /**
     * Method checks if user reached attack threshold.
     *
     * @param userUid user uid against which the check is performed
     * @return true if this one is an attack
     */
    boolean isAttack(final String userUid);


    /**
     * Method resets the counter for the given user uid
     *
     * @param userUid user uid that failed logins counter will be reset
     */
    void resetUserCounter(final String userUid);


    /**
     * Method returns current user failed login counter value.
     *
     * @param userUid user uid to return failed login number
     * @return the number of failed logins for the user
     */
    int getUserFailedLogins(final String userUid);
}
