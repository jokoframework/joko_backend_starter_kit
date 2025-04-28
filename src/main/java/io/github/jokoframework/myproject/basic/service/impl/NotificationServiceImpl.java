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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
    public NotificationEntity markAsRead(Long notificationId) throws NotificationException {
        NotificationEntity notification = repository.findById(notificationId)
                .orElseThrow(() -> NotificationException.notFound(notificationId));
        
        notification.setIsRead(true);
        notification.setReadDate(Date.from(Instant.now()));
        return repository.save(notification);
    }

    @Override
    public void delete(Long notificationId) throws NotificationException {
        if (!repository.existsById(notificationId)) {
            throw NotificationException.notFound(notificationId);
        }
        repository.deleteById(notificationId);
    }

    /**
     * Obtiene las notificaciones para un usuario específico desde la base de datos
     * @param userId ID del usuario que solicita las notificaciones
     * @return NotificationResponseDTO con la lista de notificaciones y metadatos
     */
    @Override
    public NotificationResponseDTO getUserNotifications(String userId) {
        List<NotificationEntity> entities = findByUser(Long.parseLong(userId));
        List<NotificationDTO> notifications = entities.stream()
                .map(this::mapToDTO)
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

    /**
     * Mapea una entidad NotificationEntity a NotificationDTO
     */
    private NotificationDTO mapToDTO(NotificationEntity entity) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(entity.getId().toString());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setCategory(entity.getCategory());
        dto.setTimestamp(entity.getCreatedDate().toInstant().toString());
        dto.setChannel(entity.getChannel());
        dto.setRead(entity.getIsRead());
        return dto;
    }
}