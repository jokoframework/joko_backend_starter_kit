package io.github.jokoframework.myproject.basic.service;

/**
 * Get messages from properties
 *
 * @author bsandoval
 */
public interface MessageService {
    String getMessage(String id);
    String getMessage(String id, Object[] params);
}
