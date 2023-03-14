package com.example.classroom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String HOME_PAGE = "/";
    private static final String DASHBOARD_PAGE = "/dashboard";
    private static final String SIGN_IN_PAGE = "/sign-in";
    private static final String SIGN_IN_API = "/api/sign-in";
    private static final String SIGN_OUT_API = "/api/sign-out";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/assets/**", "/img/home/**", "/webjars/**",
                        HOME_PAGE,
                        SIGN_IN_PAGE,
                        SIGN_IN_API).permitAll()
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
}

