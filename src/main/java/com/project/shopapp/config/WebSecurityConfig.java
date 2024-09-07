package com.project.shopapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    // cái này coi có quyền chi vô k
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
     http
                .csrf(AbstractHttpConfigurer::disable)
             .authorizeHttpRequests(request->{
                 request.requestMatchers("**")
                         .permitAll();
             }) ;
     return http.build();
    }

}
