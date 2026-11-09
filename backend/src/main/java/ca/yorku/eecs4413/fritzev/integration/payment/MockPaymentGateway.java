package ca.yorku.eecs4413.fritzev.integration.payment;

import ca.yorku.eecs4413.fritzev.controller.dto.CardInfo;
import ca.yorku.eecs4413.fritzev.exception.NotImplementedYetException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// STUB - simulated Payment Service required by the spec.
@Component
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult authorize(CardInfo card, BigDecimal amount) {
        // TODO(Zaaid): implement the approve-approve-deny counter described above
        throw new NotImplementedYetException("Mock payment service is not implemented yet (owner: Zaaid, PB-25)");
    }
}
