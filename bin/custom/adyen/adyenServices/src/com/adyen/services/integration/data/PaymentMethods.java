
package com.adyen.services.integration.data;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethods {

    private List<PaymentMethod> paymentMethods = new ArrayList<PaymentMethod>();

    /**
     * 
     * @return
     *     The paymentMethods
     */
    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    /**
     * 
     * @param paymentMethods
     *     The paymentMethods
     */
    public void setPaymentMethods(final List<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

}
