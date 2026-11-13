package ca.yorku.eecs4413.fritzev.service.account; 

import ca.yorku.eecs4413.fritzev.controller.dto.*; 
import ca.yorku.eecs4413.fritzev.dao.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 
import ca.yorku.eecs4413.fritzev.exception.*; 

import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 

import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional; 

// Handles account creation and sign-in for the customer-facing application. 
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
    @Transactional 
    public UserDto register(RegisterRequest request) { 
        // Roles and owner status are assigned here instead of trusting values from the browser. 
        if (userDAO.existsByUsername(request.username()) || userDAO.existsByEmail(request.email())) { 
            throw new ConflictException("Username or email is already registered"); 
        } 
        User user = new User(); 
        user.setUsername(request.username()); 
        user.setEmail(request.email()); 
        user.setPasswordHash(passwordEncoder.encode(request.password())); 
        user.setFirstName(request.firstName()); 
        user.setLastName(request.lastName()); 
        user.setRole(Role.CUSTOMER); 
        user.setVerifiedOwner(false); 
        user = userDAO.save(user); 
        saveAddress(user, request.billing(), AddressType.BILLING); 
        saveAddress(user, request.shipping(), AddressType.SHIPPING); 
        return UserDto.from(user); 
    } 

    @Override 
    public User signIn(LoginRequest request) { 
        // Keep the error deliberately generic so callers cannot discover which usernames exist. 
        User user = userDAO.findByUsername(request.username()).orElse(null); 
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) { 
            throw new UnauthorizedException("Invalid username or password"); 
        } 
        return user; 
    } 

    @Override 
    public UserDto getProfile(Long userId) { 
        return UserDto.from(userDAO.findById(userId) 
                .orElseThrow(() -> new ResourceNotFoundException("User not found"))); 
    } 

    @Override 
    public AddressDto getDefaultShipping(Long userId) { 
        Address address = addressDAO.findByUserIdAndType(userId, AddressType.SHIPPING) 
                .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found")); 
        return toDto(address); 
    } 

    @Override 
    public boolean isVerifiedOwner(Long userId) { 
        return userDAO.findById(userId).map(User::isVerifiedOwner).orElse(false); 
    } 

    private void saveAddress(User user, AddressDto dto, AddressType type) { 
        Address address = new Address(); 
        address.setUser(user); 
        address.setType(type); 
        address.setStreet(dto.street()); 
        address.setCity(dto.city()); 
        address.setProvince(dto.province()); 
        address.setCountry(dto.country() == null || dto.country().isBlank() ? "Canada" : dto.country()); 
        address.setPostalCode(dto.postalCode()); 
        address.setPhone(dto.phone()); 
        addressDAO.save(address); 
    } 

    private static AddressDto toDto(Address a) { 
        return new AddressDto(a.getStreet(), a.getCity(), a.getProvince(), a.getCountry(), a.getPostalCode(), a.getPhone()); 
    } 
} 
