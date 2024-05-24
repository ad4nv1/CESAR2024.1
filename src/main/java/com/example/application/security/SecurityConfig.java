package com.example.application.security;

import com.example.application.provider.CustomAuthenticationProvider;
import com.example.application.provider.CustomAuthenticationSuccessHandler;
import com.example.application.view.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            auth
                    .requestMatchers(new AntPathRequestMatcher("/images/*.png", "GET")).permitAll()
                    .requestMatchers("/aluno/**").hasAuthority("ROLE_ALUNO")
                    .requestMatchers("/gestor/**").hasAuthority("ROLE_GESTOR")
                    .anyRequest().authenticated();
        });

        http.formLogin()
                .loginPage("/login")
                .successHandler(customAuthenticationSuccessHandler())
                .permitAll();

        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider);
    }

    private AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }
}
