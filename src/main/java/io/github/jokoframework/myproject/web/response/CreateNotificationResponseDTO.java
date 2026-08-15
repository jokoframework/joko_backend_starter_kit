package io.github.jokoframework.myproject.web.response;

import io.github.jokoframework.myproject.basic.dto.NotificationDTO;

public class CreateNotificationResponseDTO extends ServiceResponseDTO {
    private NotificationDTO data;

    public NotificationDTO getData() {
        return data;
    }

    public void setData(NotificationDTO data) {
        this.data = data;
    }
}