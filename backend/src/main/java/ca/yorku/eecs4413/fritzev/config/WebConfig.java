package ca.yorku.eecs4413.fritzev.config; 

import org.springframework.beans.factory.annotation.Value; 
import org.springframework.context.annotation.Configuration; 
import org.springframework.core.convert.converter.Converter; 
import org.springframework.core.convert.converter.ConverterFactory; 
import org.springframework.format.FormatterRegistry; 

import java.util.Locale; 
import org.springframework.web.servlet.config.annotation.CorsRegistry; 
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; 

// Lets the React front end on another origin call /api/** with the session cookie. 
@Configuration 
public class WebConfig implements WebMvcConfigurer { 

    private final String[] allowedOrigins; 

    public WebConfig(@Value("${app.cors.allowed-origins}") String allowedOrigins) { 
        this.allowedOrigins = allowedOrigins.split("\\s*,\\s*"); 
    } 

    // Lets query parameters like ?shape=suv or ?type=at_home bind to the upper-case enums. 
    @Override 
    @SuppressWarnings({"unchecked", "rawtypes"}) 
    public void addFormatters(FormatterRegistry registry) { 
        registry.addConverterFactory(new ConverterFactory<String, Enum>() { 
            @Override 
            public <T extends Enum> Converter<String, T> getConverter(Class<T> type) { 
                return source -> source.isBlank() ? null 
                        : (T) Enum.valueOf(type, source.trim().toUpperCase(Locale.ROOT)); 
            } 
        }); 
    } 

    @Override 
    public void addCorsMappings(CorsRegistry registry) { 
        registry.addMapping("/api/**") 
                .allowedOrigins(allowedOrigins) 
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") 
                .allowCredentials(true) 
                .maxAge(3600); 
    } 
} 
