package io.github.jokoframework.myproject.basic.service.impl;

import io.github.jokoframework.common.errors.BusinessException;
import io.github.jokoframework.myproject.basic.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * @author bsandoval
 */
@Service
@Transactional(rollbackFor=BusinessException.class)
public class MessageServiceImpl implements MessageService {
    
    public static final String DEFAULT_ERROR_MESSAGE = "Error procesing your request";

    @Autowired
    private MessageSource messageSource;

    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    @Override
    public String getMessage(String id) {
        return messageSource.getMessage(id, null, DEFAULT_ERROR_MESSAGE, getLocale());
    }

    @Override
    public String getMessage(String id, Object[] params) {
        return messageSource.getMessage(id, params, DEFAULT_ERROR_MESSAGE, getLocale());
    }
}
