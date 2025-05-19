package io.github.jokoframework.myproject.web.controller;

import io.github.jokoframework.common.errors.BusinessException;
import io.github.jokoframework.common.errors.JokoApplicationException;
import io.github.jokoframework.security.controller.SecurityConstants;
import io.github.jokoframework.security.dto.JokoTokenResponse;
import io.github.jokoframework.security.errors.JokoUnauthenticatedException;
import io.github.jokoframework.security.errors.JokoUnauthorizedException;
import io.github.jokoframework.myproject.basic.service.MessageService;
import io.github.jokoframework.myproject.web.response.ServiceResponseDTO;
import io.github.jokoframework.myproject.exceptions.JokoAuthenticationException;
import io.github.jokoframework.myproject.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;

/**
 * 
 * @author bsandoval
 *
 */
@ControllerAdvice
public class ErrorController extends BaseRestController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorController.class);

	@Autowired
	private MessageService messages;

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ServiceResponseDTO> onBusinessError(BusinessException error) {
		HttpStatus status = HttpStatus.CONFLICT;
		ServiceResponseDTO response = new ServiceResponseDTO();
		response.setSuccess(false);
        response.setMessage(error.getMessage());
        response.setErrorCode(error.getErrorCode());

		 // Add specific handling for authentication errors
		if (error.getErrorCode().equals("user.error.invalid")) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (error.getErrorCode().endsWith("notFound")) {
            status = HttpStatus.NOT_FOUND;
        }


		response.setUserMessage(messages.getMessage(error.getErrorCode()));

		// Se loguea en trace porque una excepcion en regla de negocio no es
		// algo importante. Es algo que el usuario debe corregir
		LOGGER.trace(error.getMessage(),error);

		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler({ JokoUnauthenticatedException.class })
	public ResponseEntity<JokoTokenResponse> onAuthenticationException(JokoUnauthenticatedException ex) {
		JokoTokenResponse res = new JokoTokenResponse(SecurityConstants.ERROR_BAD_CREDENTIALS);
		res.setMessage(ex.getMessage());
		res.setSuccess(false);
		LOGGER.trace(ex.getMessage(), ex);
		return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler({ JokoUnauthorizedException.class })
    public ResponseEntity<JokoTokenResponse> onAuthorizationException(JokoUnauthorizedException ex) {
        JokoTokenResponse res = new JokoTokenResponse(SecurityConstants.ERROR_BAD_CREDENTIALS);
        res.setMessage(ex.getMessage());
        res.setSuccess(false);
        LOGGER.trace(ex.getMessage(), ex);
        return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
    }

	@ExceptionHandler({ RuntimeException.class })
	public ResponseEntity<ServiceResponseDTO> onAuthenticationException(RuntimeException ex) {
		ServiceResponseDTO response = new ServiceResponseDTO();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		
		if (ex instanceof JokoApplicationException) {
			Throwable cause = ex.getCause();
			if (cause instanceof JokoAuthenticationException)
			{ 
				Throwable cause2 = cause.getCause();
				if (cause2 instanceof UserException) {
					UserException userEx = (UserException) cause2;
					response.setErrorCode(userEx.getErrorCode());
					status = HttpStatus.UNAUTHORIZED;
					response.setMessage(userEx.getMessage());
				} else {
					response.setErrorCode("authentication.error");
					status = HttpStatus.BAD_REQUEST;
				}
			}
		}
		
		response.setMessage(ex.getMessage());
		response.setSuccess(false);
		LOGGER.error(ex.getMessage(), ex);

		return new ResponseEntity<>(response, status);
	}
	
	@ExceptionHandler({ ResourceAccessException.class,
			ResourceAccessException.class })
	public ResponseEntity<ServiceResponseDTO> onUnavailableException(ResourceAccessException e) {
		LOGGER.error("Unable to call external service", e);
		ServiceResponseDTO response = new ServiceResponseDTO();
		//it doesn't include the message for not to reveal internal information of the server
		response.setMessage("Service NOT available. We hope that it is temporarly");
		response.setSuccess(false);
		return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
	}

}
