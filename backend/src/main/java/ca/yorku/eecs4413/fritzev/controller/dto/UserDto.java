package ca.yorku.eecs4413.fritzev.controller.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import ca.yorku.eecs4413.fritzev.domain.*;

// Public view of an account.
public record UserDto(
        Long id, String username, String email, String firstName, String lastName, Role role,
        boolean verifiedOwner) {

    public static UserDto from(User u) {
        return new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.getFirstName(), u.getLastName(),
                u.getRole(), u.isVerifiedOwner());
    }
}
