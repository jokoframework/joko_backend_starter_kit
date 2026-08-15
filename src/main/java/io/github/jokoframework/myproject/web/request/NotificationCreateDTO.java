package io.github.jokoframework.myproject.web.request;

import javax.validation.constraints.NotBlank;

/**
 * DTO para la creación de notificaciones
 * 
 * @author FedeTraversi
 */
public class NotificationCreateDTO {

    @NotBlank(message = "El título es requerido")
    private String title;

    @NotBlank(message = "El mensaje es requerido")
    private String message;

    @NotBlank(message = "La categoría es requerida")
    private String category;

    @NotBlank(message = "El canal es requerido")
    private String channel;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}