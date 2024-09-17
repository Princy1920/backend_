package com.indium.match_app.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/error").permitAll()  // Allow access to login page and error page
                        .requestMatchers("/api/**").authenticated()  // Secure API endpoints
                        .anyRequest().authenticated()  // Secure all other requests
                )
                .formLogin(form -> form
                        .loginPage("/login")  // Custom login page
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("{\"message\": \"Login successful\"}");
                            response.getWriter().flush();
                        })
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable())  // Disable CSRF protection for APIs
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }
}
