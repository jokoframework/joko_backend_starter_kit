package io.github.jokoframework.myproject.auth;

import static io.github.jokoframework.myproject.basic.enums.AccessLevelEnum.ADMIN;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

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

@Component
public class AuthorizationManagerImpl implements JokoAuthorizationManager {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationManagerImpl.class);

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable());
        httpSecurity.headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
                .httpStrictTransportSecurity(hsts -> hsts.disable()));

        httpSecurity.httpBasic(basic -> basic.authenticationDetailsSource(authenticationDetailsSource()));

        httpSecurity.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/diagnostic/heartbeat",
                        ApiPaths.USERS_HEARTBEAT,
                        ApiPaths.PERSON_HEARTBEAT).permitAll()
                .requestMatchers(ApiPaths.COUNTRIES).permitAll()
                .requestMatchers(ApiPaths.NOTIFICATIONS_TYPE).permitAll()
                .requestMatchers(ApiPaths.API_SESSIONS).hasAnyAuthority(ADMIN.name())
                .requestMatchers(ApiPaths.NOTIFICATIONS_BY_USER,
                        ApiPaths.NOTIFICATIONS_USER,
                        ApiPaths.NOTIFICATIONS_USER_BY_ID,
                        ApiPaths.NOTIFICATIONS_USER_READ).hasAnyAuthority(ADMIN.name())
                .requestMatchers(ApiPaths.ROOT_USERS,
                        ApiPaths.USERS_HEARTBEAT,
                        ApiPaths.USERS_BY_NAME,
                        ApiPaths.USERS_CSV).hasAnyAuthority(ADMIN.name()));
    }

    @Override
    public Collection<? extends GrantedAuthority> authorize(JokoJWTClaims claims,
                                                            Collection<? extends GrantedAuthority> authorization) {
        return authorization;
    }

    private AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
        return new AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails>() {
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
