package com.tss.factory;

import com.tss.enums.PaymentType;
import com.tss.service.*;

public class PaymentFactory {
    public static IPaymentService getPaymentService(PaymentType paymentType) {
        if (paymentType == null) {
            throw new IllegalArgumentException("Payment type cannot be null");
        }

        switch (paymentType) {
            case CASH: {
                return new CashPaymentService();
            }

            case UPI:{
                return new UPIPaymentService();
            }

            default:
                throw new IllegalArgumentException("Invalid payment type: " + paymentType);
        }
    }
}
