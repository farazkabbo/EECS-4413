package ca.yorku.eecs4413.fritzev.config; 

import org.springframework.context.annotation.Bean; 
import org.springframework.context.annotation.Configuration; 
import org.springframework.security.config.annotation.web.builders.HttpSecurity; 
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; 
import org.springframework.security.web.SecurityFilterChain; 
import org.springframework.security.web.csrf.CookieCsrfTokenRepository; 

@Configuration 
public class SecurityConfig { 
    @Bean 
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { 
        http 
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) 
            .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) 
            .formLogin(AbstractHttpConfigurer::disable) 
            .httpBasic(AbstractHttpConfigurer::disable); 
        return http.build(); 
    } 
} 
