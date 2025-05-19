package io.github.jokoframework.myproject.exceptions;

import io.github.jokoframework.common.errors.BusinessException;

/**
 * Excepciones específicas para el manejo de notificaciones
 * 
 * @author FedeTraversi
 */
public class NotificationException extends BusinessException {
    private static final long serialVersionUID = 1L;
    public static final String NOTIFICATION_ERROR = "notification.error";
    public static final String NOTIFICATION_NOT_FOUND = NOTIFICATION_ERROR + ".notFound";
    public static final String NOTIFICATION_NOT_OWNED = NOTIFICATION_ERROR + ".notOwned";

    public NotificationException(String errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Construye una excepción para notificación no encontrada o no perteneciente al usuario
     *
     * @param notificationId id de la notificación
     * @param userId id del usuario
     * @return NotificationException
     */
    public static NotificationException notFoundOrNotOwned(Long notificationId, Long userId) {
        return new NotificationException(NOTIFICATION_NOT_FOUND, 
            String.format("La notificación %d no existe o no pertenece al usuario %d", notificationId, userId));
    }
}