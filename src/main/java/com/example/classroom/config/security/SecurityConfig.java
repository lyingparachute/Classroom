package com.example.classroom.config.security;

import com.example.classroom.config.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    private static final String HOME_PAGE = "/";
    private static final String DASHBOARD_PAGE = "/dashboard";
    private static final String SIGN_IN_PAGE = "/sign-in";
    private static final String SIGN_UP_PAGE = "/sign-up";
    private static final String SIGN_IN_API = "/api/sign-in";
    private static final String SIGN_OUT_API = "/api/sign-out";
    private static final String API_AUTH_ENDPOINTS = "/api/auth/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //TODO - enable csrf
                .csrf().disable()
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.POST, API_AUTH_ENDPOINTS).permitAll()
                        .requestMatchers(HOME_PAGE, "/css/**", "/js/**", "/assets/**", "/img/home/**", "/webjars/**",
                                SIGN_IN_PAGE, SIGN_IN_API, SIGN_UP_PAGE).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage(SIGN_IN_PAGE)
                        .loginProcessingUrl(SIGN_IN_API)
                        .defaultSuccessUrl(DASHBOARD_PAGE, true)
                        .failureUrl(SIGN_IN_PAGE + "?error")
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl(SIGN_OUT_API)
                        .addLogoutHandler(logoutHandler)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl(HOME_PAGE)
                );

        return http.build();
    }
}

