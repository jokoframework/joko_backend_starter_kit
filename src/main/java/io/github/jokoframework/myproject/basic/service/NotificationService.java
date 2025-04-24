package io.github.jokoframework.myproject.basic.service;

import io.github.jokoframework.myproject.basic.dto.NotificationResponseDTO;
import io.github.jokoframework.myproject.basic.entities.NotificationEntity;
import io.github.jokoframework.myproject.exceptions.NotificationException;

import java.util.List;

/**
 * Methods for notification management
 * 
 * @author copilot
 */
public interface NotificationService {
    
    /**
     * Get user notifications
     *
     * @param userId the user id
     * @return the notification response DTO
     */
    NotificationResponseDTO getUserNotifications(String userId);

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
     * Mark a notification as read
     *
     * @param notificationId the notification id
     * @return the updated notification
     * @throws NotificationException if notification is not found
     */
    NotificationEntity markAsRead(Long notificationId) throws NotificationException;
    
    /**
     * Delete a notification
     *
     * @param notificationId the notification id
     * @throws NotificationException if notification is not found
     */
    void delete(Long notificationId) throws NotificationException;
}