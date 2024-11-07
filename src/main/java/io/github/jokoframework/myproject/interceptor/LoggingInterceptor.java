package io.github.jokoframework.myproject.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);
    private final ObjectWriter objectWriter;

    public LoggingInterceptor(ObjectMapper objectMapper) {
        this.objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("Incoming request data: method={}, uri={}, headers={}, params={}",
                request.getMethod(),
                request.getRequestURI(),
                getHeadersInfo(request),
                request.getParameterMap());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        log.info("Outgoing response data: status={}, headers={}, body={}",
                response.getStatus(),
                response.getHeaderNames(),
                getResponseBody(responseWrapper));
        try {
            responseWrapper.copyBodyToResponse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getHeadersInfo(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.append(headerName).append("=").append(request.getHeader(headerName)).append(", ");
        }
        return headers.toString();
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        try {
            byte[] content = response.getContentAsByteArray();
            if (content.length > 0) {
                return objectWriter.writeValueAsString(new String(content, response.getCharacterEncoding()));
            }
        } catch (IOException e) {
            log.error("Failed to pretty print response body", e);
        }
        return "Response body pretty print not implemented";
    }
}