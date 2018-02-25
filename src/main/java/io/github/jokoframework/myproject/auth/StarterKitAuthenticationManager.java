package io.github.jokoframework.myproject.auth;

import io.github.jokoframework.security.api.JokoAuthentication;
import io.github.jokoframework.security.api.JokoAuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Created by danicricco on 2/21/18.
 */
@Component
public class StarterKitAuthenticationManager implements JokoAuthenticationManager {
    private static final String DEFAULT_PIN = "123456";

    @Override
    public JokoAuthentication authenticate(JokoAuthentication authentication) throws AuthenticationException {
        //FIXME reemplazar por codigo real de autenticacion
        if (DEFAULT_PIN.equals(authentication.getPassword())) {
            authentication.setAuthenticated(true);
            authentication.addRole("boss");
        } else {
            authentication.setAuthenticated(false);
        }

        return authentication;
    }
}
