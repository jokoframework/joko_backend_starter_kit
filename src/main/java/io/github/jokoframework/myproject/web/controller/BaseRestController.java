package io.github.jokoframework.myproject.web.controller;

import io.github.jokoframework.myproject.constants.ApiPaths;
import io.github.jokoframework.myproject.web.response.HeartBeatResponseDTO;
import io.github.jokoframework.myproject.web.session.SessionManager;
import io.github.jokoframework.myproject.web.session.UserCurrentInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 
 * @author bsandoval
 *
 */
public abstract class BaseRestController {
    
    public static final String JOKO_STARTER_KIT_VERSION_HEADER = "X-JOKO-STARTER-KIT-VERSION";
    public static final String JOKO_STARTER_KIT_VERSION = "1.0";
    
    @RequestMapping(value = ApiPaths.ROOT_DIAGNOSTIC + ApiPaths.SUFFIX_HEART_BEAT, method = RequestMethod.GET)
    public ResponseEntity<HeartBeatResponseDTO> getHearbeat() {
        HeartBeatResponseDTO heartBeatResponseDTO = getHeartBeatStatus();
        return new ResponseEntity<>(heartBeatResponseDTO, heartBeatResponseDTO.getHttpStatus());
    }

    public HeartBeatResponseDTO getHeartBeatStatus() {
        String result = String.format("Service status: %s, date: %s", "OK", new Date());
        
        return HeartBeatResponseDTO.builder()
            .success(true)
            .message(result)
            .httpStatus(HttpStatus.OK)
            .build();
    }
    
    public UserCurrentInfo getLocalCurrentUserInfo(SessionManager sm) {
		UserCurrentInfo user = sm.getUser();
        return user;
	}

	protected void logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null){    
	        new SecurityContextLogoutHandler().logout(request, response, auth);
	    }
	}
}
