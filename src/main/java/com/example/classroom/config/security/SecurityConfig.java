package com.example.classroom.config.security;

import com.example.classroom.config.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

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
                //TODO - enable csrf
//                .csrf().disable()
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
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl(SIGN_OUT_API)
                .addLogoutHandler(logoutHandler)
//                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl(HOME_PAGE);
    }
}

