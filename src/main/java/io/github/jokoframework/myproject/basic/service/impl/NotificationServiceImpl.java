package io.github.jokoframework.myproject.basic.service.impl;

import io.github.jokoframework.common.errors.BusinessException;
import io.github.jokoframework.myproject.basic.dto.NotificationDTO;
import io.github.jokoframework.myproject.basic.dto.NotificationResponseDTO;
import io.github.jokoframework.myproject.basic.dto.NotificationTypeDTO;
import io.github.jokoframework.myproject.basic.entities.NotificationEntity;
import io.github.jokoframework.myproject.basic.enums.NotificationTypeEnum;
import io.github.jokoframework.myproject.basic.repositories.NotificationRepository;
import io.github.jokoframework.myproject.basic.service.NotificationService;
import io.github.jokoframework.myproject.exceptions.NotificationException;
import io.github.jokoframework.myproject.basic.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Notification Service implementation
 * Created by FedeTraversi on 4/16/25.
 * Implementación del servicio de notificaciones.
 */
@Service
@Transactional(rollbackFor=BusinessException.class)
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository repository;
    
    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public NotificationEntity create(NotificationEntity notification) {
        notification.setCreatedDate(Date.from(Instant.now()));
        notification.setIsRead(false);
        return repository.save(notification);
    }

    @Override
    public List<NotificationEntity> findByUser(Long userId) {
        return repository.findByUserIdOrderByCreatedDateDesc(userId);
    }

    @Override
    public List<NotificationEntity> findByUserAndReadStatus(Long userId, Boolean isRead) {
        return repository.findByUserIdAndIsReadOrderByCreatedDateDesc(userId, isRead);
    }

    @Override
    public void deleteNotification(Long notificationId, Long userId) throws NotificationException {
        Optional<NotificationEntity> notification = repository.findByIdAndUserId(notificationId, userId);
        if (!notification.isPresent()) {
            throw NotificationException.notFoundOrNotOwned(notificationId, userId);
        }
        repository.deleteByIdAndUserId(notificationId, userId);
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) throws NotificationException {
        Optional<NotificationEntity> optionalNotification = repository.findByIdAndUserId(notificationId, userId);
        if (!optionalNotification.isPresent()) {
            throw NotificationException.notFoundOrNotOwned(notificationId, userId);
        }
        NotificationEntity notification = optionalNotification.get();
        notification.setIsRead(true);
        notification.setReadDate(Date.from(Instant.now()));
        repository.save(notification);
    }

    /**
     * Obtiene las notificaciones para un usuario específico desde la base de datos
     * @param userId ID del usuario que solicita las notificaciones
     * @return NotificationResponseDTO con la lista de notificaciones y metadatos
     */
    @Override
    public NotificationResponseDTO getUserNotifications(Long userId) {
        List<NotificationEntity> entities = findByUser(userId);
        List<NotificationDTO> notifications = entities.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
        
        String timestamp = Instant.now().toString();
        
        NotificationResponseDTO response = new NotificationResponseDTO();
        response.setSuccess(true);
        response.setMessage("Notificaciones recuperadas exitosamente");
        response.setData(notifications);
        
        NotificationResponseDTO.MetadataInfo metadata = new NotificationResponseDTO.MetadataInfo();
        metadata.setTotal(notifications.size());
        metadata.setTimestamp(timestamp);
        response.setMetadata(metadata);
        
        return response;
    }

    @Override
    public List<NotificationTypeDTO> getNotificationTypes() {
        return Arrays.stream(NotificationTypeEnum.values())
                .map(type -> {
                    NotificationTypeDTO dto = new NotificationTypeDTO();
                    dto.setName(type.name());
                    dto.setCategory(type.getCategory());
                    dto.setChannel(type.getChannel());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}