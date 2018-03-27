package io.github.jokoframework.myproject.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.jokoframework.myproject.profile.dto.UserDTO;

/**
 * User response
 *
 * @author bsandoval
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDTO extends ServiceResponseDTO {

    @JsonProperty
    private UserDTO user;

    public UserResponseDTO(UserDTO user) {
        this.user = user;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
