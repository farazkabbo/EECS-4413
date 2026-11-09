package ca.yorku.eecs4413.fritzev.integration.payment;

import ca.yorku.eecs4413.fritzev.controller.dto.CardInfo;

import java.math.BigDecimal;

// M15 Payment Gateway Adapter (D1 interface IPayment).
public interface PaymentGateway {

    PaymentResult authorize(CardInfo card, BigDecimal amount);
}
