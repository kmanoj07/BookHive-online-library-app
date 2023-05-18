package com.kumarmanoj.bookhive;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

// Controlling the authentication mechanism
@Configuration
public class SecurityAdapter extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests( a-> a
                        // .antMatchers("/", "/error").permitAll()
                        .anyRequest().permitAll()
                )
                .exceptionHandling( e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .csrf(c->c
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .logout(l->l
                        .logoutSuccessUrl("/").permitAll()
                )
                .oauth2Login();
    }
}
