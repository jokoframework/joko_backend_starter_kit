package io.github.jokoframework.myproject.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.jokoframework.myproject.profile.dto.UserDTO;

import java.util.List;

/**
 * Users response
 *
 * @author bsandoval
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsersResponseDTO extends ServiceResponseDTO {

    @JsonProperty
    private List<UserDTO> users;
    
    @JsonProperty
    private byte[] csv;
    
    public UsersResponseDTO(List<UserDTO> users) {
        this.users = users;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }

    public byte[] getCsv() {
        return csv;
    }

    public void setCsv(byte[] csv) {
        this.csv = csv;
    }
    
}
