package com.adyen.v6.util;

import com.adyen.constants.ApiConstants;

public class ErrorMessageUtil {
    public static String getErrorMessageByRefusalReason(String refusalReason) {
        String errorMessage = "Payment refused.";
        if (refusalReason != null) {
            switch (refusalReason) {
                case ApiConstants.RefusalReason.TRANSACTION_NOT_PERMITTED:
                    errorMessage = "The transaction is not permitted.";
                    break;
                case ApiConstants.RefusalReason.CVC_DECLINED:
                    errorMessage = "The payment is REFUSED. Please check your Card details.";
                    break;
                case ApiConstants.RefusalReason.RESTRICTED_CARD:
                    errorMessage = "The card is restricted.";
                    break;
                case ApiConstants.RefusalReason.PAYMENT_DETAIL_NOT_FOUND:
                    errorMessage = "The payment is REFUSED because the saved card is removed. Please try an other payment method.";
                    break;
                default:
                    errorMessage = "Payment refused.";
            }
        }
        return errorMessage;
    }
}
