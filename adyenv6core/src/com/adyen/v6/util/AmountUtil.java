package com.adyen.v6.util;

import com.adyen.model.checkout.Amount;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.adyen.util.Util.getDecimalPlaces;

public class AmountUtil {

    public static Amount createAmount(BigDecimal value, String currency) {
        Assert.notNull(value, "Value cannot be null");
        Assert.isTrue(StringUtils.isNotBlank(currency), "Currency cannot be null or empty");
        Amount amount = new Amount();
        amount.setCurrency(currency);
        int scale = getDecimalPlaces(currency);
        amount.setValue(BigDecimal.TEN.pow(scale).multiply(value.setScale(scale, RoundingMode.HALF_UP)).longValue());
        return amount;
    }

    public static BigDecimal calculateAmountWithTaxes(final AbstractOrderModel abstractOrderModel) {
        final Double totalPrice = abstractOrderModel.getTotalPrice();
        final Double totalTax = Boolean.TRUE.equals(abstractOrderModel.getNet()) ? abstractOrderModel.getTotalTax() : Double.valueOf(0d);
        final BigDecimal totalPriceWithoutTaxBD = BigDecimal.valueOf(totalPrice == null ? 0d : totalPrice).setScale(2,
                RoundingMode.HALF_EVEN);
        return BigDecimal.valueOf(totalTax == null ? 0d : totalTax)
                .setScale(2, RoundingMode.HALF_EVEN).add(totalPriceWithoutTaxBD);
    }

}
