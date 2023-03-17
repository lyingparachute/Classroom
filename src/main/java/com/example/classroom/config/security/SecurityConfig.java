package com.example.classroom.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String HOME_PAGE = "/";
    private static final String DASHBOARD_PAGE = "/dashboard";
    private static final String SIGN_IN_PAGE = "/sign-in";
    private static final String SIGN_UP_PAGE = "/sign-up";
    private static final String SIGN_IN_API = "/api/sign-in";
    private static final String SIGN_OUT_API = "/api/sign-out";
    private static final String AUTH_ENDPOINTS = "/api/auth/**";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/assets/**", "/img/home/**", "/webjars/**",
                        HOME_PAGE, SIGN_IN_PAGE, SIGN_IN_API, SIGN_UP_PAGE, AUTH_ENDPOINTS).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage(SIGN_IN_PAGE)
                .loginProcessingUrl(SIGN_IN_API)
                .defaultSuccessUrl(DASHBOARD_PAGE, true)
                .failureUrl(SIGN_IN_PAGE + "?error")
                .and()
                .logout()
                .logoutUrl(SIGN_OUT_API)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl(HOME_PAGE);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password(encoder.encode("admin"))
                .roles("ADMIN")
                .and()
                .withUser("user")
                .password(encoder.encode("user"))
                .roles();
    }
}

