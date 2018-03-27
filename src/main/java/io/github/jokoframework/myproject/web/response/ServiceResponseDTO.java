package io.github.jokoframework.myproject.web.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.jokoframework.common.dto.JokoBaseResponse;

/**
 * Common response
 *
 * @author agimenez
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponseDTO extends JokoBaseResponse {

    @JsonProperty
    private String userMessage;

    public ServiceResponseDTO() {
        super(true);
    }

    public ServiceResponseDTO(String pUserMessage) {
        super(true);
        userMessage = pUserMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

}
