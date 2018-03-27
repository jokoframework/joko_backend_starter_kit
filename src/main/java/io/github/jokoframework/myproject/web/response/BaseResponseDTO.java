package io.github.jokoframework.myproject.web.response;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.http.HttpStatus;

/**
 * 
 * @author bsandoval
 *
 */
public class BaseResponseDTO {
    private boolean success;
    private String errorCode;
    private String message;
    private HttpStatus httpStatus;
    
    public BaseResponseDTO() {
        super();
    }
    
    public BaseResponseDTO(boolean success) {
        this.success = success;
    }
    
    public BaseResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    protected BaseResponseDTO(Builder<?> builder) {
        this.success = builder.success;
        this.errorCode = builder.errorCode;
        this.message = builder.message;
        this.httpStatus = builder.httpStatus;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public static BaseResponseDTO error() {
        BaseResponseDTO error = new BaseResponseDTO();
        error.setSuccess(false);
        return error;

    }

    public static BaseResponseDTO ok() {
        BaseResponseDTO ok = new BaseResponseDTO();
        ok.setSuccess(true);
        return ok;
    }
    
    public static Builder<?> builder() {
        return new BaseBuilder();
    }
    
    protected static class Builder<T> {  
        protected final Class<T> clazz;
        
        private boolean success;
        private String errorCode;
        private String message;
        private HttpStatus httpStatus;
        
        protected Builder(Class<T> clazz) {
            this.clazz = clazz;
        }
        
        public T success(boolean success) {
            this.success = success;
            return clazz.cast(this);
        }
        
        public T errorCode(String errorCode) {
            this.errorCode = errorCode;
            return clazz.cast(this);
        }
        
        public T message(String message) {
            this.message = message;
            return clazz.cast(this);
        }
        
        public T httpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return clazz.cast(this);
        }
        
        public BaseResponseDTO build() {
            return new BaseResponseDTO(this);
        }
    }

    public static class BaseBuilder extends Builder<BaseBuilder> {
        protected BaseBuilder() {
            super(BaseBuilder.class);
        }
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("success", success)
                .append("errorCode", errorCode)
                .append("message", message)
                .append("httpStatus", httpStatus != null ? httpStatus.value() : null)
                .toString();
    }
}
