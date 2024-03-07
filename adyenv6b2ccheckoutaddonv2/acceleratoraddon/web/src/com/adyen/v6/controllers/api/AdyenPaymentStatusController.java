package com.adyen.v6.controllers.api;

import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/api/checkout")
public class AdyenPaymentStatusController {

    @Autowired
    PaymentService paymentService;

    @PostMapping("/get-payment-status")
    public ResponseEntity<String> postIdForPaymentStatus(@RequestBody String transactionId) {

        PaymentTransactionEntryModel paymentTransactionEntry = paymentService.getPaymentTransactionEntry(transactionId);
        if (paymentTransactionEntry != null) {
            String transactionStatus = paymentTransactionEntry.getTransactionStatus();
            String extracted = getMessageFromStatus(transactionStatus);
            return ResponseEntity.ok().body(extracted);
        } else {
            return ResponseEntity.badRequest().build();
        }


    }

    private String getMessageFromStatus(String transactionStatus) {
        switch (transactionStatus) {
            case "ACCEPTED": {
                return "Completed.";
            }
            case "REJECTED": {
                return "Rejected.";
            }
            case "REVIEW": {
                return "Waiting";
            }
            default: {
                return "Error.";
            }
        }
    }

}
