package io.github.jokoframework.myproject.basic.service.impl;

import io.github.jokoframework.common.errors.BusinessException;
import io.github.jokoframework.myproject.basic.dto.NotificationDTO;
import io.github.jokoframework.myproject.basic.dto.NotificationResponseDTO;
import io.github.jokoframework.myproject.basic.entities.NotificationEntity;
import io.github.jokoframework.myproject.basic.repositories.NotificationRepository;
import io.github.jokoframework.myproject.basic.service.NotificationService;
import io.github.jokoframework.myproject.exceptions.NotificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Notification Service implementation
 * Created by FedeTraversi on 4/16/25.
 * Implementación del servicio de notificaciones.
 * Esta clase se encarga de generar y gestionar notificaciones aleatorias para los usuarios.
 */
@Service
@Transactional(rollbackFor=BusinessException.class)
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository repository;

    /** Categorías posibles para las notificaciones */
    private static final String[] CATEGORIES = {"info", "alert", "warning"};
    /** Canales disponibles para enviar notificaciones */
    private static final String[] CHANNELS = {"app_updates", "security", "system"};
    /** Títulos predefinidos para las notificaciones */
    private static final String[] TITLES = {
            "Sistema actualizado",
            "Alerta de seguridad",
            "Mantenimiento programado",
            "Nueva funcionalidad disponible",
            "Backup completado"
    };
    /** Mensajes predefinidos para el cuerpo de las notificaciones */
    private static final String[] MESSAGES = {
            "Se ha completado una actualización importante del sistema.",
            "Se detectó un intento de acceso no autorizado.",
            "El sistema entrará en mantenimiento en 30 minutos.",
            "Se han agregado nuevas características a la plataforma.",
            "Se ha completado el respaldo de datos programado."
    };

    @Override
    public NotificationEntity create(NotificationEntity notification) {
        notification.setCreatedDate(new Date());
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
        notification.setReadDate(new Date());
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
     * Obtiene las notificaciones para un usuario específico
     * @param userId ID del usuario que solicita las notificaciones
     * @return NotificationResponseDTO con la lista de notificaciones y metadatos
     */
    @Override
    public NotificationResponseDTO getUserNotifications(String userId) {
        // Genera notificaciones aleatorias y obtiene el timestamp actual
        List<NotificationDTO> notifications = generateRandomNotifications();
        String timestamp = Instant.now().toString();
        
        // Construye la respuesta con las notificaciones
        NotificationResponseDTO response = new NotificationResponseDTO();
        response.setSuccess(true);
        response.setMessage("Notificaciones recuperadas exitosamente");
        response.setData(notifications);
        
        // Agrega metadatos a la respuesta
        NotificationResponseDTO.MetadataInfo metadata = new NotificationResponseDTO.MetadataInfo();
        metadata.setTotal(notifications.size());
        metadata.setTimestamp(timestamp);
        response.setMetadata(metadata);
        
        return response;
    }

    /**
     * Genera un número aleatorio de notificaciones (entre 1 y 5)
     * con datos aleatorios tomados de los arrays predefinidos
     * @return Lista de NotificationDTO generadas aleatoriamente
     */
    private List<NotificationDTO> generateRandomNotifications() {
        Random random = new Random();
        int count = random.nextInt(5) + 1; // Genera entre 1 y 5 notificaciones
        List<NotificationDTO> notifications = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int index = random.nextInt(TITLES.length);
            NotificationDTO notification = new NotificationDTO();
            notification.setId(UUID.randomUUID().toString());
            notification.setTitle(TITLES[index]);
            notification.setBody(MESSAGES[index]);
            notification.setCategory(CATEGORIES[random.nextInt(CATEGORIES.length)]);
            notification.setTimestamp(Instant.now().toString());
            notification.setChannel(CHANNELS[random.nextInt(CHANNELS.length)]);
            notification.setRead(false);
            notifications.add(notification);
        }

        return notifications;
    }
}