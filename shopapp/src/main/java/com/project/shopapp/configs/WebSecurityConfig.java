package com.project.shopapp.configs;

import com.project.shopapp.components.JwtTokenFilter;
import com.project.shopapp.components.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        requests ->
                                requests.requestMatchers("api/v1/users/register", "api/v1/users/login").permitAll()
                                        .requestMatchers(HttpMethod.POST, "api/v1/orders/**").hasAnyRole("user","admin")
                                        .requestMatchers(HttpMethod.GET, "api/v1/orders/**").hasAnyRole("user", "admin")
                                        .requestMatchers(HttpMethod.PUT, "api/v1/orders/**").hasAnyRole("admin")
                                        .requestMatchers(HttpMethod.DELETE, "api/v1/orders/**").hasAnyRole("admin")
                );
        return http.build();
    }
}
