package com.neerajbisht.projects.AirBnB.security;


import com.neerajbisht.projects.AirBnB.entity.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.nio.file.AccessDeniedException;

import static com.neerajbisht.projects.AirBnB.entity.enums.Role.HOTEL_MANAGER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomJwtSecurityFilterChain customJwtSecurityFilterChain;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(customJwtSecurityFilterChain, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth->
                        auth
                                .requestMatchers("/admin/**").hasRole(HOTEL_MANAGER.name())
                                .requestMatchers("/bookings/**").authenticated()
                                .anyRequest().permitAll())
                .exceptionHandling(exception-> exception.accessDeniedHandler(accessDeniedException()));
        return http.build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedException(){
        return (request,response,accessDeniedException)->{
            handlerExceptionResolver.resolveException(request, response, null,accessDeniedException);
        };
    }
}
