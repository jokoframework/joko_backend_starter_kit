package io.github.jokoframework.myproject.auth;

import static io.github.jokoframework.myproject.basic.enums.AccessLevelEnum.ADMIN;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import io.github.jokoframework.security.JokoJWTClaims;
import io.github.jokoframework.security.api.JokoAuthorizationManager;
import io.github.jokoframework.myproject.constants.ApiPaths;
import io.github.jokoframework.myproject.security.CustomAuthenticationDetails;

/**
 *
 * @author bsandoval
 *
 */
@Component
public class AuthorizationManagerImpl implements JokoAuthorizationManager {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationManagerImpl.class);

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .httpBasic()
                .authenticationDetailsSource(authenticationDetailsSource())
                .and()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/index.html").permitAll()
                .antMatchers("/swagger/**").permitAll()
                .antMatchers("/api-docs/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/**/heartbeat").permitAll()
                .antMatchers(ApiPaths.COUNTRIES).permitAll()
                .antMatchers(ApiPaths.API_SESSIONS).hasAnyAuthority(ADMIN.name())
                // Users
                .antMatchers(ApiPaths.ROOT_USERS,
                        ApiPaths.USERS_HEARTBEAT,
                        ApiPaths.USERS_BY_NAME,
                        ApiPaths.USERS_CSV).hasAnyAuthority(ADMIN.name());

        // Only in dev profile,
        // Allows X-Frame-Options headers sent by H2 console.
        // http://docs.spring.io/spring-security/site/docs/current/reference/html/headers.html
        httpSecurity
                .headers()
                .frameOptions().sameOrigin()
                .httpStrictTransportSecurity().disable();
    }

    @Override
    public Collection<? extends GrantedAuthority> authorize(JokoJWTClaims claims,
                                                            Collection<? extends GrantedAuthority> authorization) {
        return authorization;
    }

    private AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
        return new AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails>() {
            /**
             * We want to pass extra information to the authentication provider
             * such as the 'custom' parameter of the request in the login.
             *
             * Inside the provider you can access that information with:
             * authentication.getDetails()
             */
            @Override
            public WebAuthenticationDetails buildDetails(HttpServletRequest request) {
                CustomAuthenticationDetails details = new CustomAuthenticationDetails(request);
                details.addCustom(CustomAuthenticationDetails.USER_DATE, System.currentTimeMillis())
                        .addCustom(CustomAuthenticationDetails.IP_ADDRESS, getIp()
                                .orElse((String) details.getCustom().get(CustomAuthenticationDetails.HOST)));
                return details;
            }

            private Optional<String> getIp() {
                try {
                    InetAddress ip = InetAddress.getLocalHost();
                    return Optional.of(ip.getHostAddress());
                } catch (UnknownHostException e) {
                    log.error(e.getMessage(), e);
                }
                return Optional.empty();
            }

        };
    }
}
