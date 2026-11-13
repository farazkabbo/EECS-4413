package ca.yorku.eecs4413.fritzev.service.account; 

import ca.yorku.eecs4413.fritzev.controller.dto.*; 
import ca.yorku.eecs4413.fritzev.dao.*; 
import ca.yorku.eecs4413.fritzev.domain.*; 
import ca.yorku.eecs4413.fritzev.exception.*; 

import java.math.BigDecimal; 
import java.time.*; 
import java.util.*; 

// M4 Account Manager (D1 interfaces IAuth + IAccount). 
public interface AccountService { 

    // UC-01 
    UserDto register(RegisterRequest request); 

    // UC-02 
    User signIn(LoginRequest request); 

    // UC-02 
    UserDto getProfile(Long userId); 

    // UC-14 
    AddressDto getDefaultShipping(Long userId); 

    // UC-15 
    boolean isVerifiedOwner(Long userId); 
} 
