package com.tss.repository;

import com.tss.entity.Payment;
import com.tss.enums.PaymentType;

public interface IPaymentRepo {
    int savePayment(int orderId, PaymentType paymentType, double amount);
    Payment getPaymentByOrderId(int orderId);
}
