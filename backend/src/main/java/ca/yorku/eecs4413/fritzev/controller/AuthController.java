package ca.yorku.eecs4413.fritzev.controller;

import ca.yorku.eecs4413.fritzev.controller.dto.LoginRequest;
import ca.yorku.eecs4413.fritzev.controller.dto.RegisterRequest;
import ca.yorku.eecs4413.fritzev.controller.dto.UserDto;
import ca.yorku.eecs4413.fritzev.domain.User;
import ca.yorku.eecs4413.fritzev.service.account.AccountService;
import ca.yorku.eecs4413.fritzev.service.cart.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.web.csrf.CsrfToken;

// UC-01 Register, UC-02 Sign In, UC-03 Sign Out.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AccountService accountService;
    private final CartService cartService;

    public AuthController(AccountService accountService, CartService cartService) {
        this.accountService = accountService;
        this.cartService = cartService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody RegisterRequest request) {
        return accountService.register(request);
    }

    @PostMapping("/login")
    public UserDto login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        User user = accountService.signIn(request);
        HttpSession anonymous = http.getSession(false);
        String anonymousToken = anonymous == null ? null : anonymous.getId();
        SessionAuth.signIn(http, user);
        if (anonymousToken != null) cartService.mergeAnonymousCart(user.getId(), anonymousToken);
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

    @GetMapping("/csrf")
    public java.util.Map<String, String> csrf(CsrfToken token) {
        return java.util.Map.of("token", token.getToken());
    }
}
