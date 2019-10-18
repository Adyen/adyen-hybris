package com.adyen.v6.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.adyen.model.nexo.DocumentQualifierType;
import com.adyen.model.nexo.ErrorConditionType;
import com.adyen.model.nexo.OutputText;
import com.adyen.model.nexo.PaymentReceipt;
import com.adyen.model.nexo.ResultType;
import com.adyen.model.terminal.TerminalAPIResponse;
import com.google.common.base.Splitter;

public class TerminalAPIUtil {
    public static final Logger LOGGER = Logger.getLogger(TerminalAPIUtil.class);

    public static ResultType getStatusResultFromStatusResponse(TerminalAPIResponse terminalApiResponse) {
        if (terminalApiResponse.getSaleToPOIResponse() != null
                && terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse() != null
                && terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse().getResponse() != null) {

            return terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse().getResponse().getResult();
        }
        return null;
    }

    public static ErrorConditionType getErrorConditionForPaymentFromStatusResponse(TerminalAPIResponse terminalApiResponse) {
        if (terminalApiResponse != null
                && terminalApiResponse.getSaleToPOIResponse() != null
                && terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse() != null
                && terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse().getRepeatedMessageResponse() != null
                && terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse().getRepeatedMessageResponse().getRepeatedResponseMessageBody() != null
                && terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse().getRepeatedMessageResponse().getRepeatedResponseMessageBody().getPaymentResponse() != null
                && terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse().getRepeatedMessageResponse().getRepeatedResponseMessageBody().getPaymentResponse().getResponse() != null) {
            return terminalApiResponse.getSaleToPOIResponse()
                                      .getTransactionStatusResponse()
                                      .getRepeatedMessageResponse()
                                      .getRepeatedResponseMessageBody()
                                      .getPaymentResponse()
                                      .getResponse()
                                      .getErrorCondition();
        }
        return null;
    }

    public static ErrorConditionType getErrorConditionForPaymentResponse(TerminalAPIResponse terminalApiResponse) {
        if (terminalApiResponse != null
                && terminalApiResponse.getSaleToPOIResponse() != null
                && terminalApiResponse.getSaleToPOIResponse().getPaymentResponse() != null
                && terminalApiResponse.getSaleToPOIResponse().getPaymentResponse().getResponse() != null) {

            return terminalApiResponse.getSaleToPOIResponse().getPaymentResponse().getResponse().getErrorCondition();
        }
        return null;
    }

    public static ErrorConditionType getErrorConditionForStatusFromStatusResponse(TerminalAPIResponse terminalApiResponse) {
        if (terminalApiResponse != null && terminalApiResponse.getSaleToPOIResponse() != null && terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse() != null) {
            return terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse().getResponse().getErrorCondition();
        }
        return null;
    }

    /**
     * @param terminalApiResponse
     * @return Result from payment response present in paymentResponse in terminalApiResponse for POS payment or POS status call, if present.
     * Otherwise returns Failure.
     */
    public static ResultType getPaymentResultFromStatusOrPaymentResponse(TerminalAPIResponse terminalApiResponse) {

        if (terminalApiResponse != null && terminalApiResponse.getSaleToPOIResponse() != null) {

            if (terminalApiResponse.getSaleToPOIResponse().getPaymentResponse() != null) {
                if (terminalApiResponse.getSaleToPOIResponse().getPaymentResponse().getResponse() != null) {
                    return terminalApiResponse.getSaleToPOIResponse().getPaymentResponse().getResponse().getResult();
                }
            } else if (terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse() != null) {
                if (terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse().getRepeatedMessageResponse() != null
                        && terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse().getRepeatedMessageResponse().getRepeatedResponseMessageBody() != null
                        && terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse().getRepeatedMessageResponse().getRepeatedResponseMessageBody().getPaymentResponse() != null
                        && terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse().getRepeatedMessageResponse().getRepeatedResponseMessageBody().getPaymentResponse().getResponse()
                        != null) {
                    return terminalApiResponse.getSaleToPOIResponse()
                                              .getTransactionStatusResponse()
                                              .getRepeatedMessageResponse()
                                              .getRepeatedResponseMessageBody()
                                              .getPaymentResponse()
                                              .getResponse()
                                              .getResult();
                }
            }
        }
        return ResultType.FAILURE;
    }

    public static String getReceiptFromPaymentResponse(TerminalAPIResponse terminalApiResponse) {
        String posReceipt = null;
        if (terminalApiResponse != null && terminalApiResponse.getSaleToPOIResponse() != null && terminalApiResponse.getSaleToPOIResponse().getPaymentResponse() != null) {
            posReceipt = TerminalAPIUtil.formatTerminalAPIReceipt(terminalApiResponse.getSaleToPOIResponse().getPaymentResponse().getPaymentReceipt());
        }
        return posReceipt;
    }

    public static String getReceiptFromStatusResponse(TerminalAPIResponse terminalApiResponse) {

        String posReceipt = null;

        if (terminalApiResponse != null
                && terminalApiResponse.getSaleToPOIResponse() != null
                && terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse() != null
                && terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse().getRepeatedMessageResponse() != null
                && terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse().getRepeatedMessageResponse().getRepeatedResponseMessageBody() != null
                && terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse().getRepeatedMessageResponse().getRepeatedResponseMessageBody().getPaymentResponse() != null) {


            posReceipt = formatTerminalAPIReceipt(terminalApiResponse.getSaleToPOIResponse()
                                                                     .getTransactionStatusResponse()
                                                                     .getRepeatedMessageResponse()
                                                                     .getRepeatedResponseMessageBody()
                                                                     .getPaymentResponse()
                                                                     .getPaymentReceipt());
        }
        return posReceipt;
    }

    public static String formatTerminalAPIReceipt(List<PaymentReceipt> paymentReceipts) {
        String formattedHtml = "<table class='terminal-api-receipt'>";
        for (PaymentReceipt paymentReceipt : paymentReceipts) {
            if (paymentReceipt.getDocumentQualifier() == DocumentQualifierType.CUSTOMER_RECEIPT) {
                for (OutputText outputText : paymentReceipt.getOutputContent().getOutputText()) {
                    Map<String, String> map = Splitter.on("&").withKeyValueSeparator('=').split(outputText.getText());
                    formattedHtml += "<tr class='terminal-api-receipt'>";
                    if ((map.get("name") != null)) {
                        formattedHtml += "<td class='terminal-api-receipt-name'>" + map.get("name") + "</td>";
                    } else {
                        formattedHtml += "<td class='terminal-api-receipt-name'>&nbsp;</td>";
                    }
                    if (map.get("value") != null) {
                        formattedHtml += "<td class='terminal-api-receipt-value' align='right'>" + map.get("value") + "</td>";
                    } else {
                        formattedHtml += "<td class='terminal-api-receipt-value' align='right'>&nbsp;</td>";
                    }
                    formattedHtml += "</tr>";
                }
            }
        }
        formattedHtml += "</table>";
        try {
            formattedHtml = URLDecoder.decode(formattedHtml, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.debug("Exception in receipt generation  " + e.getMessage());
        }
        return formattedHtml;
    }

    public static String getErrorMessageForNonAuthorizedPosPayment(TerminalAPIResponse terminalApiResponse) {
        String errorMessage;

        if (terminalApiResponse.getSaleToPOIResponse() != null && terminalApiResponse.getSaleToPOIResponse().getPaymentResponse() != null) {
            ErrorConditionType errorCondition = TerminalAPIUtil.getErrorConditionForPaymentResponse(terminalApiResponse);
            errorMessage = TerminalAPIUtil.getErrorMessageByPosErrorCondition(errorCondition);
        } else if (terminalApiResponse.getSaleToPOIResponse() != null && terminalApiResponse.getSaleToPOIResponse().getTransactionStatusResponse() != null) {
            ErrorConditionType errorCondition = TerminalAPIUtil.getErrorConditionForPaymentFromStatusResponse(terminalApiResponse);
            errorMessage = TerminalAPIUtil.getErrorMessageByPosErrorCondition(errorCondition);
        } else {
            // probably SaleToPOIRequest, that means terminal unreachable, return the response as error
            errorMessage = "checkout.error.authorization.pos.configuration";
        }
        return errorMessage;
    }

    public static String getErrorMessageByPosErrorCondition(ErrorConditionType errorCondition) {
        LOGGER.debug("getErrorMessageByPosErrorCondition: " + errorCondition);

        String errorMessage = "checkout.error.authorization.payment.error";

        if (errorCondition != null) {
            switch (errorCondition) {
                case ABORTED:
                case CANCEL:
                    errorMessage = "checkout.error.authorization.payment.cancelled";
                    break;
                case BUSY:
                case IN_PROGRESS:
                    errorMessage = "checkout.error.authorization.pos.busy";
                    break;
                case INVALID_CARD:
                case NOT_ALLOWED:
                case PAYMENT_RESTRICTION:
                    errorMessage = "checkout.error.authorization.transaction.not.permitted";
                    break;
                case REFUSAL:
                    errorMessage = "checkout.error.authorization.payment.refused";
                    break;
                case DEVICE_OUT:
                case MESSAGE_FORMAT:
                case NOT_FOUND:
                case UNAVAILABLE_DEVICE:
                case UNAVAILABLE_SERVICE:
                case UNREACHABLE_HOST:
                    errorMessage = "checkout.error.authorization.pos.configuration";
                    break;
                case WRONG_PIN:
                    errorMessage = "checkout.error.authorization.pos.pin";
                    break;
                default:
                    errorMessage = "checkout.error.authorization.payment.error";
            }
        }

        return errorMessage;
    }

}
