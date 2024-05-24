package com.example.application.provider;

import com.example.application.data.AlunoRepository;
import com.example.application.data.GestorRepository;
import com.example.application.entity.Aluno;
import com.example.application.entity.Gestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private GestorRepository gestorRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Aluno validacaoUsuario = alunoRepository.validarLogin(authentication.getName(), (String) authentication.getCredentials());
        Gestor validacaoGestor = gestorRepository.validarLogin(authentication.getName(), (String) authentication.getCredentials());

        if (validacaoUsuario != null || validacaoGestor != null) {
            UserDetails user;
            user = validacaoUsuario != null?
                    User.withUsername(validacaoUsuario.getEmail())
                            .password(validacaoUsuario.getSenha())
                            .authorities(Arrays.asList(validacaoUsuario.isPrimeiroLogin()
                                    ?new SimpleGrantedAuthority("ROLE_TROCAR_SENHA")
                                    :new SimpleGrantedAuthority("ROLE_ALUNO")))
                            .build():
                    User.withUsername(validacaoGestor.getEmail())
                            .password(validacaoGestor.getSenha())
                            .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_GESTOR")))
                            .build();;

            return new UsernamePasswordAuthenticationToken(user, authentication.getCredentials(), user.getAuthorities());
        } else {
            throw new AuthenticationException("Usuário ou senha inválidos") {};
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
