package ca.yorku.eecs4413.fritzev.controller; 

import ca.yorku.eecs4413.fritzev.domain.Role; 
import ca.yorku.eecs4413.fritzev.domain.User; 
import ca.yorku.eecs4413.fritzev.exception.ForbiddenException; 
import ca.yorku.eecs4413.fritzev.exception.UnauthorizedException; 
import jakarta.servlet.http.HttpSession; 

// Tiny session-based authentication helper shared by every controller. 
public final class SessionAuth { 

    public static final String USER_ID = "FRITZ_USER_ID"; 
    public static final String ROLE = "FRITZ_ROLE"; 

    private SessionAuth() { } 

    // Call after the password was verified (UC-02). 
    public static void signIn(jakarta.servlet.http.HttpServletRequest request, User user) { 
        HttpSession old = request.getSession(false); 
        if (old != null) old.invalidate(); 
        HttpSession session = request.getSession(true); 
        signIn(session, user); 
    } 

    // Stores the signed-in user in the given session. 
    public static void signIn(HttpSession session, User user) { 
        session.setAttribute(USER_ID, user.getId()); 
        session.setAttribute(ROLE, user.getRole().name()); 
    } 

    // UC-03 
    public static void signOut(HttpSession session) { 
        if (session != null) session.invalidate(); 
    } 

    // The signed-in user's id, or null for an anonymous visitor. 
    public static Long currentUserId(HttpSession session) { 
        return session == null ? null : (Long) session.getAttribute(USER_ID); 
    } 

    // Throws 401 when nobody is signed in (checkout requires login). 
    public static Long requireUser(HttpSession session) { 
        Long id = currentUserId(session); 
        if (id == null) throw new UnauthorizedException("Sign-in required"); 
        return id; 
    } 

    // Throws 401 when anonymous and 403 when signed in without the ADMIN role (UC-17..UC-19). 
    public static Long requireAdmin(HttpSession session) { 
        Long id = requireUser(session); 
        if (!Role.ADMIN.name().equals(session.getAttribute(ROLE))) { 
            throw new ForbiddenException("Administrator role required"); 
        } 
        return id; 
    } 
} 
