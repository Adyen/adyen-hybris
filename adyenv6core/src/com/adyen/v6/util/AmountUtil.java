package com.adyen.v6.util;

import com.adyen.model.checkout.Amount;
import de.hybris.platform.commercefacades.product.data.PriceData;

import java.math.BigDecimal;

public class AmountUtil {

    public static Amount createAmount(Long value, String currency) {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setValue(value);
        return amount;
    }

    public static Amount createAmount(PriceData priceData) {
        Amount amount = new Amount();
        amount.setCurrency(priceData.getCurrencyIso());
        amount.setValue(priceData.getValue().longValue());
        return amount;
    }

    public static Amount createAmount(BigDecimal value, String currency) {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setValue(value.longValue());
        return amount;
    }

    public static Amount createAmount(String value, String currency) {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setValue(Long.parseLong(value));
        return amount;
    }

}
