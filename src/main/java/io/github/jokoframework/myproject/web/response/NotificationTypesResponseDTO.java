package io.github.jokoframework.myproject.web.response;

import io.github.jokoframework.myproject.basic.dto.NotificationTypeDTO;
import java.util.List;

public class NotificationTypesResponseDTO extends ServiceResponseDTO {
    private List<NotificationTypeDTO> types;

    public List<NotificationTypeDTO> getTypes() {
        return types;
    }

    public void setTypes(List<NotificationTypeDTO> types) {
        this.types = types;
    }
}