package io.github.jokoframework.myproject.auth;

import io.github.jokoframework.myproject.exceptions.JokoAuthenticationException;
import io.github.jokoframework.myproject.exceptions.UserException;
import io.github.jokoframework.myproject.profile.manager.UserManager;
import io.github.jokoframework.myproject.security.CustomAuthenticationDetails;
import io.github.jokoframework.myproject.web.dto.UserAuthDTO;
import io.github.jokoframework.security.api.JokoAuthentication;
import io.github.jokoframework.security.api.JokoAuthenticationManager;
import io.github.jokoframework.security.dto.request.AuditSessionRequestDTO;
import io.github.jokoframework.security.dto.request.PrincipalSessionRequestDTO;
import io.github.jokoframework.security.services.IAuditSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *
 * @author bsandoval
 *
 */
@Component
public class AuthenticationManagerImpl implements JokoAuthenticationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationManagerImpl.class);

    public static final String AUDIT_APP_DESCRIPTION = "user_id";
    public static final String AUDIT_USER_DESCRIPTION = "joko_starter_kit";
    public static final String AUDIT_APP_DEFAULT_ID = "1";

    @Autowired
    private UserManager userManager;

    @Autowired
    private IAuditSessionService auditSessionService;

    @Override
    public JokoAuthentication authenticate(JokoAuthentication authentication)
            throws AuthenticationException {
        try {
            UserAuthDTO user = userManager.checkUser(authentication.getUsername(), authentication.getPassword());
            authentication.setAuthenticated(true);
            authentication.addRole(user.getProfile());
            auditPrincipalSession(authentication, user);
            return authentication;
        } catch (UserException e) {
            LOGGER.error(e.getMessage(), e);
            throw new JokoAuthenticationException(e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new JokoAuthenticationException("Error executing authentication", e);
        }
    }

    private void auditPrincipalSession(JokoAuthentication request, UserAuthDTO result){
        AuditSessionRequestDTO auditRequest = new AuditSessionRequestDTO();
        PrincipalSessionRequestDTO principalRequest = new PrincipalSessionRequestDTO();
        auditRequest.setRemoteIp((String) request.getCustom(CustomAuthenticationDetails.IP_ADDRESS));
        auditRequest.setUserAgent((String) request.getCustom(CustomAuthenticationDetails.USER_AGENT));
        auditRequest.setUserDate((Date) request.getCustom(CustomAuthenticationDetails.USER_DATE));
        principalRequest.setAppId(AUDIT_APP_DEFAULT_ID);
        principalRequest.setAppDescription(AUDIT_USER_DESCRIPTION);
        principalRequest.setUserId(String.valueOf(result.getId()));
        principalRequest.setUserDescription(AUDIT_APP_DESCRIPTION);
        auditRequest.setPrincipal(principalRequest);
        auditSessionService.save(auditRequest);
    }

}
