package io.github.jokoframework.myproject.exceptions;

import io.github.jokoframework.common.errors.BusinessException;

/**
 * 
 * @author bsandoval
 *
 */
public class UserException extends BusinessException {
	private static final long serialVersionUID = 1L;
    public static final String USER_ERROR = "user.error";
    public static final String USER_NOT_FOUND = USER_ERROR + ".notFound";
    public static final String USER_INVALID = USER_ERROR + ".invalid";

    public UserException(String errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Builds a User Not Found Exception
     *
     * @param userId user name
     * @return userException
     */
    public static UserException notFound(Long userId) {
        return new UserException(USER_NOT_FOUND, String.format("User %s not found" , userId));
    }

    public static UserException userNotFound(String username) {
        return new UserException(USER_NOT_FOUND, String.format("User %s not found" , username));
    }
    
    public static UserException invalidUserPassword() {
        return new UserException(USER_INVALID, "Invalid user or password ");
    }
}
