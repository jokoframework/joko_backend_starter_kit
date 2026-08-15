package io.github.jokoframework.myproject.basic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO for notification type data
 *
 * @author FedeTraversi
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationTypeDTO {
    private String name;
    private String category;
    private String channel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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