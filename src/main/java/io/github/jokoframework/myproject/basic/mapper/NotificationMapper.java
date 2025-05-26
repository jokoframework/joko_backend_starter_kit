package io.github.jokoframework.myproject.basic.mapper;

import io.github.jokoframework.myproject.basic.dto.NotificationDTO;
import io.github.jokoframework.myproject.basic.entities.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    
    public NotificationDTO toDTO(NotificationEntity entity) {
        if (entity == null) {
            return null;
        }
        
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