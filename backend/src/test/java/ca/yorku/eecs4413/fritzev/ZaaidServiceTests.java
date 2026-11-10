package ca.yorku.eecs4413.fritzev;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.*;
import ca.yorku.eecs4413.fritzev.domain.*;
import ca.yorku.eecs4413.fritzev.exception.ForbiddenException;
import ca.yorku.eecs4413.fritzev.integration.payment.MockPaymentGateway;
import ca.yorku.eecs4413.fritzev.service.account.AccountServiceImpl;
import ca.yorku.eecs4413.fritzev.service.review.ReviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZaaidServiceTests {
    @Mock UserDAO userDAO; @Mock AddressDAO addressDAO; @Mock PasswordEncoder encoder;

    @Test
    void registrationStoresHashAndNeverReturnsIt() {
        when(userDAO.existsByUsername(any())).thenReturn(false); when(userDAO.existsByEmail(any())).thenReturn(false);
        when(encoder.encode("Password123!")).thenReturn("$2a$hashed");
        when(userDAO.save(any(User.class))).thenAnswer(inv -> { User u = inv.getArgument(0); u.setId(99L); return u; });
        var service = new AccountServiceImpl(userDAO, addressDAO, encoder);
        var address = new AddressDto("1 Main St", "Toronto", "ON", "Canada", "M3J 1P3", "416-555-0101");
        UserDto result = service.register(new RegisterRequest("newuser", "new@example.com", "Password123!", "New", "User", address, address));
        assertEquals(99L, result.id()); assertEquals("newuser", result.username()); assertFalse(result.toString().contains("hashed"));
        verify(encoder).encode("Password123!"); verify(addressDAO, times(2)).save(any(Address.class));
    }

    @Test
    void invalidLoginUsesOneGenericError() {
        User user = new User(); user.setPasswordHash("hash"); when(userDAO.findByUsername("alice")).thenReturn(Optional.of(user)); when(encoder.matches("wrong", "hash")).thenReturn(false);
        var service = new AccountServiceImpl(userDAO, addressDAO, encoder);
        var error = assertThrows(RuntimeException.class, () -> service.signIn(new LoginRequest("alice", "wrong")));
        assertEquals("Invalid username or password", error.getMessage());
    }

    @Test
    void mockPaymentApprovesTwoThenDeniesAndRepeats() {
        PaymentAttemptCounterDAO dao = mock(PaymentAttemptCounterDAO.class); PaymentAttemptCounter counter = new PaymentAttemptCounter(1L, 0);
        when(dao.findById(1L)).thenReturn(Optional.of(counter)); when(dao.save(any())).thenAnswer(i -> i.getArgument(0));
        var gateway = new MockPaymentGateway(dao); var card = new CardInfo("A", "4111111111111111", "12/30", "123");
        assertTrue(gateway.authorize(card, BigDecimal.ONE).approved()); assertTrue(gateway.authorize(card, BigDecimal.ONE).approved()); assertFalse(gateway.authorize(card, BigDecimal.ONE).approved());
        assertTrue(gateway.authorize(card, BigDecimal.ONE).approved()); assertTrue(gateway.authorize(card, BigDecimal.ONE).approved()); assertFalse(gateway.authorize(card, BigDecimal.ONE).approved());
    }

    @Test
    void unverifiedUserCannotSubmitReview() {
        UserDAO users = mock(UserDAO.class); VehicleDAO vehicles = mock(VehicleDAO.class); ReviewDAO reviews = mock(ReviewDAO.class); var accounts = mock(ca.yorku.eecs4413.fritzev.service.account.AccountService.class);
        when(accounts.isVerifiedOwner(3L)).thenReturn(false);
        var service = new ReviewServiceImpl(reviews, vehicles, users, accounts);
        var request = new ReviewRequest(5, 400, "Good", "Easy", "Good", 5, "Nice");
        assertThrows(ForbiddenException.class, () -> service.submitReview(3L, 1L, request)); verifyNoInteractions(vehicles, users, reviews);
    }
}
