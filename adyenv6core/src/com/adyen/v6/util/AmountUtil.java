package com.adyen.v6.util;

import com.adyen.model.checkout.Amount;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.math.BigDecimal;

public class AmountUtil {

    public static Amount createAmount(BigDecimal value, String currency) {
        Assert.notNull(value, "Value cannot be null");
        Assert.isTrue(StringUtils.isNotBlank(currency), "Currency cannot be null or empty");
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setValue(value.movePointRight(2).longValue());
        return amount;
    }


}
