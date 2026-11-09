package ca.yorku.eecs4413.fritzev.service.account;

import ca.yorku.eecs4413.fritzev.controller.dto.*;
import ca.yorku.eecs4413.fritzev.dao.*;
import ca.yorku.eecs4413.fritzev.domain.*;
import ca.yorku.eecs4413.fritzev.exception.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import org.springframework.stereotype.Service;

// STUB - M4 Account Manager (D1 interfaces IAuth + IAccount).
@Service
public class AccountServiceImpl implements AccountService {

    private final UserDAO userDAO;
    private final AddressDAO addressDAO;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public AccountServiceImpl(UserDAO userDAO, AddressDAO addressDAO, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.addressDAO = addressDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto register(RegisterRequest request) {
        // TODO(Zaaid): reject duplicate username/email with ConflictException; store passwordEncoder.encode(password); save both addresses
        throw new NotImplementedYetException("UC-01 AccountService.register is not implemented yet (owner: Zaaid, PB-19)");
    }

    @Override
    public User signIn(LoginRequest request) {
        // TODO(Zaaid): userDAO.findByUsername + passwordEncoder.matches; on failure throw UnauthorizedException("Invalid username or password") (same message for both cases)
        throw new NotImplementedYetException("UC-02 AccountService.signIn is not implemented yet (owner: Zaaid, PB-19)");
    }

    @Override
    public UserDto getProfile(Long userId) {
        // TODO(Zaaid): ResourceNotFoundException if missing
        throw new NotImplementedYetException("UC-02 AccountService.getProfile is not implemented yet (owner: Zaaid, PB-19)");
    }

    @Override
    public AddressDto getDefaultShipping(Long userId) {
        // TODO(Zaaid): addressDAO.findByUserIdAndType(userId, AddressType.SHIPPING)
        throw new NotImplementedYetException("UC-14 AccountService.getDefaultShipping is not implemented yet (owner: Zaaid, PB-19)");
    }

    @Override
    public boolean isVerifiedOwner(Long userId) {
        // TODO(Zaaid): read User.verifiedOwner
        throw new NotImplementedYetException("UC-15 AccountService.isVerifiedOwner is not implemented yet (owner: Zaaid, PB-19)");
    }
}
