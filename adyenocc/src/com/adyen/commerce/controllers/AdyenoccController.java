/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.adyen.commerce.controllers;

import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/{baseSiteId}/test")
public class AdyenoccController
{
    @GetMapping
    @ApiBaseSiteIdParam
    public String getNewResource()
    {
        return "newSampleResource";
    }
}
