package io.github.jokoframework.myproject.exceptions;

import io.github.jokoframework.common.errors.BusinessException;

/**
 * Exception for notification related errors
 *
 * @author copilot
 */
public class NotificationException extends BusinessException {

    private static final long serialVersionUID = 1L;
    public static final String NOTIFICATION_ERROR = "notification.error";
    public static final String NOTIFICATION_NOT_FOUND = NOTIFICATION_ERROR + ".notFound";

    private NotificationException(String errorCode, String message) {
        super(errorCode, message);
    }

    public static NotificationException notFound(Long notificationId) {
        return new NotificationException(
            NOTIFICATION_NOT_FOUND, 
            String.format("Notification not found with id: %d", notificationId)
        );
    }
}