package io.github.jokoframework.myproject.basic.service;

import io.github.jokoframework.myproject.basic.dto.NotificationResponseDTO;
import io.github.jokoframework.myproject.basic.dto.NotificationTypeDTO;
import io.github.jokoframework.myproject.basic.entities.NotificationEntity;
import io.github.jokoframework.myproject.exceptions.NotificationException;
import java.util.List;

/**
 * Methods for notification management
 * 
 * @author FedeTraversi
 */
public interface NotificationService {
    
    /**
     * Get user notifications
     *
     * @param userId the user id
     * @return the notification response DTO
     */
    NotificationResponseDTO getUserNotifications(Long userId);

    /**
     * Create a new notification
     *
     * @param notification the notification to create
     * @return the created notification
     */
    NotificationEntity create(NotificationEntity notification);
    
    /**
     * Get all notifications for a user ordered by creation date
     *
     * @param userId the user id
     * @return list of notifications
     */
    List<NotificationEntity> findByUser(Long userId);
    
    /**
     * Get notifications for a user filtered by read status
     *
     * @param userId the user id
     * @param isRead the read status to filter
     * @return list of notifications
     */
    List<NotificationEntity> findByUserAndReadStatus(Long userId, Boolean isRead);

    /**
     * Get all available notification types
     *
     * @return list of notification types
     */
    List<NotificationTypeDTO> getNotificationTypes();

    /**
     * Delete a notification by its ID
     *
     * @param notificationId the ID of the notification to delete
     * @param userId the ID of the user who owns the notification
     * @throws NotificationException if the notification doesn't exist or doesn't belong to the user
     */
    void deleteNotification(Long notificationId, Long userId) throws NotificationException;

    /**
     * Mark a notification as read
     *
     * @param notificationId the ID of the notification to mark as read
     * @param userId the ID of the user who owns the notification
     * @throws NotificationException if the notification doesn't exist or doesn't belong to the user
     */
    void markAsRead(Long notificationId, Long userId) throws NotificationException;
}