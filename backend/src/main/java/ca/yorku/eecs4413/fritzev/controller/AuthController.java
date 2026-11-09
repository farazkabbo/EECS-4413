package ca.yorku.eecs4413.fritzev.controller;

import ca.yorku.eecs4413.fritzev.controller.dto.LoginRequest;
import ca.yorku.eecs4413.fritzev.controller.dto.RegisterRequest;
import ca.yorku.eecs4413.fritzev.controller.dto.UserDto;
import ca.yorku.eecs4413.fritzev.domain.User;
import ca.yorku.eecs4413.fritzev.service.account.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// UC-01 Register, UC-02 Sign In, UC-03 Sign Out.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AccountService accountService;

    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody RegisterRequest request) {
        return accountService.register(request);
    }

    @PostMapping("/login")
    public UserDto login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        User user = accountService.signIn(request);
        SessionAuth.signIn(http, user);
        return UserDto.from(user);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest http) {
        SessionAuth.signOut(http.getSession(false));
    }

    @GetMapping("/me")
    public UserDto me(HttpSession session) {
        return accountService.getProfile(SessionAuth.requireUser(session));
    }
}
