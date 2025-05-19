package io.github.jokoframework.myproject.basic.enums;

/**
 * Enum para los tipos de notificaciones disponibles en el sistema
 * 
 * @author FedeTraversi
 */
public enum NotificationTypeEnum {
    INFO("info", "system"),
    WARNING("warning", "system"),
    ERROR("error", "system"),
    SUCCESS("success", "system"),
    EMAIL("info", "email"),
    PUSH("info", "push"),
    SMS("info", "sms");

    private final String category;
    private final String channel;

    NotificationTypeEnum(String category, String channel) {
        this.category = category;
        this.channel = channel;
    }

    public String getCategory() {
        return category;
    }

    public String getChannel() {
        return channel;
    }
}