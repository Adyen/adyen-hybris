package com.adyen.v6.repository;

import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.adyen.v6.constants.Adyenv6b2ccheckoutaddonConstants.PAYMENT_PROVIDER;

/**
 * Repository class for PaymentTransactions
 */
public class PaymentTransactionRepository extends AbstractRepository {
    private static final Logger LOG = Logger.getLogger(PaymentTransactionRepository.class);

    public PaymentTransactionModel getTransactionModel(String pspReference) {
        final Map queryParams = new HashMap();
        queryParams.put("paymentProvider", PAYMENT_PROVIDER);
        queryParams.put("requestId", pspReference);
        final FlexibleSearchQuery selectOrderQuery = new FlexibleSearchQuery(
                "SELECT {pk} FROM {" + PaymentTransactionModel._TYPECODE + "}"
                        + " WHERE {" + PaymentTransactionModel.PAYMENTPROVIDER + "} = ?paymentProvider"
                        + " AND {" + PaymentTransactionEntryModel.REQUESTID + "} = ?requestId",
                queryParams
        );

        LOG.info("Finding transaction with PSP reference: " + pspReference);

        return (PaymentTransactionModel) getOneOrNull(selectOrderQuery);
    }
}
