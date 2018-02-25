package io.github.jokoframework.myproject.auth;

import io.github.jokoframework.security.JokoJWTClaims;
import io.github.jokoframework.security.api.JokoAuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import io.github.jokoframework.myproject.web.APIPaths;

import java.util.Collection;

/**
 * Created by danicricco on 2/21/18.
 */
@Component
public class StarterKitAuthorizationManager implements JokoAuthorizationManager {
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/index.html").permitAll()
                .antMatchers("/swagger/**").permitAll()
                .antMatchers("/api-docs/**").permitAll()
                .antMatchers(APIPaths.COUNTRIES).permitAll();

    }

    @Override
    public Collection<? extends GrantedAuthority> authorize(JokoJWTClaims claims, Collection<? extends GrantedAuthority> authorization) {
        return authorization;
    }
}
