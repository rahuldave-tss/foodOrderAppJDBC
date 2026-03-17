package com.tss.factory;

import com.tss.enums.PaymentType;
import com.tss.service.CashPaymentService;
import com.tss.service.IPaymentService;
import com.tss.service.UPIPaymentService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentFactoryTest {

    @Test
    void testGetCashPaymentService() {
        IPaymentService service = PaymentFactory.getPaymentService(PaymentType.CASH);
        assertNotNull(service);
        assertInstanceOf(CashPaymentService.class, service);
    }

    @Test
    void testGetUPIPaymentService() {
        IPaymentService service = PaymentFactory.getPaymentService(PaymentType.UPI);
        assertNotNull(service);
        assertInstanceOf(UPIPaymentService.class, service);
    }

    @Test
    void testNullPaymentTypeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            PaymentFactory.getPaymentService(null);
        });
    }

    @Test
    void testNullPaymentTypeExceptionMessage() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            PaymentFactory.getPaymentService(null);
        });
        assertEquals("Payment type cannot be null", ex.getMessage());
    }
}
